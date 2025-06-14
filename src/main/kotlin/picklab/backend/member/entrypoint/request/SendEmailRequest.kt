package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendEmailRequest(
    @field:NotBlank
    @field:Email(message = "유효하지 않은 이메일 형식입니다.")
    @field:Schema(description = "이메일", example = "test@example.com")
    val email: String,
)
