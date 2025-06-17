package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerifyEmailCodeRequest(
    @field:NotBlank(message = "인증 코드는 필수 입력값입니다.")
    @field:Pattern(
        regexp = "^[0-9]{6}$",
        message = "인증 코드는 6자리 영문 대소문자와 숫자로만 구성되어야 합니다.",
    )
    @field:Schema(description = "인증 코드", example = "123456")
    val code: String,
)
