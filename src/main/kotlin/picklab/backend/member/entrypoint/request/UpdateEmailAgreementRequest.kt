package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateEmailAgreementRequest(
    @field:JsonProperty("email_agreement")
    @field:Schema(description = "이메일 마케팅 수신 동의 여부", example = "true")
    val emailAgreement: Boolean,
)
