package picklab.backend.activitygroup.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "활동 그룹 응답")
data class ActivityGroupResponse(
    @field:Schema(description = "활동 그룹 ID", example = "1")
    val id: Long,
    @field:Schema(description = "활동 그룹 이름", example = "대외활동")
    val name: String,
    @field:Schema(description = "활동 그룹 설명", example = "대외활동 관련 그룹입니다.")
    val description: String,
)
