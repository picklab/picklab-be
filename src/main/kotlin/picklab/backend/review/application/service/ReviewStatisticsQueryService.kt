package picklab.backend.review.application.service

import org.springframework.stereotype.Service
import picklab.backend.review.application.query.ReviewStatisticsQueryRepository
import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem
import picklab.backend.review.application.query.model.SatisfactionStatisticsItem

@Service
class ReviewStatisticsQueryService(
    private val reviewStatisticsQueryRepository: ReviewStatisticsQueryRepository,
) {
    fun getJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsItem =
        reviewStatisticsQueryRepository.findJobRelevanceStatistics(activityId)

    fun getSatisfactionStatistics(
        activityId: Long,
        jobCategoryIds: List<Long>,
    ): List<SatisfactionStatisticsItem> = reviewStatisticsQueryRepository.findSatisfactionStatistics(activityId, jobCategoryIds)
}
