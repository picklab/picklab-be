package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateInfoRequest(
    @field:NotBlank
    @field:Pattern(
        regexp = "^[a-zA-Z가-힣]{1,20}$]",
        message = "이름은 영문, 한글만 사용 가능하며 최대 20자까지 입력 가능합니다.",
    )
    val name: String,
    @field:NotBlank
    @field:Pattern(
        regexp = "^[a-zA-Z0-9가-힣_.-]{1,20}$",
        message = "닉네임은 영문, 숫자, 한글, _, -, .만 사용 가능하며 최대 20자까지 입력 가능합니다.",
    )
    val nickname: String,
    @field:NotBlank
    val educationLevel: String,
    @field:NotBlank
    val school: String,
    @field:NotBlank
    val graduationStatus: String,
    val employmentStatus: String,
    val company: String,
    val employmentType: String,
)
