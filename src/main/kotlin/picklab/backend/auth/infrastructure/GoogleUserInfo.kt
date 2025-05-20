package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class GoogleUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    private val socialId = attributes["id"]?.asText() ?: throw IllegalArgumentException("SocialId is required")
    private val name = attributes["name"]?.asText() ?: throw IllegalArgumentException("Name is required")
    private val email = attributes["email"]?.asText() ?: throw IllegalArgumentException("Email is required")
    private val profileImage = attributes["picture"]?.asText() ?: throw IllegalArgumentException("Profile image is required")
    private val birthdate = attributes["birthdate"]?.asText()

    override fun getSocialId(): String = socialId

    override fun getName(): String = name

    override fun getEmail(): String = email

    override fun getProfileImage(): String = profileImage

    override fun getBirthdate(): LocalDate? =
        birthdate?.let {
            LocalDate.parse(it)
        }
}
