package picklab.backend.review.application.query

import picklab.backend.review.application.query.model.JobRelevanceStatisticsView
import picklab.backend.review.application.query.model.SatisfactionStatisticsView

interface ReviewStatisticsQueryRepository {
    fun findJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsView

    fun findSatisfactionStatistics(
        activityId: Long,
        jobCategoryIds: List<Long>,
    ): List<SatisfactionStatisticsView>
}
