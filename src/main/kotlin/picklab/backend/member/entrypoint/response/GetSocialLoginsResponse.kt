package picklab.backend.member.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class GetSocialLoginsResponse(
    @field:Schema(description = "로그인 유형")
    val loginType: List<String>,
)
