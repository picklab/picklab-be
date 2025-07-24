package picklab.backend.participation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.helper.extractBody
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.participation.domain.repository.ActivityParticipationRepository
import picklab.backend.participation.entrypoint.response.GetActivityApplicationUrlResponse
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.test.Test

class ActivityParticipationIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var activityRepository: ActivityRepository

    @Autowired
    private lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var activityParticipationRepository: ActivityParticipationRepository

    private lateinit var member: Member
    private lateinit var activityGroup: ActivityGroup

    @BeforeEach
    fun setUp() {
        cleanUp.all()

        activityGroup =
            activityGroupRepository.save(
                ActivityGroup(
                    name = "테스트 그룹",
                    description = "테스트 그룹 설명",
                ),
            )

        member =
            memberRepository.save(
                Member(
                    name = "테스트 유저",
                    email = "test@example.com",
                ),
            )
    }

    @WithMockUser
    @DisplayName("[성공] 활동 지원에 성공한다")
    @Test
    fun getActivityApplicationUrlSuccess() {
        // given
        val given =
            activityRepository.save(
                ExternalActivity(
                    title = "테스트 대외활동",
                    organizer = OrganizerType.PUBLIC_ORGANIZATION,
                    targetAudience = ParticipantType.WORKER,
                    location = LocationType.SEOUL_INCHEON,
                    recruitmentStartDate = LocalDate.now().minusDays(1),
                    recruitmentEndDate = LocalDate.now().plusMonths(1),
                    startDate = LocalDate.now().plusMonths(3),
                    endDate = LocalDate.now().plusMonths(6),
                    status = RecruitmentStatus.OPEN,
                    viewCount = 0L,
                    duration =
                        ChronoUnit.DAYS
                            .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                            .toInt(),
                    activityHomepageUrl = "테스트 홈페이지 url",
                    activityApplicationUrl = "테스트 신청 url",
                    activityThumbnailUrl = null,
                    activityGroup = activityGroup,
                    activityField = ActivityFieldType.MENTORING,
                    benefit = "",
                ),
            )

        // when
        val result =
            mockMvc
                .get("/v1/activities/${given.id}/application-url")
                .andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITY_APPLICATION_URL.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITY_APPLICATION_URL.message) } }
                .andReturn()
        val body: ResponseWrapper<GetActivityApplicationUrlResponse> = result.extractBody(mapper)
        val got = body.data!!

        // then
        assertThat(got.applicationUrl).isEqualTo(given.activityApplicationUrl)
    }
}
