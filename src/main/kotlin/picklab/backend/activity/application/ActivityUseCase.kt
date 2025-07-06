package picklab.backend.activity.application

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.bookmark.application.BookmarkUseCase
import picklab.backend.bookmark.domain.BookmarkService

@Component
class ActivityUseCase(
    private val activityService: ActivityService,
    private val bookmarkService: BookmarkService,
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
            bookmarkService.getActivityIdsBookmarkedByMember(
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
}
