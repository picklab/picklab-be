package picklab.backend.participation.entrypoint

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.participation.application.ActivityParticipationUseCase
import picklab.backend.participation.entrypoint.response.GetActivityApplicationUrlResponse

@RestController
class ActivityParticipationController(
    private val activityParticipationUseCase: ActivityParticipationUseCase,
) : ActivityParticipationApi {
    @GetMapping("/v1/activities/{activityId}/application-url")
    override fun getActivityApplicationUrl(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<GetActivityApplicationUrlResponse>> =
        activityParticipationUseCase
            .getActivityApplicationUrl(activityId)
            .let {
                ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITY_APPLICATION_URL, it))
            }
}
