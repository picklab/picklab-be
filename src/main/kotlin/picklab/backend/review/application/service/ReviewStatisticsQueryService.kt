package picklab.backend.review.application.service

import org.springframework.stereotype.Service
import picklab.backend.review.application.query.ReviewStatisticsQueryRepository
import picklab.backend.review.application.query.model.JobRelevanceStatisticsView
import picklab.backend.review.application.query.model.SatisfactionStatisticsView

@Service
class ReviewStatisticsQueryService(
    private val reviewStatisticsQueryRepository: ReviewStatisticsQueryRepository,
) {
    fun getJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsView =
        reviewStatisticsQueryRepository.findJobRelevanceStatistics(activityId)

    fun getSatisfactionStatistics(
        activityId: Long,
        jobCategoryIds: List<Long>,
    ): List<SatisfactionStatisticsView> = reviewStatisticsQueryRepository.findSatisfactionStatistics(activityId, jobCategoryIds)
}
