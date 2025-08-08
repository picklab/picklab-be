package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem

@Schema(description = "직무 연관성 통계 응답")
data class JobRelevanceStatisticsResponse(
    @Schema(description = "기획 직무 연관성 평균 점수")
    val planningAvgScore: Double,
    @Schema(description = "개발 직무 연관성 평균 점수")
    val developmentAvgScore: Double,
    @Schema(description = "마케팅 직무 연관성 평균 점수")
    val marketingAvgScore: Double,
    @Schema(description = "AI 직무 연관성 평균 점수")
    val aiScore: Double,
    @Schema(description = "디자인 직무 연관성 평균 점수")
    val designAvgScore: Double,
)

fun JobRelevanceStatisticsItem.toResponse(): JobRelevanceStatisticsResponse =
    JobRelevanceStatisticsResponse(
        planningAvgScore = this.planningAvgScore,
        developmentAvgScore = this.developmentAvgScore,
        marketingAvgScore = this.marketingAvgScore,
        aiScore = this.aiAvgScore,
        designAvgScore = this.designAvgScore,
    )
