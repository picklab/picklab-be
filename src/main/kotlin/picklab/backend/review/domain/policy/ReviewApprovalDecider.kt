package picklab.backend.review.domain.policy

import picklab.backend.review.domain.enums.ReviewApprovalStatus

object ReviewApprovalDecider {
    fun decideOnCreate(url: String?): ReviewApprovalStatus =
        if (url.isNullOrBlank()) ReviewApprovalStatus.REJECTED else ReviewApprovalStatus.PENDING

    fun decideOnUpdate(
        originalUrl: String?,
        newUrl: String?,
        originalActivityId: Long,
        updatedActivityId: Long,
        originalStatus: ReviewApprovalStatus,
    ): ReviewApprovalStatus =
        when {
            newUrl.isNullOrBlank() -> ReviewApprovalStatus.REJECTED
            newUrl != originalUrl || updatedActivityId != originalActivityId -> ReviewApprovalStatus.PENDING
            else -> originalStatus
        }
}
