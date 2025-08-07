package picklab.backend.review.application.query

import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem

interface ReviewStatisticsQueryRepository {
    fun findJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsItem
}
