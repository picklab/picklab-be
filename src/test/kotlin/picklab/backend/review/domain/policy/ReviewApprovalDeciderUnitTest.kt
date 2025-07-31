package picklab.backend.review.domain.policy

import org.junit.jupiter.api.DisplayName
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class ReviewApprovalDeciderUnitTest {
    @Test
    @DisplayName("url이 없으면 승인 상태는 미승인(REJECTED)되어야 한다")
    fun urlIsNullOrBlankReturnsRejected() {
        // given
        val originalUrl = "http://old-url.com"
        val originalActivityId = 1L
        val updatedActivityId = 1L
        val originalStatus = ReviewApprovalStatus.APPROVED

        // when
        val result =
            ReviewApprovalDecider.decideOnUpdate(
                originalUrl,
                null,
                originalActivityId,
                updatedActivityId,
                originalStatus,
            )

        // then
        assertEquals(ReviewApprovalStatus.REJECTED, result)
    }

    @Test
    @DisplayName("url과 activity가 변경되지 않으면 기존 승인 상태를 유지한다")
    fun urlAndActivityUnchangedReturnsOriginalStatus() {
        // given
        val originalUrl = "http://old-url.com"
        val originalActivityId = 1L
        val updatedActivityId = 1L
        val originalStatus = ReviewApprovalStatus.APPROVED

        // when
        val result =
            ReviewApprovalDecider.decideOnUpdate(
                originalUrl,
                originalUrl,
                originalActivityId,
                updatedActivityId,
                originalStatus,
            )

        // then
        assertEquals(originalStatus, result)
    }

    @Test
    @DisplayName("url이 변경되면 상태는 승인 중(PENDING)이 되어야 한다")
    fun urlChangedReturnsPending() {
        // given
        val originalUrl = "http://old-url.com"
        val newUrl = "http://new-url.com"
        val originalActivityId = 1L
        val updatedActivityId = 1L
        val originalStatus = ReviewApprovalStatus.APPROVED

        // when
        val result =
            ReviewApprovalDecider.decideOnUpdate(
                originalUrl,
                newUrl,
                originalActivityId,
                updatedActivityId,
                originalStatus,
            )

        // then
        assertEquals(ReviewApprovalStatus.PENDING, result)
    }

    @Test
    @DisplayName("activity가 변경되면 상태는 PENDING이 되어야 한다")
    fun activityChangedReturnsPending() {
        // given
        val originalUrl = "http://example.com"
        val originalActivityId = 1L
        val updatedActivityId = 2L
        val originalStatus = ReviewApprovalStatus.APPROVED

        // when
        val result =
            ReviewApprovalDecider.decideOnUpdate(
                originalUrl,
                originalUrl,
                originalActivityId,
                updatedActivityId,
                originalStatus,
            )

        // then
        assertEquals(ReviewApprovalStatus.PENDING, result)
    }
}
