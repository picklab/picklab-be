package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Component
class KakaoOAuthProvider(
    @Qualifier("oAuthRestClient")
    private val restClient: RestClient,
    @Value("\${oauth.kakao.client-id}")
    private val clientId: String,
    @Value("\${oauth.kakao.redirect-uri}")
    private val redirectUri: String,
) : OAuthProvider {
    companion object {
        private const val AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize"
        private const val TOKEN_URL = "https://kauth.kakao.com/oauth/token"
        private const val USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
    }

    override fun getAuthorizationUrl(): String =
        UriComponentsBuilder
            .fromUriString(AUTHORIZE_URL)
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .build()
            .toUriString()

    // 단위테스트 -> exception 발생이 잘 되는가
    override fun getToken(authCode: String): JsonNode {
        val uri =
            UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", authCode)
                .build()
                .toUriString()

        return restClient
            .post()
            .uri(uri)
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw RuntimeException("카카오 토큰 응답 실패")
    }

    override fun getUserInfo(accessToken: String): JsonNode {
        val token = getToken(accessToken)
        val userInfo = getUserInfoFromKakao(token["access_token"].asText())
        return userInfo
    }

    // 단위테스트
    private fun getUserInfoFromKakao(accessToken: String): JsonNode =
        restClient
            .get()
            .uri(USER_INFO_URL)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw IllegalStateException("카카오 유저 정보 조회 실패")
}
