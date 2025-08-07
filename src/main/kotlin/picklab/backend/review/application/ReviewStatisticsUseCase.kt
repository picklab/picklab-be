package picklab.backend.review.application

import org.springframework.stereotype.Component
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem
import picklab.backend.review.application.service.ReviewStatisticsQueryService

@Component
class ReviewStatisticsUseCase(
    private val activityService: ActivityService,
    private val reviewStatisticsQueryService: ReviewStatisticsQueryService,
) {
    fun getJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsItem {
        val activity = activityService.mustFindById(activityId)
        return reviewStatisticsQueryService.getJobRelevanceStatistics(activity.id)
    }
}
