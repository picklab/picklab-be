package picklab.backend.activity.entrypoint

import io.swagger.v3.oas.annotations.Parameter
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import picklab.backend.activity.application.ActivityUseCase
import picklab.backend.activity.entrypoint.mapper.toCommand
import picklab.backend.activity.entrypoint.request.ActivitySearchCondition
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
@RequestMapping("/v1/activities")
class ActivityController(
    private val activityUseCase: ActivityUseCase,
) : ActivityApi {
    @GetMapping("")
    override fun getActivities(
        @ModelAttribute condition: ActivitySearchCondition,
        @Parameter(description = "데이터 개수")
        @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<ResponseWrapper<GetActivityListResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId

        val data =
            activityUseCase.getActivities(
                queryParams = condition.toCommand(),
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
    override fun increaseViewCount(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
        request: HttpServletRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityUseCase.increaseViewCount(activityId, request)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.INCREASE_VIEW_COUNT))
    }

    override fun applyActivity(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        TODO("Not yet implemented")
    }
}
