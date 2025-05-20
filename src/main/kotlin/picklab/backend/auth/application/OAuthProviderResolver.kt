package picklab.backend.auth.application

import org.springframework.stereotype.Component
import picklab.backend.auth.infrastructure.*
import picklab.backend.member.domain.enums.SocialType

@Component
class OAuthProviderResolver(
    private val kakaoOAuthProvider: KakaoOAuthProvider,
    private val naverOAuthProvider: NaverOAuthProvider,
    private val githubOAuthProvider: GithubOAuthProvider,
    private val googleOAuthProvider: GoogleOAuthProvider,
) {
    fun resolve(provider: SocialType): OAuthProvider =
        when (provider) {
            SocialType.KAKAO -> kakaoOAuthProvider
            SocialType.NAVER -> naverOAuthProvider
            SocialType.GITHUB -> githubOAuthProvider
            SocialType.GOOGLE -> googleOAuthProvider
        }
}
