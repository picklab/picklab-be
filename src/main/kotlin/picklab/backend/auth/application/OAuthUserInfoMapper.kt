package picklab.backend.auth.application

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.auth.infrastructure.GithubUserInfo
import picklab.backend.auth.infrastructure.GoogleUserInfo
import picklab.backend.auth.infrastructure.KakaoUserInfo
import picklab.backend.auth.infrastructure.NaverUserInfo
import picklab.backend.member.domain.enums.SocialType

@Component
class OAuthUserInfoMapper {
    fun map(
        provider: SocialType,
        attributes: JsonNode,
    ): OAuthUserInfo =
        when (provider) {
            SocialType.KAKAO -> KakaoUserInfo(attributes)
            SocialType.NAVER -> NaverUserInfo(attributes)
            SocialType.GOOGLE -> GoogleUserInfo(attributes)
            SocialType.GITHUB -> GithubUserInfo(attributes)
        }
}
