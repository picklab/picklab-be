package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Component
class NaverOAuthProvider(
    @Qualifier("oAuthRestClient")
    private val restClient: RestClient,
    @Value("\${oauth.naver.client-id}")
    private val clientId: String,
    @Value("\${oauth.naver.client-secret}")
    private val clientSecret: String,
    @Value("\${oauth.naver.redirect-uri}")
    private val redirectUri: String,
) : OAuthProvider {
    companion object {
        private const val AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize"
        private const val TOKEN_URL = "https://nid.naver.com/oauth2.0/token"
        private const val USER_INFO_URL = "https://openapi.naver.com/v1/nid/me"
    }

    override fun getAuthorizationUrl(): String =
        UriComponentsBuilder
            .fromUriString(AUTHORIZE_URL)
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("state", "RANDOM_STATE") // CSRF 보호용
            .build()
            .toUriString()

    override fun getToken(authCode: String): JsonNode {
        val uri =
            UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", authCode)
                .queryParam("state", "RANDOM_STATE")
                .build()
                .toUriString()

        return restClient
            .post()
            .uri(uri)
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw RuntimeException("네이버 토큰 응답 실패")
    }

    override fun getUserInfo(accessToken: String): JsonNode {
        val token = getToken(accessToken)
        val userInfo = getUserInfoFromNaver(token["access_token"].asText())
        return userInfo
    }

    private fun getUserInfoFromNaver(accessToken: String): JsonNode =
        restClient
            .get()
            .uri(USER_INFO_URL)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw RuntimeException("네이버 유저 정보 조회 실패")
}
