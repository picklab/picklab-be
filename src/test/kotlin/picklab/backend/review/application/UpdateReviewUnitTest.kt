package picklab.backend.review.application

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.job.domain.service.JobService
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.Member
import picklab.backend.participation.domain.service.ActivityParticipationService
import picklab.backend.review.application.model.ReviewUpdateCommand
import picklab.backend.review.application.service.ReviewOverviewQueryService
import picklab.backend.review.domain.entity.Review
import picklab.backend.review.domain.service.ReviewService
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UpdateReviewUnitTest {
    @MockK
    lateinit var reviewService: ReviewService

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var activityService: ActivityService

    @MockK
    lateinit var activityParticipationService: ActivityParticipationService

    @MockK
    lateinit var reviewCreateConverter: ReviewCreateConverter

    @MockK
    lateinit var reviewOverviewQueryService: ReviewOverviewQueryService

    @MockK
    lateinit var jobService: JobService

    @InjectMockKs
    lateinit var reviewUseCase: ReviewUseCase

    @Test
    @DisplayName("로그인한 사용자와 리뷰 작성자가 다르면 수정 시 CANNOT_UPDATE_REVIEW 에러 발생")
    fun updateReviewByNonAuthor_shouldThrowException() {
        // given
        val loggedInMemberId = 2L
        val reviewId = 10L

        val mockMember =
            mockk<Member> {
                every { id } returns loggedInMemberId
            }
        val mockReview =
            mockk<Review> {
                every { member.id } returns 1L
            }
        val dummyActivity = mockk<Activity>()
        every { memberService.findActiveMember(loggedInMemberId) } returns mockMember
        every { reviewService.mustFindById(reviewId) } returns mockReview
        every { activityService.mustFindById(any()) } returns dummyActivity
        val command =
            ReviewUpdateCommand(
                id = reviewId,
                activityId = 999L,
                memberId = loggedInMemberId,
                overallScore = 3,
                infoScore = 3,
                difficultyScore = 3,
                benefitScore = 3,
                summary = "summary",
                strength = "strength",
                weakness = "weakness",
                tips = null,
                jobRelevanceScore = 3,
                jobGroup = JobGroup.DEVELOPMENT,
                jobDetail = null,
                url = "http://some.url",
            )

        // when
        val exception =
            assertThrows(BusinessException::class.java) {
                reviewUseCase.updateReview(command)
            }

        // then
        assert(exception.errorCode == ErrorCode.CANNOT_UPDATE_REVIEW)
        verify(exactly = 1) { memberService.findActiveMember(loggedInMemberId) }
        verify(exactly = 1) { reviewService.mustFindById(reviewId) }
    }

    @Test
    @DisplayName("로그인한 사용자와 리뷰 작성자가 다르면 조회 시 CANNOT_READ_REVIEW 에러 발생")
    fun getReviewByNonAuthor_shouldThrowException() {
        // given
        val loggedInMemberId = 2L
        val reviewId = 10L

        val mockMember =
            mockk<Member> {
                every { id } returns loggedInMemberId
            }
        val mockReview =
            mockk<Review> {
                every { member.id } returns 1L
            }

        every { memberService.findActiveMember(loggedInMemberId) } returns mockMember
        every { reviewService.mustFindById(reviewId) } returns mockReview

        // when
        val exception =
            assertThrows(BusinessException::class.java) {
                reviewUseCase.getMyReview(reviewId, loggedInMemberId)
            }

        // then
        assert(exception.errorCode == ErrorCode.CANNOT_READ_REVIEW)
        verify(exactly = 1) { memberService.findActiveMember(loggedInMemberId) }
        verify(exactly = 1) { reviewService.mustFindById(reviewId) }
    }

    @Test
    @DisplayName("로그인한 사용자와 리뷰 작성자가 다르면 삭제 시 CANNOT_DELETE_REVIEW 에러 발생")
    fun deleteReviewByNonAuthor_shouldThrowException() {
        // given
        val loggedInMemberId = 3L
        val reviewId = 20L

        val mockMember =
            mockk<Member> {
                every { id } returns loggedInMemberId
            }
        val mockReview =
            mockk<Review> {
                every { member.id } returns 1L
            }

        every { memberService.findActiveMember(loggedInMemberId) } returns mockMember
        every { reviewService.mustFindById(reviewId) } returns mockReview

        // when
        val exception =
            assertThrows(BusinessException::class.java) {
                reviewUseCase.deleteReview(loggedInMemberId, reviewId)
            }

        // then
        assert(exception.errorCode == ErrorCode.CANNOT_DELETE_REVIEW)
        verify(exactly = 1) { memberService.findActiveMember(loggedInMemberId) }
        verify(exactly = 1) { reviewService.mustFindById(reviewId) }
    }
}
