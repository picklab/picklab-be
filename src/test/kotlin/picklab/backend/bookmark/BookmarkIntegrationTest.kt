package picklab.backend.bookmark

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BookmarkIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var activityBookmarkRepository: ActivityBookmarkRepository

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
                    organizer = OrganizerType.PUBLIC_ORGANIZATION,
                    targetAudience = ParticipantType.WORKER,
                    location = LocationType.SEOUL_INCHEON,
                    recruitmentStartDate = LocalDate.now().plusDays(1),
                    recruitmentEndDate = LocalDate.now().plusMonths(1),
                    startDate = LocalDate.now().plusMonths(3),
                    endDate = LocalDate.now().plusMonths(6),
                    status = RecruitmentStatus.OPEN,
                    viewCount = 0L,
                    duration =
                        ChronoUnit.DAYS
                            .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                            .toInt(),
                    activityThumbnailUrl = null,
                    activityGroup = activityGroup,
                    activityField = ActivityFieldType.MENTORING,
                    benefit = "테스트 혜택",
                ),
            )
    }

    @Nested
    @WithMockUser
    @DisplayName("활동 북마크 관련 테스트")
    inner class ActivityBookmarkTests {
        @Nested
        @DisplayName("활동 북마크 생성 테스트")
        inner class CreateActivityBookmarkTests {
            @Test
            @DisplayName("[성공] 활동 북마크를 생성한다")
            fun createActivityBookmarkSuccess() {
                // when
                val result =
                    mockMvc
                        .post("/v1/activities/${activity.id}/bookmarks") {
                        }.andExpect { status { isCreated() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.ACTIVITY_BOOKMARK_CREATED.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.ACTIVITY_BOOKMARK_CREATED.message) } }
                        .andReturn()

                // then
                val exist = activityBookmarkRepository.existsByMemberAndActivity(member, activity)
                assertThat(exist).isTrue
            }

            @Test
            @DisplayName("[실패] 이미 북마크가 존재한다면 ALREADY_EXISTS_ACTIVITY_BOOKMARK 에러코드가 발생한다")
            fun alreadyExistBookmarkTest() {
                // given
                activityBookmarkRepository.save(
                    ActivityBookmark(
                        member = member,
                        activity = activity,
                    ),
                )

                // when
                mockMvc
                    .post("/v1/activities/${activity.id}/bookmarks") {
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK.status.value()) } }
                    .andExpect { jsonPath("$.message") { value(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK.message) } }
            }
        }

        @Nested
        @DisplayName("활동 북마크 해제 테스트")
        inner class RemoveActivityBookmarkTest {
            @Test
            @DisplayName("[성공] 활동 북마크를 해제한다")
            fun removeActivityBookmarkSuccess() {
                // given
                activityBookmarkRepository.save(
                    ActivityBookmark(
                        member = member,
                        activity = activity,
                    ),
                )

                mockMvc
                    .delete("/v1/activities/${activity.id}/bookmarks") {
                    }.andExpect { status { isOk() } }
                    .andExpect { jsonPath("$.code") { value(SuccessCode.ACTIVITY_BOOKMARK_REMOVED.status.value()) } }
                    .andExpect { jsonPath("$.message") { value(SuccessCode.ACTIVITY_BOOKMARK_REMOVED.message) } }

                // then
                val exist = activityBookmarkRepository.existsByMemberAndActivity(member, activity)
                assertThat(exist).isFalse
            }

            @Test
            @DisplayName("[실패] 활동이 북마크되어 있지 않다면 NOT_FOUND_ACTIVITY_BOOKMARK 에러코드가 발생한다")
            fun activityBookmarkNotFoundTest() {
                mockMvc
                    .delete("/v1/activities/${activity.id}/bookmarks") {
                    }.andExpect { status { isNotFound() } }
                    .andExpect { jsonPath("$.code") { value(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK.status.value()) } }
                    .andExpect { jsonPath("$.message") { value(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK.message) } }
            }
        }
    }
}
