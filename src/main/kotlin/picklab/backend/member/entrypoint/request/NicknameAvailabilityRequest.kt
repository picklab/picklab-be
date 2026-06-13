package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class NicknameAvailabilityRequest(
    @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9가-힣_.-]{1,20}$",
        message = "닉네임은 영문, 숫자, 한글, _, -, .만 사용 가능하며 최대 20자까지 입력 가능합니다.",
    )
    @field:Schema(description = "중복 확인할 닉네임", example = "picklab멤버", maxLength = 20)
    val nickname: String,
)
