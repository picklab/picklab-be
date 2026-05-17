package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import java.time.LocalDateTime

@Schema(description = "리뷰 응답")
data class MyReviewsResponse(
    @field:Schema(description = "리뷰 ID", example = "1")
    val id: Long,
    @field:Schema(description = "활동명", example = "활동명")
    val title: String,
    @field:Schema(description = "주최 기관명", example = "삼성전자")
    val organizer: String?,
    @field:Schema(description = "주최 기관 유형", example = "LARGE_CORPORATION")
    val organizerType: String,
    @field:Schema(description = "활동 구분", example = "활동 구분")
    val activityType: String,
    @field:Schema(description = "작성일", example = "2024-01-01T12:00:00")
    val createdAt: LocalDateTime,
    @field:Schema(description = "승인여부 (미승인 / 승인 / 승인 중)", example = "미승인")
    val approvalStatus: ReviewApprovalStatus,
)
