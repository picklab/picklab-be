package picklab.backend.review.domain.policy

import picklab.backend.review.domain.enums.ReviewApprovalStatus

object ReviewApprovalDecider {
    fun decideOnCreate(objectKey: String?): ReviewApprovalStatus =
        if (objectKey.isNullOrBlank()) ReviewApprovalStatus.REJECTED else ReviewApprovalStatus.PENDING

    fun decideOnUpdate(
        originalObjectKey: String?,
        newObjectKey: String?,
        originalActivityId: Long,
        updatedActivityId: Long,
        originalStatus: ReviewApprovalStatus,
    ): ReviewApprovalStatus =
        when {
            newObjectKey.isNullOrBlank() -> ReviewApprovalStatus.REJECTED
            newObjectKey != originalObjectKey || updatedActivityId != originalActivityId -> ReviewApprovalStatus.PENDING
            else -> originalStatus
        }
}
