package picklab.backend.auth.application

import org.springframework.stereotype.Component
import picklab.backend.auth.domain.AuthToken
import picklab.backend.auth.infrastructure.AccessTokenProvider
import picklab.backend.auth.infrastructure.RefreshTokenProvider
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService

@Component
class TokenUseCase(
    private val refreshTokenProvider: RefreshTokenProvider,
    private val memberService: MemberService,
    private val accessTokenProvider: AccessTokenProvider,
) {
    fun refreshAccessToken(refreshToken: String): AuthToken {
        if (!refreshTokenProvider.validateToken(refreshToken)) {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }
        val tokenType = refreshTokenProvider.getTokenType(refreshToken)
        if (tokenType != "refresh") {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }

        val memberId = refreshTokenProvider.getSubject(refreshToken).toLong()
        val member = memberService.findActiveMember(memberId)
        if (member.refreshToken != refreshToken) {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }
        val newAccessToken = accessTokenProvider.generateToken(memberId)

        return AuthToken(
            accessToken = newAccessToken,
            refreshToken = refreshToken,
        )
    }
}
