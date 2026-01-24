package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "직무 연관성 통계 응답")
data class JobRelevanceStatisticsResponse(
    @field:Schema(description = "기획 직무 연관성 평균 점수")
    val planningAvgScore: Double,
    @field:Schema(description = "개발 직무 연관성 평균 점수")
    val developmentAvgScore: Double,
    @field:Schema(description = "마케팅 직무 연관성 평균 점수")
    val marketingAvgScore: Double,
    @field:Schema(description = "AI 직무 연관성 평균 점수")
    val aiAvgScore: Double,
    @field:Schema(description = "디자인 직무 연관성 평균 점수")
    val designAvgScore: Double,
)
