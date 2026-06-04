package picklab.backend.participation.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class ActivityParticipationSummaryResponse(
    @field:Schema(description = "지원 완료로 표시한 전체 활동 수")
    val appliedCount: Long,
    @field:Schema(description = "최종 합격 활동 수")
    val acceptedCount: Long,
    @field:Schema(description = "불합격 활동 수")
    val rejectedCount: Long,
    @field:Schema(description = "수료 완료 활동 수")
    val completedCount: Long,
)
