package picklab.backend.member.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import picklab.backend.auth.infrastructure.AccessTokenProvider
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.file.entrypoint.response.CreatePresignedurlResponse
import picklab.backend.helper.extractBody
import picklab.backend.job.domain.JobCategoryRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.application.MemberUseCase
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.entity.MemberAgreement
import picklab.backend.member.domain.entity.NotificationPreference
import picklab.backend.member.domain.repository.InterestedJobCategoryRepository
import picklab.backend.member.domain.repository.MemberAgreementRepository
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.member.domain.repository.NotificationPreferenceRepository
import picklab.backend.template.IntegrationTest
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.LocalDate
import java.util.Base64

class MemberMeServiceTest(
    @Value("\${oci.cloud.end-point}") private val storageEndpoint: String,
    @Value("\${oci.storage.bucket-name}") private val bucketName: String,
) : IntegrationTest() {
    @Autowired
    lateinit var s3Client: S3Client

    @Autowired
    lateinit var accessTokenProvider: AccessTokenProvider

    @Autowired
    lateinit var memberUseCase: MemberUseCase

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var interestedJobCategoryRepository: InterestedJobCategoryRepository

    @Autowired
    lateinit var memberAgreementRepository: MemberAgreementRepository

    @Autowired
    lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    @Autowired
    lateinit var jobCategoryRepository: JobCategoryRepository

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Test
    @DisplayName("getMemberMe는 요청한 회원 필드와 직무 데이터를 반환한다")
    fun getMemberMe() {
        val member =
            memberRepository.save(
                Member(
                    name = "hong",
                    email = "test@example.com",
                    nickname = "picklab_member",
                    educationLevel = "대학교(4년)",
                    birthDate = LocalDate.of(1998, 3, 1),
                    employmentStatus = "재직 중",
                    company = "Picklab",
                ),
            )

        memberAgreementRepository.save(
            MemberAgreement(
                member = member,
                emailAgreement = true,
                privacyAgreement = true,
            ),
        )
        notificationPreferenceRepository.save(
            NotificationPreference(
                member = member,
                notifyPopularActivity = true,
                notifyBookmarkedActivity = false,
            ),
        )

        val backend =
            jobCategoryRepository.save(
                JobCategory(
                    jobGroup = JobGroup.DEVELOPMENT,
                    jobDetail = JobDetail.BACKEND,
                ),
            )
        val devops =
            jobCategoryRepository.save(
                JobCategory(
                    jobGroup = JobGroup.DEVELOPMENT,
                    jobDetail = JobDetail.DEVOPS,
                ),
            )

        interestedJobCategoryRepository.saveAll(
            listOf(
                InterestedJobCategory(member, backend),
                InterestedJobCategory(member, devops),
            ),
        )

        val result = memberUseCase.getMemberMe(member.id)

        assertThat(result.name).isEqualTo("hong")
        assertThat(result.nickname).isEqualTo("picklab_member")
        assertThat(result.educationLevel).isEqualTo("대학교(4년)")
        assertThat(result.birthDate).isEqualTo(LocalDate.of(1998, 3, 1))
        assertThat(result.selectedInterestedJobs).containsExactly(JobDetail.BACKEND, JobDetail.DEVOPS)
        assertThat(result.jobFields).containsExactly(JobGroup.DEVELOPMENT)
        assertThat(result.employmentStatus).isEqualTo("재직 중")
        assertThat(result.company).isEqualTo("Picklab")
        assertThat(result.emailAgreement).isTrue()
        assertThat(result.notifyPopularActivity).isTrue()
        assertThat(result.notifyBookmarkedActivity).isFalse()

        memberAgreementRepository.deleteAll()

        assertThat(memberUseCase.getMemberMe(member.id).emailAgreement).isFalse()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    @DisplayName("프로필 이미지를 업로드하고 저장하면 /me에서 영구 이미지 URL을 반환한다")
    fun updateProfileImage(useObjectKey: Boolean) {
        val member =
            memberRepository.save(
                Member(
                    name = "hong",
                    email = "test@example.com",
                    school = "픽랩대학교",
                    graduationStatus = "졸업",
                ),
            )
        notificationPreferenceRepository.save(
            NotificationPreference(
                member = member,
                notifyPopularActivity = false,
                notifyBookmarkedActivity = false,
            ),
        )
        if (s3Client.listBuckets().buckets().none { it.name() == bucketName }) {
            s3Client.createBucket { it.bucket(bucketName) }
        }

        val image =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aohsAAAAASUVORK5CYII=",
            )
        val upload =
            mockMvc
                .post("/v1/files/presigned-url") {
                    header("Authorization", "Bearer ${accessTokenProvider.generateToken(member.id)}")
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"category":"PROFILE","file_name":"profile.png","file_size":${image.size}}"""
                }.andExpect { status { isOk() } }
                .andReturn()
                .extractBody<ResponseWrapper<CreatePresignedurlResponse>>(mapper)
                .data!!

        val request =
            HttpRequest
                .newBuilder(URI.create(upload.presignedUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "image/png")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(image))
                .build()
        val uploaded = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding())
        assertThat(uploaded.statusCode()).isEqualTo(200)

        val profileImage = if (useObjectKey) upload.objectKey else upload.presignedUrl.substringBefore('?')
        mockMvc
            .put("/v1/members/profile-image") {
                header("Authorization", "Bearer ${accessTokenProvider.generateToken(member.id)}")
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(mapOf("profile_image" to profileImage))
            }.andExpect { status { isOk() } }

        val permanentKey = upload.objectKey.removePrefix("temp/")
        mockMvc
            .get("/v1/members/me") {
                header("Authorization", "Bearer ${accessTokenProvider.generateToken(member.id)}")
            }.andExpect { status { isOk() } }
            .andExpect { jsonPath("$.data.school") { value("픽랩대학교") } }
            .andExpect { jsonPath("$.data.graduation_status") { value("졸업") } }
            .andExpect { jsonPath("$.data.profile_image") { value("$storageEndpoint/$bucketName/$permanentKey") } }

        val savedImage = s3Client.getObjectAsBytes { it.bucket(bucketName).key(permanentKey) }
        assertThat(savedImage.asByteArray()).isEqualTo(image)
    }
}
