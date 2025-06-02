package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotBlank

data class JobCategoryDto(
    @field:NotBlank
    val group: String,
    @field:NotBlank
    val detail: String,
)
