package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode

interface OAuthProvider {
    fun getAuthorizationUrl(): String

    fun getToken(code: String): JsonNode

    fun getUserInfo(code: String): JsonNode
}
