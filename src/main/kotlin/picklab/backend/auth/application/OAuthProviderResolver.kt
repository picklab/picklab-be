package picklab.backend.auth.application

import org.springframework.stereotype.Component
import picklab.backend.auth.infrastructure.*

@Component
class OAuthProviderResolver(
    private val kakaoOAuthProvider: KakaoOAuthProvider,
    private val naverOAuthProvider: NaverOAuthProvider,
    private val githubOAuthProvider: GithubOAuthProvider,
    private val googleOAuthProvider: GoogleOAuthProvider,
) {
    fun resolve(provider: String): OAuthProvider =
        when (provider) {
            "kakao" -> kakaoOAuthProvider
            "naver" -> naverOAuthProvider
            "github" -> githubOAuthProvider
            "google" -> googleOAuthProvider
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }
}
