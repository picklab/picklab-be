package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Component
class GithubOAuthProvider(
    @Qualifier("oAuthRestClient")
    private val restClient: RestClient,
    @Value("\${oauth.github.client-id}")
    private val clientId: String,
    @Value("\${oauth.github.client-secret}")
    private val clientSecret: String,
    @Value("\${oauth.github.redirect-uri}")
    private val redirectUri: String,
    @Value("\${oauth.github.scope}")
    private val scope: String,
) : OAuthProvider {
    companion object {
        private const val AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
        private const val TOKEN_URL = "https://github.com/login/oauth/access_token"
        private const val USER_INFO_URL = "https://api.github.com/user"
        private const val EMAIL_INFO_URL = "https://api.github.com/user/emails"
    }

    override fun getAuthorizationUrl(): String =
        UriComponentsBuilder
            .fromUriString(AUTHORIZE_URL)
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("scope", scope.replace(" ", "%20"))
            .build()
            .toUriString()

    override fun getToken(authCode: String): JsonNode =
        restClient
            .post()
            .uri(TOKEN_URL)
            .header("Accept", "application/json")
            .body(
                mapOf(
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "code" to authCode,
                    "redirect_uri" to redirectUri,
                ),
            ).retrieve()
            .body(JsonNode::class.java)
            ?: throw RuntimeException("GitHub 토큰 응답 실패")

    override fun getUserInfo(accessToken: String): JsonNode {
        val token = getToken(accessToken)
        val userInfo = getUserInfoFromGithub(token["access_token"].asText())
        val email = getPrimaryEmailFromGithub(token["access_token"].asText())

        (userInfo as ObjectNode).put("email", email)

        return userInfo
    }

    private fun getUserInfoFromGithub(accessToken: String): JsonNode =
        restClient
            .get()
            .uri(USER_INFO_URL)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw RuntimeException("GitHub 유저 정보 조회 실패")

    private fun getPrimaryEmailFromGithub(accessToken: String): String? {
        val emails =
            restClient
                .get()
                .uri(EMAIL_INFO_URL)
                .header("Authorization", "Bearer $accessToken")
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(JsonNode::class.java)
                ?: return null

        for (emailNode in emails) {
            val isPrimary = emailNode["primary"]?.asBoolean() ?: false
            val isVerified = emailNode["verified"]?.asBoolean() ?: false
            if (isPrimary && isVerified) {
                return emailNode["email"]?.asText()
            }
        }

        return null
    }
}
