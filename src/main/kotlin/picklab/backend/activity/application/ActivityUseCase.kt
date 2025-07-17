package picklab.backend.activity.application

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
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
    private val viewCountLimiterPort: ViewCountLimiterPort,
) {
    /**
     * 검색 필터에 일치하는 활동 리스트 및 북마크 여부를 페이징으로 가져옵니다.
     */
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

    /**
     * 활동 ID에 해당하는 활동의 상세 데이터를 가져옵니다.
     */
    fun getActivityDetail(
        activityId: Long,
        memberId: Long?,
    ): GetActivityDetailResponse {
        val activity = activityService.mustFindById(activityId)

        val bookmarkCount = activityBookmarkService.countByActivityId(activityId)
        val isBookmarked = memberId?.let { activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId) } ?: false

        return GetActivityDetailResponse.from(
            activity = activity,
            bookmarkCount = bookmarkCount,
            isBookmarked = isBookmarked,
        )
    }

    @Transactional
    fun increaseViewCount(
        activityId: Long,
        request: HttpServletRequest,
    ) {
        val activity = activityService.mustFindById(activityId)

        val ip = request.remoteAddr
        val userAgent = request.getHeader("User-Agent")
        val viewIdentifier = "activity:${activity.id}:ip:$ip:userAgent:$userAgent"

        if (viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier)) {
            activityService.increaseViewCount(activity)
        }
    }
}
