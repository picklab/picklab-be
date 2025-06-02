package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotBlank

data class JobCategoryRequest(
    @field:NotBlank
    val group: String,
    @field:NotBlank
    val detail: String,
)
