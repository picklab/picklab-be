package picklab.backend.activity.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.application.model.GetMyBookmarkListCondition

@Service
class ActivityQueryService(
    private val activityQueryRepository: ActivityQueryRepository,
) {
    fun getRecommendationActivities(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityView> = activityQueryRepository.findAllByMemberJobRecommendation(jobIds, pageable)

    fun getBookmarkedActivityItems(
        memberId: Long,
        queryData: GetMyBookmarkListCondition,
        pageable: PageRequest,
    ): Page<ActivityView> = activityQueryRepository.findActivityItemByMemberBookmarked(memberId, queryData, pageable)

    fun getRecentlyViewedActivities(
        memberId: Long,
        pageable: PageRequest,
    ): Page<ActivityView> = activityQueryRepository.findRecentlyViewedActivities(memberId, pageable)
}
