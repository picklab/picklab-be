package picklab.backend.review

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
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
import picklab.backend.review.application.ReviewUseCase
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.model.MyReviewListQueryRequest
import picklab.backend.review.application.model.ReviewUpdateCommand
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

    @Autowired
    lateinit var reviewUseCase: ReviewUseCase

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

    @Test
    fun withdrawnAuthorsReviewsRemainVisibleWithCountsAndFilters() {
        reviewHelpfulRepository.save(ReviewHelpful(member, review))
        member.withdraw()
        memberRepository.save(member)
        assertThat(memberRepository.findByIdIgnoreDelete(member.id)!!.deletedAt).isNotNull()

        mockMvc
            .get("/v1/activities/${activity.id}/reviews") {
                param("status", "COMPLETED")
                param("rating", "5")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.total_elements") { value(1) }
                jsonPath("$.data.items[0].id") { value(review.id) }
                jsonPath("$.data.items[0].helpful_count") { value(1) }
                jsonPath("$.data.items[0].is_helpful") { value(false) }
            }
        mockMvc
            .get("/v1/activities/${activity.id}/reviews") { param("status", "DROPPED") }
            .andExpect {
                status { isOk() }
                jsonPath("$.data.total_elements") { value(0) }
            }
    }

    @Test
    fun newMemberDoesNotInheritReviewOwnershipOrHelpfulSelection() {
        reviewHelpfulRepository.save(ReviewHelpful(member, review))
        member.withdraw()
        memberRepository.save(member)
        val rejoined = memberRepository.save(Member(name = "재가입회원", email = member.email))

        assertThat(reviewUseCase.getMyReviews(MyReviewListQueryRequest(1, 10, rejoined.id)).content).isEmpty()
        assertThat(assertThrows<BusinessException> { reviewUseCase.getMyReview(review.id, rejoined.id) }.errorCode)
            .isEqualTo(ErrorCode.CANNOT_READ_REVIEW)
        assertThat(assertThrows<BusinessException> { reviewUseCase.deleteReview(rejoined.id, review.id) }.errorCode)
            .isEqualTo(ErrorCode.CANNOT_DELETE_REVIEW)
        val update =
            ReviewUpdateCommand(
                id = review.id,
                memberId = rejoined.id,
                overallScore = 3,
                infoScore = 3,
                difficultyScore = 3,
                benefitScore = 3,
                summary = "수정시도",
                strength = "장점",
                weakness = "단점",
                tips = null,
                jobRelevanceScore = 3,
                objectKey = null,
                jobGroup = JobGroup.DEVELOPMENT,
                jobDetail = null,
            )
        assertThat(assertThrows<BusinessException> { reviewUseCase.updateReview(update) }.errorCode)
            .isEqualTo(ErrorCode.CANNOT_UPDATE_REVIEW)

        val request = ActivityReviewListQueryRequest(null, null, null, null)
        val before = reviewUseCase.getReviewsByActivity(request, activity.id, rejoined.id, 1, 10).content.single()
        assertThat(before.helpfulCount).isEqualTo(1)
        assertThat(before.isHelpful).isFalse()
        reviewUseCase.markReviewHelpful(rejoined.id, review.id)
        val after = reviewUseCase.getReviewsByActivity(request, activity.id, rejoined.id, 1, 10).content.single()
        assertThat(after.helpfulCount).isEqualTo(2)
        assertThat(after.isHelpful).isTrue()
    }

    @Test
    fun deletedAndUnapprovedReviewsRemainHiddenAfterWithdrawal() {
        member.withdraw()
        memberRepository.save(member)
        review.reviewApprovalStatus = ReviewApprovalStatus.PENDING
        reviewRepository.save(review)
        mockMvc.get("/v1/activities/${activity.id}/reviews").andExpect {
            status { isOk() }
            jsonPath("$.data.total_elements") { value(0) }
        }

        review.reviewApprovalStatus = ReviewApprovalStatus.APPROVED
        review.delete()
        reviewRepository.save(review)
        mockMvc.get("/v1/activities/${activity.id}/reviews").andExpect {
            status { isOk() }
            jsonPath("$.data.total_elements") { value(0) }
        }
    }
}
