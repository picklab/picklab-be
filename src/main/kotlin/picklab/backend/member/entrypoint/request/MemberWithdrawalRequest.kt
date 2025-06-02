package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.Size
import picklab.backend.member.domain.enums.WithdrawalType

data class MemberWithdrawalRequest(
    val reason: WithdrawalType,
    @field:Size(max = 500, message = "글자 수를 초과하였습니다.")
    val detail: String?,
)
