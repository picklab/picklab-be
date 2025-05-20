package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class GithubUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    private val socialId = attributes["id"]?.asText() ?: throw IllegalArgumentException("SocialId is required")
    private val name = attributes["name"]?.asText() ?: throw IllegalArgumentException("Name is required")
    private val email = attributes["email"]?.asText() ?: throw IllegalArgumentException("Email is required")
    private val profileImage = attributes["avatar_url"]?.asText() ?: throw IllegalArgumentException("Profile image is required")
    private val birthdate = null

    override fun getSocialId(): String = socialId

    override fun getName(): String = name

    override fun getEmail(): String = email

    override fun getProfileImage(): String = profileImage

    // github 의 경우 생일정보를 제공하지 않음
    override fun getBirthdate(): LocalDate? = birthdate
}
