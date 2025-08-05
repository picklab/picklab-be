package picklab.backend.activity.application

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.application.model.PopularActivitiesCommand
import picklab.backend.activity.application.model.RecommendActivitiesCommand
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.common.model.PageResponse
import picklab.backend.member.domain.MemberService

@Component
class ActivityUseCase(
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityQueryService: ActivityQueryService,
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
        val isBookmarked =
            memberId?.let { activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId) } ?: false

        return GetActivityDetailResponse.from(
            activity = activity,
            bookmarkCount = bookmarkCount,
            isBookmarked = isBookmarked,
        )
    }

    /**
     * 조회수를 증가시킵니다. 로컬 캐시에 "activity:{activity.id}:ip:{ip}:userAgent:{userAgent}의 키 값을 이용하여
     * 일정 기간 내 일정 횟수의 조회수 증가만 가능합니다.
     */
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
            activity.increaseViewCount()
        }
    }

    /**
     * 사용자의 직무에 해당하는 추천 활동을 조회합니다.
     */
    fun getRecommendationActivities(command: RecommendActivitiesCommand): PageResponse<ActivityItemWithBookmark> {
        val member = memberService.findActiveMember(command.memberId)

        val pageable = PageRequest.of(command.page - 1, command.size)
        val myJobIds = memberService.findMyInterestedJobCategoryIds(member)

        val activityPage = activityQueryService.getRecommendationActivities(myJobIds, pageable)
        val activityItems = activityPage.content
        val activityIds = activityItems.map { it.id }

        val bookmarkedActivityIds =
            activityBookmarkService.getMyBookmarkedActivityIds(
                memberId = command.memberId,
                activityIds = activityIds,
            )

        val itemsPage =
            activityPage.map {
                ActivityItemWithBookmark.from(
                    item = it,
                    isBookmarked = bookmarkedActivityIds.contains(it.id),
                )
            }

        return PageResponse.from(itemsPage)
    }

    /**
     * 전체 활동 중 인기도가 높은 활동들을 조회합니다.
     * 인기도는 조회수와 북마크 수를 합산하여 계산합니다.
     */
    @Transactional(readOnly = true)
    fun getPopularActivities(command: PopularActivitiesCommand): PageResponse<ActivityItemWithBookmark> {
        val pageable = PageRequest.of(command.page - 1, command.size)

        val activityPage = activityService.getPopularActivities(pageable)
        val activityIds = activityPage.content.map { it.id }

        val bookmarkedActivityIds: Set<Long> =
            command.memberId
                ?.let { activityBookmarkService.getMyBookmarkedActivityIds(it, activityIds) }
                ?: emptySet()

        val itemsPage =
            activityPage.map {
                ActivityItemWithBookmark.from(
                    item = it,
                    isBookmarked = bookmarkedActivityIds.contains(it.id),
                )
            }

        return PageResponse.from(itemsPage)
    }
}
