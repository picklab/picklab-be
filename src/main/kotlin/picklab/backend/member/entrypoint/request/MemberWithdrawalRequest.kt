package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import picklab.backend.member.domain.enums.WithdrawalType

data class MemberWithdrawalRequest(
    @field:Schema(description = "탈퇴 사유", example = "LACK_OF_CONTENT")
    val reason: WithdrawalType,
    @field:Size(max = 500, message = "글자 수를 초과하였습니다.")
    @field:Schema(description = "탈퇴 상세 사유(기타일 시 추가 입력란)", example = "다른 아이디로 이용할게요.")
    val detail: String?,
)
