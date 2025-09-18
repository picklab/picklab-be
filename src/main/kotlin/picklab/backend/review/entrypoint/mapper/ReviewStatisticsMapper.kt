package picklab.backend.review.entrypoint.mapper

import picklab.backend.review.application.query.model.JobRelevanceStatisticsView
import picklab.backend.review.application.query.model.SatisfactionStatisticsView
import picklab.backend.review.entrypoint.response.JobRelevanceStatisticsResponse
import picklab.backend.review.entrypoint.response.SatisfactionAvgScores
import picklab.backend.review.entrypoint.response.SatisfactionStatisticsResponse

fun JobRelevanceStatisticsView.toResponse(): JobRelevanceStatisticsResponse =
    JobRelevanceStatisticsResponse(
        planningAvgScore = planningAvgScore,
        developmentAvgScore = developmentAvgScore,
        marketingAvgScore = marketingAvgScore,
        aiAvgScore = aiAvgScore,
        designAvgScore = designAvgScore,
    )

fun SatisfactionStatisticsView.toItem(): SatisfactionAvgScores =
    SatisfactionAvgScores(
        jobGroup = jobGroup,
        jobDetail = jobDetail,
        avgTotalScore = avgTotalScore,
        avgJobExperienceScore = avgJobExperienceScore,
        avgActivityIntensityScore = avgActivityIntensityScore,
        avgBenefitScore = avgBenefitScore,
    )

fun List<SatisfactionStatisticsView>.toResponse(): SatisfactionStatisticsResponse =
    SatisfactionStatisticsResponse(items = map { it.toItem() })
