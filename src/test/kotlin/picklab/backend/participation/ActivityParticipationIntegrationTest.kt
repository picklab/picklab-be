package picklab.backend.participation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
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
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.participation.domain.repository.ActivityParticipationRepository
import picklab.backend.participation.entrypoint.request.UpdateApplicationStatusRequest
import picklab.backend.participation.entrypoint.request.UpdateProgressStatusRequest
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ActivityParticipationIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var participationRepository: ActivityParticipationRepository

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
    @DisplayName("활동 참여 API")
    inner class ActivityParticipationTests {
        @Test
        @DisplayName("[성공] 활동을 지원 완료로 표시한다")
        fun createAppliedParticipation() {
            mockMvc
                .post("/v1/activities/${activity.id}/participations")
                .andExpect { status { isCreated() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.CREATE_ACTIVITY_PARTICIPATION.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.CREATE_ACTIVITY_PARTICIPATION.message) } }

            val participation = participationRepository.findByMemberIdAndActivityId(member.id, activity.id)
            assertThat(participation).isNotNull
            assertThat(participation!!.applicationStatus).isEqualTo(ApplicationStatus.APPLIED)
            assertThat(participation.progressStatus).isEqualTo(ProgressStatus.NOT_SELECTED)
        }

        @Test
        @DisplayName("[실패] 이미 지원 완료 표시한 활동은 중복 표시할 수 없다")
        fun cannotCreateDuplicateAppliedParticipation() {
            participationRepository.save(
                ActivityParticipation(
                    applicationStatus = ApplicationStatus.APPLIED,
                    progressStatus = ProgressStatus.NOT_SELECTED,
                    member = member,
                    activity = activity,
                ),
            )

            mockMvc
                .post("/v1/activities/${activity.id}/participations")
                .andExpect { status { isBadRequest() } }
                .andExpect { jsonPath("$.code") { value(ErrorCode.ALREADY_EXISTS_ACTIVITY_PARTICIPATION.status.value()) } }
                .andExpect { jsonPath("$.message") { value(ErrorCode.ALREADY_EXISTS_ACTIVITY_PARTICIPATION.message) } }
        }

        @Test
        @DisplayName("[성공] 합격 여부와 수료 여부를 수정하고 현황을 조회한다")
        fun updateStatusesAndGetSummary() {
            val participation =
                participationRepository.save(
                    ActivityParticipation(
                        applicationStatus = ApplicationStatus.APPLIED,
                        progressStatus = ProgressStatus.NOT_SELECTED,
                        member = member,
                        activity = activity,
                    ),
                )

            mockMvc
                .patch("/v1/activity-participations/${participation.id}/application-status") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        mapper.writeValueAsString(
                            UpdateApplicationStatusRequest(ApplicationStatus.ACCEPTED),
                        )
                }.andExpect { status { isOk() } }

            mockMvc
                .patch("/v1/activity-participations/${participation.id}/progress-status") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        mapper.writeValueAsString(
                            UpdateProgressStatusRequest(ProgressStatus.COMPLETED),
                        )
                }.andExpect { status { isOk() } }

            mockMvc
                .get("/v1/activity-participations/summary")
                .andExpect { status { isOk() } }
                .andExpect { jsonPath("$.data.applied_count") { value(1) } }
                .andExpect { jsonPath("$.data.accepted_count") { value(1) } }
                .andExpect { jsonPath("$.data.rejected_count") { value(0) } }
                .andExpect { jsonPath("$.data.completed_count") { value(1) } }
        }

        @Test
        @DisplayName("[실패] 최종 합격 상태가 아니면 수료 여부를 수정할 수 없다")
        fun cannotUpdateProgressStatusWithoutAccepted() {
            val participation =
                participationRepository.save(
                    ActivityParticipation(
                        applicationStatus = ApplicationStatus.APPLIED,
                        progressStatus = ProgressStatus.NOT_SELECTED,
                        member = member,
                        activity = activity,
                    ),
                )

            mockMvc
                .patch("/v1/activity-participations/${participation.id}/progress-status") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        mapper.writeValueAsString(
                            UpdateProgressStatusRequest(ProgressStatus.COMPLETED),
                        )
                }.andExpect { status { isBadRequest() } }
                .andExpect { jsonPath("$.code") { value(ErrorCode.CANNOT_UPDATE_ACTIVITY_PROGRESS_STATUS.status.value()) } }
                .andExpect { jsonPath("$.message") { value(ErrorCode.CANNOT_UPDATE_ACTIVITY_PROGRESS_STATUS.message) } }
        }

        @Test
        @DisplayName("[성공] 지원 완료 표시를 취소한다")
        fun cancelAppliedParticipation() {
            participationRepository.save(
                ActivityParticipation(
                    applicationStatus = ApplicationStatus.APPLIED,
                    progressStatus = ProgressStatus.NOT_SELECTED,
                    member = member,
                    activity = activity,
                ),
            )

            mockMvc
                .delete("/v1/activities/${activity.id}/participations")
                .andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.DELETE_ACTIVITY_PARTICIPATION.status.value()) } }

            assertThat(participationRepository.findByMemberIdAndActivityId(member.id, activity.id)).isNull()
        }
    }
}
