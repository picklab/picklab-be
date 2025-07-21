package picklab.backend.participation.entrypoint

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.participation.application.ActivityParticipationUseCase

@RestController
class ActivityParticipationController(
    private val activityParticipationUseCase: ActivityParticipationUseCase,
) : ActivityParticipationApi {
    @PostMapping("/v1/activities/{activityId}/apply")
    override fun applyActivity(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityParticipationUseCase.applyToActivity(member.memberId, activityId)
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.APPLY_ACTIVITY))
    }
}
