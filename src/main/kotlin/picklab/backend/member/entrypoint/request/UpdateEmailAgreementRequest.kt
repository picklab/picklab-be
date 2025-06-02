package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotNull

data class UpdateEmailAgreementRequest(
    @field:NotNull
    val emailAgreement: Boolean,
)
