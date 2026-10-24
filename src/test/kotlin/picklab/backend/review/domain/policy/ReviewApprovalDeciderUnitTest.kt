package picklab.backend.review.domain.policy

import org.junit.jupiter.api.DisplayName
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class ReviewApprovalDeciderUnitTest {
    @Test
    @DisplayName("인증 자료 키가 없으면 승인 상태는 미승인(REJECTED)이어야 한다")
    fun objectKeyIsNullOrBlankReturnsRejected() {
        // given
        val originalObjectKey = "review/1/10/abcd1234_20261002_120000.jpg"
        val originalStatus = ReviewApprovalStatus.APPROVED

        // when
        listOf(null, "", " ").forEach { newObjectKey ->
            val result =
                ReviewApprovalDecider.decideOnUpdate(
                    originalObjectKey,
                    newObjectKey,
                    originalStatus,
                )

            // then
            assertEquals(ReviewApprovalStatus.REJECTED, result)
        }
    }

    @Test
    @DisplayName("인증 자료 키가 변경되지 않으면 기존 승인 상태를 유지한다")
    fun objectKeyUnchangedReturnsOriginalStatus() {
        // given
        val originalObjectKey = "review/1/10/abcd1234_20261002_120000.jpg"

        // when
        ReviewApprovalStatus.entries.forEach { originalStatus ->
            val result =
                ReviewApprovalDecider.decideOnUpdate(
                    originalObjectKey,
                    originalObjectKey,
                    originalStatus,
                )

            // then
            assertEquals(originalStatus, result)
        }
    }

    @Test
    @DisplayName("인증 자료 키가 변경되면 상태는 승인 중(PENDING)이어야 한다")
    fun objectKeyChangedReturnsPending() {
        // given
        val originalObjectKey = "review/1/10/abcd1234_20261002_120000.jpg"
        val newObjectKey = "review/1/10/ffff1234_20261024_120000.jpg"
        val originalStatus = ReviewApprovalStatus.APPROVED

        // when
        val result =
            ReviewApprovalDecider.decideOnUpdate(
                originalObjectKey,
                newObjectKey,
                originalStatus,
            )

        // then
        assertEquals(ReviewApprovalStatus.PENDING, result)
    }
}
