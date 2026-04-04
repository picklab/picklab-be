package picklab.backend.activitygroup.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import picklab.backend.activitygroup.application.model.ActivityGroupCreateCommand

@Schema(description = "활동 그룹 생성 요청")
data class ActivityGroupCreateRequest(
    @field:NotBlank
    @field:Size(max = 100)
    @field:Schema(description = "활동 그룹 이름", example = "대외활동")
    val name: String,
    @field:NotBlank
    @field:Size(max = 255)
    @field:Schema(description = "활동 그룹 설명", example = "대외활동 관련 그룹입니다.")
    val description: String,
) {
    fun toCommand(): ActivityGroupCreateCommand =
        ActivityGroupCreateCommand(
            name = name,
            description = description,
        )
}
