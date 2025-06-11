package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class UpdateProfileImageRequest(
    @field:NotBlank
    @field:JsonProperty("profile_image")
    @field:Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    val profileImage: String,
)
