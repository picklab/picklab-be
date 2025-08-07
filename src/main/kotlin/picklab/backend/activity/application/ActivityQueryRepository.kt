package picklab.backend.activity.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.GetMyBookmarkListCondition

interface ActivityQueryRepository {
    fun findAllByMemberJobRecommendation(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityItem>

    fun findPopularActivities(pageable: PageRequest): Page<ActivityItem>

    fun findActivityItemByActivityIds(activityIds: List<Long>): List<ActivityItem>

    fun findActivityItemByMemberBookmarked(
        memberId: Long,
        queryData: GetMyBookmarkListCondition,
        pageable: PageRequest,
    ): Page<ActivityItem>

    fun findRecentlyViewedActivities(
        memberId: Long,
        pageable: PageRequest,
    ): Page<ActivityItem>
}
