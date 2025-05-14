package picklab.backend.auth.application

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import picklab.backend.auth.domain.OAuthUserInfo

@Component
class OAuthUserInfoMapper {
    fun map(
        provider: String,
        attributes: JsonNode,
    ): OAuthUserInfo =
        when (provider.lowercase()) {
            "kakao" -> KakaoUserInfo(attributes)
            "naver" -> NaverUserInfo(attributes)
            "google" -> GoogleUserInfo(attributes)
            "github" -> GithubUserInfo(attributes)
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }
}
