package picklab.backend.review.domain.policy

import picklab.backend.review.domain.enums.ReviewApprovalStatus

object ReviewApprovalDecider {
    fun decide(url: String?): ReviewApprovalStatus =
        if (url.isNullOrBlank()) ReviewApprovalStatus.REJECTED else ReviewApprovalStatus.PENDING
}
