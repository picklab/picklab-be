package picklab.backend.auth.application

import org.springframework.stereotype.Service

@Service
class AuthUseCase(
    private val oAuthProviderResolver: OAuthProviderResolver,
    private val oAuthUserInfoMapper: OAuthUserInfoMapper,
) {
    fun handleOAuthCallback(
        provider: String,
        code: String,
    ) {
        val oAuthProvider = oAuthProviderResolver.resolve(provider)
        val userInfo = oAuthProvider.getUserInfo(code)
        val mappedUserInfo = oAuthUserInfoMapper.map(provider, userInfo)
    }
}
