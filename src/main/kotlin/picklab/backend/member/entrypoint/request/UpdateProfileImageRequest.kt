package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class UpdateProfileImageRequest(
    @field:NotBlank(message = "프로필 이미지 값은 필수입니다.")
    @field:JsonProperty("profile_image")
    @field:Schema(
        description = "업로드한 PROFILE 파일의 object_key. 버킷 경로가 포함된 쿼리 없는 임시 URL도 허용합니다.",
        example = "temp/profile/1/abcd1234_20261002_120000.jpg",
    )
    val profileImage: String,
)
