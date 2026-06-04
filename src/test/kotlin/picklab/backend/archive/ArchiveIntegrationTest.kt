package picklab.backend.archive

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import picklab.backend.activitygroup.domain.repository.ActivityGroupRepository
import picklab.backend.archive.domain.enums.DetailRoleType
import picklab.backend.archive.domain.enums.RoleType
import picklab.backend.archive.domain.repository.ArchiveRepository
import picklab.backend.archive.entrypoint.request.ArchiveCreateRequest
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.participation.domain.repository.ActivityParticipationRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ArchiveIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var participationRepository: ActivityParticipationRepository

    @Autowired
    lateinit var archiveRepository: ArchiveRepository

    lateinit var member: Member
    lateinit var activity: Activity

    @BeforeEach
    fun setUp() {
        cleanUp.all()

        member =
            memberRepository.save(
                Member(
                    name = "테스트 유저",
                    email = "test@example.com",
                ),
            )

        val activityGroup =
            activityGroupRepository.save(
                ActivityGroup(
                    name = "테스트 그룹",
                    description = "테스트 그룹 설명",
                ),
            )

        activity =
            activityRepository.save(
                ExternalActivity(
                    title = "테스트 대외활동",
                    organizer = "테스트 주최",
                    organizerType = OrganizerType.PUBLIC_ORGANIZATION,
                    targetAudience = ParticipantType.WORKER,
                    location = LocationType.SEOUL_INCHEON,
                    recruitmentStartDate = LocalDate.now().minusDays(1),
                    recruitmentEndDate = LocalDate.now().plusMonths(1),
                    startDate = LocalDate.now().plusMonths(2),
                    endDate = LocalDate.now().plusMonths(3),
                    status = RecruitmentStatus.OPEN,
                    viewCount = 0L,
                    duration =
                        ChronoUnit.DAYS
                            .between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1))
                            .toInt(),
                    activityHomepageUrl = null,
                    activityApplicationUrl = null,
                    activityThumbnailUrl = null,
                    description = "테스트 설명",
                    benefit = "테스트 혜택",
                    activityGroup = activityGroup,
                    activityField = ActivityFieldType.MENTORING,
                ),
            )
    }

    @Nested
    @WithMockUser
    @DisplayName("아카이브 API")
    inner class ArchiveApiTests {
        @Test
        @DisplayName("[성공] 수료 완료한 참여 이력은 아카이브가 없어도 미작성으로 조회된다")
        fun getCompletedParticipationWithoutArchive() {
            val participation = saveParticipation(ProgressStatus.COMPLETED)

            mockMvc
                .get("/v1/archive")
                .andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ARCHIVE_LIST.status.value()) } }
                .andExpect { jsonPath("$.data[0].archive_id") { value(nullValue()) } }
                .andExpect { jsonPath("$.data[0].activity_participation_id") { value(participation.id) } }
                .andExpect { jsonPath("$.data[0].write_status") { value("NOT_WRITTEN") } }
        }

        @Test
        @DisplayName("[성공] 수료 완료한 참여 이력에 아카이브를 생성한다")
        fun createArchiveForCompletedParticipation() {
            val participation = saveParticipation(ProgressStatus.COMPLETED)

            mockMvc
                .post("/v1/archive") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(createRequest(participation.id))
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.CREATE_ARCHIVE_SUCCESS.status.value()) } }

            val archive = archiveRepository.findByParticipationId(participation.id)
            assertThat(archive).isNotNull
            assertThat(archive!!.participation.id).isEqualTo(participation.id)

            mockMvc
                .get("/v1/archive")
                .andExpect { status { isOk() } }
                .andExpect { jsonPath("$.data[0].archive_id") { value(archive.id) } }
                .andExpect { jsonPath("$.data[0].write_status") { value("COMPLETED") } }
        }

        @Test
        @DisplayName("[실패] 수료 완료하지 않은 참여 이력은 아카이브를 생성할 수 없다")
        fun cannotCreateArchiveForNotCompletedParticipation() {
            val participation = saveParticipation(ProgressStatus.NOT_SELECTED)

            mockMvc
                .post("/v1/archive") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(createRequest(participation.id))
                }.andExpect { status { isBadRequest() } }
                .andExpect { jsonPath("$.code") { value(ErrorCode.CANNOT_CREATE_ARCHIVE.status.value()) } }
                .andExpect { jsonPath("$.message") { value(ErrorCode.CANNOT_CREATE_ARCHIVE.message) } }
        }
    }

    private fun saveParticipation(progressStatus: ProgressStatus): ActivityParticipation =
        participationRepository.save(
            ActivityParticipation(
                applicationStatus = ApplicationStatus.ACCEPTED,
                progressStatus = progressStatus,
                member = member,
                activity = activity,
            ),
        )

    private fun createRequest(participationId: Long): ArchiveCreateRequest =
        ArchiveCreateRequest(
            participationId = participationId,
            detailRole = DetailRoleType.BACKEND,
            activityRecord = "백엔드 API를 구현했습니다.",
            fileUrls = emptyList(),
            referenceUrls = emptyList(),
            startDate = LocalDate.now().minusMonths(1),
            endDate = LocalDate.now(),
            role = RoleType.DEVELOPMENT,
            customRole = null,
        )
}
