package picklab.backend.activity.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.application.model.GetMyBookmarkListCondition

interface ActivityQueryRepository {
    fun findAllByMemberJobRecommendation(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityView>

    fun findPopularActivities(pageable: PageRequest): Page<ActivityView>

    fun findActivityItemByMemberBookmarked(
        memberId: Long,
        queryData: GetMyBookmarkListCondition,
        pageable: PageRequest,
    ): Page<ActivityView>

    fun findRecentlyViewedActivities(
        memberId: Long,
        pageable: PageRequest,
    ): Page<ActivityView>
}
