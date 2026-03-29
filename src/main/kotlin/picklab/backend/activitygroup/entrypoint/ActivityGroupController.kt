package picklab.backend.activitygroup.entrypoint

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.activitygroup.application.ActivityGroupUseCase
import picklab.backend.activitygroup.entrypoint.request.ActivityGroupCreateRequest
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
@RequestMapping("/v1/activity-groups")
class ActivityGroupController(
    private val activityGroupUseCase: ActivityGroupUseCase,
) : ActivityGroupApi {
    @PostMapping("")
    override fun createActivityGroup(
        @Valid @RequestBody request: ActivityGroupCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityGroupUseCase.createActivityGroup(request.toCommand())
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.CREATE_ACTIVITY_GROUP))
    }
}
