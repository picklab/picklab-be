package picklab.backend.auth.application

import org.springframework.stereotype.Service
import picklab.backend.auth.domain.TokenResponse
import picklab.backend.auth.infrastructure.AccessTokenProvider
import picklab.backend.auth.infrastructure.RefreshTokenProvider
import picklab.backend.member.domain.MemberService

@Service
class AuthUseCase(
    private val oAuthProviderResolver: OAuthProviderResolver,
    private val oAuthUserInfoMapper: OAuthUserInfoMapper,
    private val memberService: MemberService,
    private val accessTokenProvider: AccessTokenProvider,
    private val refreshTokenProvider: RefreshTokenProvider,
) {
    fun handleOAuthCallback(
        provider: String,
        code: String,
    ): TokenResponse {
        val oAuthProvider = oAuthProviderResolver.resolve(provider)
        val userInfo = oAuthProvider.getUserInfo(code)
        val mappedUserInfo = oAuthUserInfoMapper.map(provider, userInfo)

        val loginMember = memberService.loginOrSignup(provider, mappedUserInfo)

        val accessToken = accessTokenProvider.generateToken(loginMember.id)
        val refreshToken = refreshTokenProvider.generateToken(loginMember.id)

        memberService.saveRefreshToken(
            memberId = loginMember.id,
            refreshToken = refreshToken,
        )

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
