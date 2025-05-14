package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode

interface OAuthProvider {
    fun getAuthorizationUrl(): String

    fun getToken(authCode: String): JsonNode

    fun getUserInfo(accessToken: String): JsonNode
}
