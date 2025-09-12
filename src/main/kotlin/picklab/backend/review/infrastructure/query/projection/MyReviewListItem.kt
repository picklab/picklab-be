package picklab.backend.review.infrastructure.query.projection

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.review.application.query.model.MyReviewListView
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import java.time.LocalDateTime

@QueryProjection
data class MyReviewListItem(
    override val id: Long,
    override val title: String,
    override val organizer: OrganizerType,
    override val activityType: String,
    override val createdAt: LocalDateTime,
    override val approvalStatus: ReviewApprovalStatus,
) : MyReviewListView
