package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.review.application.query.model.JobRelevanceStatisticsView

@Schema(description = "직무 연관성 통계 응답")
data class JobRelevanceStatisticsResponse(
    @Schema(description = "기획 직무 연관성 평균 점수")
    val planningAvgScore: Double,
    @Schema(description = "개발 직무 연관성 평균 점수")
    val developmentAvgScore: Double,
    @Schema(description = "마케팅 직무 연관성 평균 점수")
    val marketingAvgScore: Double,
    @Schema(description = "AI 직무 연관성 평균 점수")
    val aiAvgScore: Double,
    @Schema(description = "디자인 직무 연관성 평균 점수")
    val designAvgScore: Double,
)

fun JobRelevanceStatisticsView.toResponse(): JobRelevanceStatisticsResponse =
    JobRelevanceStatisticsResponse(
        planningAvgScore = this.planningAvgScore,
        developmentAvgScore = this.developmentAvgScore,
        marketingAvgScore = this.marketingAvgScore,
        aiAvgScore = this.aiAvgScore,
        designAvgScore = this.designAvgScore,
    )
