package picklab.backend.review

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.delete
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
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.job.domain.JobCategoryRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.participation.domain.repository.ActivityParticipationRepository
import picklab.backend.review.domain.entity.Review
import picklab.backend.review.domain.entity.ReviewHelpful
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import picklab.backend.review.domain.repository.ReviewHelpfulRepository
import picklab.backend.review.domain.repository.ReviewRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ReviewHelpfulIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var jobCategoryRepository: JobCategoryRepository

    @Autowired
    lateinit var activityParticipationRepository: ActivityParticipationRepository

    @Autowired
    lateinit var reviewRepository: ReviewRepository

    @Autowired
    lateinit var reviewHelpfulRepository: ReviewHelpfulRepository

    lateinit var member: Member
    lateinit var activity: Activity
    lateinit var review: Review

    @BeforeEach
    fun setUp() {
        cleanUp.all()

        member =
            memberRepository.save(
                Member(
                    name = "테스트 유저",
                    email = "review-helpful@example.com",
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
                    title = "테스트 활동",
                    organizer = "테스트 기관",
                    organizerType = OrganizerType.PUBLIC_ORGANIZATION,
                    targetAudience = ParticipantType.WORKER,
                    location = LocationType.SEOUL_INCHEON,
                    recruitmentStartDate = LocalDate.now().minusMonths(6),
                    recruitmentEndDate = LocalDate.now().minusMonths(5),
                    startDate = LocalDate.now().minusMonths(4),
                    endDate = LocalDate.now().minusMonths(1),
                    status = RecruitmentStatus.CLOSED,
                    viewCount = 0L,
                    duration =
                        ChronoUnit.DAYS
                            .between(LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(1))
                            .toInt(),
                    activityHomepageUrl = null,
                    activityApplicationUrl = null,
                    activityThumbnailUrl = null,
                    description = null,
                    benefit = "테스트 혜택",
                    activityGroup = activityGroup,
                    activityField = ActivityFieldType.MENTORING,
                ),
            )
        val jobCategory =
            jobCategoryRepository.save(
                JobCategory(
                    jobGroup = JobGroup.DEVELOPMENT,
                ),
            )
        activityParticipationRepository.save(
            ActivityParticipation(
                applicationStatus = ApplicationStatus.ACCEPTED,
                progressStatus = ProgressStatus.COMPLETED,
                member = member,
                activity = activity,
            ),
        )
        review =
            reviewRepository.save(
                Review(
                    overallScore = 5,
                    infoScore = 4,
                    difficultyScore = 3,
                    benefitScore = 5,
                    summary = "도움되는 리뷰",
                    strength = "장점",
                    weakness = "단점",
                    tips = "팁",
                    jobRelevanceScore = 5,
                    reviewApprovalStatus = ReviewApprovalStatus.APPROVED,
                    member = member,
                    activity = activity,
                    jobCategory = jobCategory,
                ),
            )
    }

    @Test
    @WithMockUser
    @DisplayName("도움돼요 생성은 멱등하며 로그인 리뷰 목록에 선택 상태를 반환한다")
    fun markReviewHelpful() {
        repeat(2) {
            mockMvc
                .post("/v1/reviews/${review.id}/helpful")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value(SuccessCode.MARK_REVIEW_HELPFUL_SUCCESS.message) }
                }
        }

        assertThat(reviewHelpfulRepository.count()).isEqualTo(1)

        mockMvc
            .get("/v1/activities/${activity.id}/reviews")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.items[0].helpful_count") { value(1) }
                jsonPath("$.data.items[0].is_helpful") { value(true) }
            }
    }

    @Test
    @DisplayName("비로그인 리뷰 목록에는 도움돼요 개수와 false 상태를 반환한다")
    fun getReviewHelpfulAnonymously() {
        reviewHelpfulRepository.save(
            ReviewHelpful(
                member = member,
                review = review,
            ),
        )

        mockMvc
            .get("/v1/activities/${activity.id}/reviews")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.items[0].helpful_count") { value(1) }
                jsonPath("$.data.items[0].is_helpful") { value(false) }
            }
    }

    @Test
    @WithMockUser
    @DisplayName("도움돼요 취소는 멱등하게 hard delete한다")
    fun unmarkReviewHelpful() {
        reviewHelpfulRepository.save(
            ReviewHelpful(
                member = member,
                review = review,
            ),
        )

        repeat(2) {
            mockMvc
                .delete("/v1/reviews/${review.id}/helpful")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value(SuccessCode.UNMARK_REVIEW_HELPFUL_SUCCESS.message) }
                }
        }

        assertThat(reviewHelpfulRepository.count()).isZero()
    }
}
