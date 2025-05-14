package picklab.backend.auth.application

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class GoogleUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    override fun getSocialId(): String = attributes["id"].asText()

    override fun getName(): String? = attributes["name"]?.asText()

    override fun getEmail(): String? = attributes["email"]?.asText()

    override fun getProfileImage(): String? = attributes["picture"]?.asText()

    override fun getBirthdate(): LocalDate? {
        val birthdateString = attributes["birthdate"]?.asText() ?: return null
        return LocalDate.parse(birthdateString)
    }
}
