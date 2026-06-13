package picklab.backend.member.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class NicknameAvailabilityResponse(
    @field:Schema(description = "닉네임 사용 가능 여부")
    val available: Boolean,
)
