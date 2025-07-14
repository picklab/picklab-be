package picklab.backend.activity.application

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse

@Component
class ActivityUseCase(
    private val activityService: ActivityService,
    private val activityBookmarkService: ActivityBookmarkService,
) {
    fun getActivities(
        queryParams: ActivitySearchCommand,
        size: Int,
        page: Int,
        memberId: Long?,
    ): GetActivityListResponse {
        val pageable = PageRequest.of(page - 1, size)
        val queryData = activityService.adjustQueryByCategory(queryParams)

        val activityPage =
            activityService.getActivities(
                queryData = queryData,
                pageable = pageable,
            )

        val activityItems = activityPage.content
        val activityIds = activityItems.map { it.id }

        val bookmarkedActivityIds =
            activityBookmarkService.getMyBookmarkedActivityIds(
                memberId = memberId,
                activityIds = activityIds,
            )

        val items =
            activityItems.map {
                ActivityItemWithBookmark.from(
                    item = it,
                    isBookmarked = bookmarkedActivityIds.contains(it.id),
                )
            }

        return GetActivityListResponse.from(
            activityPage = activityPage,
            items = items,
        )
    }

    fun getActivityDetail(
        activityId: Long,
        memberId: Long?,
    ): GetActivityDetailResponse {
        // TODO 조회수 증가 로직 추가 필요
        val activity = activityService.mustFindById(activityId)
        val bookmarkCount = activityBookmarkService.countByActivityId(activityId)
        val isBookmarked = memberId?.let { activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId) } ?: false

        return GetActivityDetailResponse.from(
            activity = activity,
            bookmarkCount = bookmarkCount,
            isBookmarked = isBookmarked,
        )
    }

    fun mustFindActivity(activityId: Long) = activityService.mustFindActiveActivity(activityId)
}
