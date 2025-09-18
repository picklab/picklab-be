package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import java.time.LocalDateTime

@Schema(description = "리뷰 응답")
data class MyReviewsResponse(
    @Schema(description = "리뷰 ID", example = "1")
    val id: Long,
    @Schema(description = "활동명", example = "활동명")
    val title: String,
    @Schema(description = "주최기관/단체명", example = "주최기관/단체명")
    val organizer: OrganizerType,
    @Schema(description = "활동 구분", example = "활동 구분")
    val activityType: String,
    @Schema(description = "작성일", example = "2024-01-01T12:00:00")
    val createdAt: LocalDateTime,
    @Schema(description = "승인여부 (미승인 / 승인 / 승인 중)", example = "미승인")
    val approvalStatus: ReviewApprovalStatus,
)
