package picklab.backend.activity.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.ActivityItem

interface ActivityQueryRepository {
    fun findAllByMemberJobRecommendation(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityItem>

    fun findPopularActivities(pageable: PageRequest): Page<ActivityItem>

    fun findActivityItemByActivityIds(activityIds: List<Long>): List<ActivityItem>
}
