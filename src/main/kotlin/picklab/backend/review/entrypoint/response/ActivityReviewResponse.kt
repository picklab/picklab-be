package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.participation.domain.enums.ProgressStatus
import java.time.LocalDateTime

@Suppress("ktlint:standard:comment-wrapping")
@Schema(description = "리뷰 응답")
data class ActivityReviewResponse(
    @Schema(description = "리뷰 ID", example = "1")
    val id: Long,
    @Schema(description = "총 평점", example = "4")
    val overallScore: Int,
    @Schema(description = "직무 경험 평점", example = "4")
    val infoScore: Int,
    @Schema(description = "활동 강도 평점", example = "3")
    val difficultyScore: Int,
    @Schema(description = "혜택 및 복지 평점", example = "5")
    val benefitScore: Int,
    @Schema(description = "직무", example = "기획")
    val jobGroup: JobGroup,
    @Schema(description = "리뷰 세부 직무 (null = 전체)", example = "서비스 기획")
    val jobDetail: JobDetail?,
    @Schema(description = "참여 날짜", example = "2024-01-01T12:00:00")
    val participationDate: LocalDateTime,
    @Schema(description = "진행 상태 (진행 중 / 수료 완료 / 중도 포기)", example = "수료 완료")
    val progressStatus: ProgressStatus,
    // 로그인 사용자만 노출되는 필드
    @Schema(description = "한줄평")
    val summary: String? = null,
    @Schema(description = "장점")
    val strength: String? = null,
    @Schema(description = "단점")
    val weakness: String? = null,
    @Schema(description = "꿀팁")
    val tips: String? = null,
)
