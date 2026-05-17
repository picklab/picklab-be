package picklab.backend.review.application.query.model

import picklab.backend.review.domain.enums.ReviewApprovalStatus
import java.time.LocalDateTime

interface MyReviewListView {
    val id: Long
    val title: String
    val organizer: String?
    val organizerType: String
    val activityType: String
    val createdAt: LocalDateTime
    val approvalStatus: ReviewApprovalStatus
}
