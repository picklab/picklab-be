package picklab.backend.review.application.query

import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem
import picklab.backend.review.application.query.model.SatisfactionStatisticsItem

interface ReviewStatisticsQueryRepository {
    fun findJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsItem

    fun findSatisfactionStatistics(
        activityId: Long,
        jobCategoryIds: List<Long>,
    ): List<SatisfactionStatisticsItem>
}
