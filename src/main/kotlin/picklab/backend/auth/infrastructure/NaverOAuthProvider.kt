package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import picklab.backend.auth.domain.AuthException
import picklab.backend.common.model.ErrorCode

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
            .build()
            .toUriString()

    override fun getToken(code: String): JsonNode {
        val uri =
            UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .build()
                .toUriString()

        return restClient
            .post()
            .uri(uri)
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw AuthException(ErrorCode.SOCIAL_CODE_ERROR)
    }

    override fun getUserInfo(code: String): JsonNode {
        val token = getToken(code)
        val userInfo = getUserInfoFromNaver(token["access_token"].asText())
        return userInfo["response"]
    }

    private fun getUserInfoFromNaver(accessToken: String): JsonNode =
        restClient
            .get()
            .uri(USER_INFO_URL)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw AuthException(ErrorCode.SOCIAL_USER_INFO_ERROR)
}
