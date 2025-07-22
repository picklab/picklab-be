package picklab.backend.review.application.query.model

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import java.time.LocalDateTime

data class MyReviewListItem
    @QueryProjection
    constructor(
        val id: Long,
        val title: String,
        val organizer: OrganizerType,
        val activityType: String,
        val createdAt: LocalDateTime,
        val approvalStatus: ReviewApprovalStatus,
    )
