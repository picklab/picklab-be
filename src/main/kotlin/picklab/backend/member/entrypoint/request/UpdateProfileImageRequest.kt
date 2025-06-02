package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotBlank

data class UpdateProfileImageRequest(
    @field:NotBlank
    val profileImage: String,
)
