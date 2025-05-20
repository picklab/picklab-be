package picklab.backend.auth.application

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.auth.infrastructure.GithubUserInfo
import picklab.backend.auth.infrastructure.GoogleUserInfo
import picklab.backend.auth.infrastructure.KakaoUserInfo
import picklab.backend.auth.infrastructure.NaverUserInfo

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
