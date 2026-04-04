package picklab.backend.activitygroup.entrypoint

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.activitygroup.application.ActivityGroupQueryUseCase
import picklab.backend.activitygroup.application.ActivityGroupUseCase
import picklab.backend.activitygroup.entrypoint.mapper.toResponse
import picklab.backend.activitygroup.entrypoint.request.ActivityGroupCreateRequest
import picklab.backend.activitygroup.entrypoint.response.ActivityGroupResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
@RequestMapping("/v1/activity-groups")
class ActivityGroupController(
    private val activityGroupUseCase: ActivityGroupUseCase,
    private val activityGroupQueryUseCase: ActivityGroupQueryUseCase,
) : ActivityGroupApi {
    @PostMapping("")
    override fun createActivityGroup(
        @Valid @RequestBody request: ActivityGroupCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        activityGroupUseCase.createActivityGroup(request.toCommand())
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.CREATE_ACTIVITY_GROUP))
    }

    @GetMapping("")
    override fun getActivityGroups(): ResponseEntity<ResponseWrapper<List<ActivityGroupResponse>>> {
        val data = activityGroupQueryUseCase.getActivityGroups().map { it.toResponse() }
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_ACTIVITY_GROUPS, data))
    }
}
