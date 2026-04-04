package picklab.backend.activitygroup.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import picklab.backend.activitygroup.entrypoint.request.ActivityGroupCreateRequest
import picklab.backend.activitygroup.entrypoint.response.ActivityGroupResponse
import picklab.backend.common.model.ResponseWrapper

@Tag(name = "활동 그룹 API", description = "활동 그룹 관련 API")
interface ActivityGroupApi {
    @Operation(
        summary = "활동 그룹 생성",
        description = "새로운 활동 그룹을 생성합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "활동 그룹 생성에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
        ],
    )
    fun createActivityGroup(
        @RequestBody request: ActivityGroupCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "활동 그룹 목록 조회",
        description = "등록된 활동 그룹 목록을 조회합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "활동 그룹 목록 조회에 성공했습니다."),
        ],
    )
    fun getActivityGroups(): ResponseEntity<ResponseWrapper<List<ActivityGroupResponse>>>
}
