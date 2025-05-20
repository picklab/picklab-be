package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class GoogleUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    override fun getSocialId(): String = attributes["id"].asText() ?: throw IllegalArgumentException("SocialId is required")

    override fun getName(): String =
        attributes["name"]?.asText()
            ?: throw IllegalArgumentException("Name is required")

    override fun getEmail(): String =
        attributes["email"]?.asText()
            ?: throw IllegalArgumentException("Email is required")

    override fun getProfileImage(): String = attributes["picture"]?.asText() ?: throw IllegalArgumentException("Profile image is required")

    override fun getBirthdate(): LocalDate? {
        val birthdateString = attributes["birthdate"]?.asText() ?: return null
        return LocalDate.parse(birthdateString)
    }
}
