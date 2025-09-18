package picklab.backend.activity.application

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.application.model.*
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.service.MemberActivityViewHistoryService

@Component
class ActivityUseCase(
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityQueryService: ActivityQueryService,
    private val activityBookmarkService: ActivityBookmarkService,
    private val viewCountLimiterPort: ViewCountLimiterPort,
    private val memberActivityViewHistoryService: MemberActivityViewHistoryService,
) {
    /**
     * 검색 필터에 일치하는 활동 리스트 및 북마크 여부를 페이징으로 가져옵니다.
     */
    fun getActivities(
        queryParams: ActivitySearchCondition,
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
     * 활동 조회를 기록합니다. 조회수를 증가시키고 로그인한 사용자의 경우 조회 이력을 저장합니다.
     * 조회수 증가는 로컬 캐시를 이용하여 일정 기간 내 일정 횟수만 가능합니다.
     */
    @Transactional
    fun recordActivityView(
        activityId: Long,
        request: HttpServletRequest,
        memberId: Long?,
    ) {
        val activity = activityService.mustFindById(activityId)

        val ip = request.remoteAddr
        val userAgent = request.getHeader("User-Agent")
        val viewIdentifier = "activity:${activity.id}:ip:$ip:userAgent:$userAgent"

        if (viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier)) {
            activity.increaseViewCount()
        }

        // 로그인한 사용자의 경우 조회 이력 저장
        memberId?.let { id ->
            memberActivityViewHistoryService.recordActivityView(id, activity)
        }
    }

    /**
     * 사용자의 직무에 해당하는 추천 활동을 조회합니다.
     */
    fun getRecommendationActivities(condition: RecommendActivitiesCondition): Page<ActivityItemWithBookmark> {
        val member = memberService.findActiveMember(condition.memberId)

        val pageable = PageRequest.of(condition.page - 1, condition.size)
        val myJobIds = memberService.findMyInterestedJobCategoryIds(member)

        val activityPage = activityQueryService.getRecommendationActivities(myJobIds, pageable)
        val activityItems = activityPage.content
        val activityIds = activityItems.map { it.id }

        val bookmarkedActivityIds =
            activityBookmarkService.getMyBookmarkedActivityIds(
                memberId = condition.memberId,
                activityIds = activityIds,
            )

        return activityPage.map {
            ActivityItemWithBookmark.from(
                item = it,
                isBookmarked = bookmarkedActivityIds.contains(it.id),
            )
        }
    }

    /**
     * 전체 활동 중 인기도가 높은 활동들을 조회합니다.
     * 인기도는 조회수와 북마크 수를 합산하여 계산합니다.
     */
    @Transactional(readOnly = true)
    fun getPopularActivities(condition: PopularActivitiesCondition): Page<ActivityItemWithBookmark> {
        val pageable = PageRequest.of(condition.page - 1, condition.size)

        val activityPage = activityService.getPopularActivities(pageable)
        val activityIds = activityPage.content.map { it.id }

        val bookmarkedActivityIds: Set<Long> =
            condition.memberId
                ?.let { activityBookmarkService.getMyBookmarkedActivityIds(it, activityIds) }
                ?: emptySet()

        return activityPage.map {
            ActivityItemWithBookmark.from(
                item = it,
                isBookmarked = bookmarkedActivityIds.contains(it.id),
            )
        }
    }

    /**
     * 사용자가 최근에 조회한 활동들을 조회합니다.
     */
    @Transactional(readOnly = true)
    fun getRecentlyViewedActivities(condition: RecentlyViewedActivitiesCondition): Page<ActivityItemWithBookmark> {
        val pageable = PageRequest.of(condition.page - 1, condition.size)

        val activityPage = activityQueryService.getRecentlyViewedActivities(condition.memberId, pageable)
        val activityIds = activityPage.content.map { it.id }

        val bookmarkedActivityIds: Set<Long> =
            activityBookmarkService.getMyBookmarkedActivityIds(condition.memberId, activityIds)

        return activityPage.map {
            ActivityItemWithBookmark.from(
                item = it,
                isBookmarked = bookmarkedActivityIds.contains(it.id),
            )
        }
    }
}
