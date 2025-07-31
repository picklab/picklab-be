package picklab.backend.activity.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.activity.application.model.ActivityItem

@Service
class ActivityQueryService(
    private val activityQueryRepository: ActivityQueryRepository,
) {
    fun getRecommendationActivities(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityItem> = activityQueryRepository.findAllByMemberJobRecommendation(jobIds, pageable)
}
