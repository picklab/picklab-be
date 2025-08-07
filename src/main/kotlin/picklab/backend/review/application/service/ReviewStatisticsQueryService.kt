package picklab.backend.review.application.service

import org.springframework.stereotype.Service
import picklab.backend.review.application.query.ReviewStatisticsQueryRepository
import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem

@Service
class ReviewStatisticsQueryService(
    private val reviewStatisticsQueryRepository: ReviewStatisticsQueryRepository,
) {
    fun getJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsItem =
        reviewStatisticsQueryRepository.findJobRelevanceStatistics(activityId)
}
