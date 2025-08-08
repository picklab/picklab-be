package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.review.application.query.model.SatisfactionStatisticsItem

@Schema(description = "활동 만족도 평가 통계 응답")
data class SatisfactionStatisticsResponse(
    @Schema(description = "전체 및 직무별 평균 점수 리스트")
    val items: List<SatisfactionAvgScores>,
)

fun SatisfactionStatisticsItem.toResponse(): SatisfactionAvgScores =
    SatisfactionAvgScores(
        jobGroup = this.jobGroup,
        jobDetail = this.jobDetail,
        avgTotalScore = this.avgTotalScore,
        avgJobExperienceScore = this.avgJobExperienceScore,
        avgActivityIntensityScore = this.avgActivityIntensityScore,
        avgBenefitScore = this.avgBenefitScore,
    )
