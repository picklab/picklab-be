package picklab.backend.activity.entrypoint

import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import picklab.backend.activity.application.ActivityUseCase
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.entrypoint.mapper.toCondition
import picklab.backend.activity.entrypoint.mapper.toPopularActivitiesCondition
import picklab.backend.activity.entrypoint.mapper.toRecentlyViewedActivitiesCondition
import picklab.backend.activity.entrypoint.mapper.toRecommendActivitiesCondition
import picklab.backend.activity.entrypoint.request.ActivitySearchRequest
import picklab.backend.activity.entrypoint.request.GetActivityPageRequest
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
@RequestMapping("/v1/activities")
class ActivityController(
    private val activityUseCase: ActivityUseCase,
) : ActivityApi {
    @GetMapping("")
    override fun getActivities(
        @ModelAttribute condition: ActivitySearchRequest,
        @Parameter(description = "데이터 개수")
        @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<ResponseWrapper<GetActivityListResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId

        val data =
            activityUseCase.getActivities(
                queryParams = condition.toCondition(),
                size = size,
                page = page,
                memberId = memberId,
            )
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITIES, data))
    }

    @GetMapping("/{activityId}")
    override fun getActivitiesDetail(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<GetActivityDetailResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId

        val data = activityUseCase.getActivityDetail(activityId, memberId)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITY_DETAIL, data))
    }

    @PostMapping("/{activityId}/view")
    override fun recordActivityView(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId

        activityUseCase.recordActivityView(activityId, request, memberId)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.INCREASE_VIEW_COUNT))
    }

    @GetMapping("/recommendations")
    override fun getRecommendationActivities(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @ModelAttribute request: GetActivityPageRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>> {
        val data = activityUseCase.getRecommendationActivities(request.toRecommendActivitiesCondition(member.memberId))
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITIES, data))
    }

    @GetMapping("/popular")
    override fun getWeeklyPopularActivities(
        @Valid @ModelAttribute request: GetActivityPageRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId

        val data = activityUseCase.getPopularActivities(request.toPopularActivitiesCondition(memberId))
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITIES, data))
    }

    @GetMapping("/recently-viewed")
    override fun getRecentlyViewedActivities(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @ModelAttribute request: GetActivityPageRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>> {
        val data =
            activityUseCase.getRecentlyViewedActivities(request.toRecentlyViewedActivitiesCondition(member.memberId))
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITIES, data))
    }
}
