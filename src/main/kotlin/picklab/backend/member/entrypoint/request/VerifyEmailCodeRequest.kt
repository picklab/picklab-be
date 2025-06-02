package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerifyEmailCodeRequest(
    @field:NotBlank
    @field:Pattern(
        regexp = "^[0-9]{6}$",
        message = "인증 코드는 6자리 영문 대소문자와 숫자로만 구성되어야 합니다.",
    )
    val code: String,
)
