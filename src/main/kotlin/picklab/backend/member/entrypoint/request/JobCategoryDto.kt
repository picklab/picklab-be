package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class JobCategoryDto(
    @field:NotBlank
    @field:Schema(description = "대분류", example = "PLANNING")
    val group: String,
    @field:NotBlank
    @field:Schema(description = "상세 직무 옵션", example = "SERVICE_PLANNING")
    val detail: String,
)
