package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import picklab.backend.auth.domain.AuthException
import picklab.backend.common.model.ErrorCode
import java.time.LocalDate

@Component
class GoogleOAuthProvider(
    @Qualifier("oAuthRestClient")
    private val restClient: RestClient,
    @Value("\${oauth.google.client-id}")
    private val clientId: String,
    @Value("\${oauth.google.client-secret}")
    private val clientSecret: String,
    @Value("\${oauth.google.redirect-uri}")
    private val redirectUri: String,
    @Value("\${oauth.google.scope}")
    private val scope: String,
) : OAuthProvider {
    companion object {
        private const val AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth"
        private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo"
        private const val BIRTHDAY_INFO_URL = "https://people.googleapis.com/v1/people/me?personFields=birthdays"
    }

    override fun getAuthorizationUrl(): String =
        UriComponentsBuilder
            .fromUriString(AUTHORIZE_URL)
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam(
                "scope",
                scope.replace(" ", "%20"),
            ).build()
            .toUriString()

    override fun getToken(code: String): JsonNode {
        val formData =
            LinkedMultiValueMap<String, String>().apply {
                add("code", code)
                add("client_id", clientId)
                add("client_secret", clientSecret)
                add("redirect_uri", redirectUri)
                add("grant_type", "authorization_code")
            }

        val tokenResponse =
            restClient
                .post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(JsonNode::class.java)

        return tokenResponse
            ?: throw AuthException(ErrorCode.SOCIAL_CODE_ERROR)
    }

    override fun getUserInfo(code: String): JsonNode {
        val token = getToken(code)
        val userInfo = getBasicUserInfoFromGoogle(token["access_token"].asText())
        val birthdayInfo = getBirthdayInfoFromGoogle(token["access_token"].asText())

        (userInfo as ObjectNode).put("birthdays", birthdayInfo.toString())

        return userInfo
    }

    private fun getBasicUserInfoFromGoogle(accessToken: String): JsonNode =
        restClient
            .get()
            .uri(USER_INFO_URL)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw AuthException(ErrorCode.SOCIAL_USER_INFO_ERROR)

    private fun getBirthdayInfoFromGoogle(accessToken: String): LocalDate? {
        val birthdays =
            restClient
                .get()
                .uri(BIRTHDAY_INFO_URL)
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .body(JsonNode::class.java)
                ?: throw RuntimeException("구글 생일 정보 조회 실패")

        for (birthday in birthdays) {
            val metadata = birthday["metadata"]
            val isPrimary = metadata?.get("primary")?.asBoolean() ?: false

            if (isPrimary) {
                val date = birthday["date"]
                val year = date["year"]?.asInt()
                val month = date["month"]?.asInt()
                val day = date["day"]?.asInt()
                if (year != null && month != null && day != null) {
                    return LocalDate.of(year, month, day)
                }
            }
        }

        val fallback = birthdays.firstOrNull()?.get("date")
        val year = fallback?.get("year")?.asInt()
        val month = fallback?.get("month")?.asInt()
        val day = fallback?.get("day")?.asInt()

        return if (year != null && month != null && day != null) {
            LocalDate.of(year, month, day)
        } else {
            null
        }
    }
}
