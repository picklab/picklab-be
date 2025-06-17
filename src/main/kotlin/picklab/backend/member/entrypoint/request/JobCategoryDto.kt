package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class JobCategoryDto(
    @field:NotBlank(message = "직무 대분류는 필수 입력값입니다.")
    @field:Schema(description = "대분류", example = "PLANNING")
    val group: String,
    @field:NotBlank(message = "상세 직무 옵션은 필수 입력값입니다.")
    @field:Schema(description = "상세 직무 옵션", example = "SERVICE_PLANNING")
    val detail: String,
)
