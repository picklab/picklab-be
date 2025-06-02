package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SendEmailRequest(
    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[-A-Za-z0-9-]+)*(\\.[[A-Za-z]{2,})$",
        message = "유효하지 않은 이메일 형식입니다.",
    )
    val email: String,
)
