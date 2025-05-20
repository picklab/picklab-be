package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class GithubUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    override fun getSocialId(): String = attributes["id"].asText() ?: throw IllegalArgumentException("SocialId is required")

    override fun getName(): String = attributes["name"]?.asText() ?: throw IllegalArgumentException("Name is required")

    override fun getEmail(): String = attributes["email"]?.asText() ?: throw IllegalArgumentException("Email is required")

    override fun getProfileImage(): String =
        attributes["avatar_url"]?.asText() ?: throw IllegalArgumentException("Profile image is required")

    // github 의 경우 생일정보를 제공하지 않음
    override fun getBirthdate(): LocalDate? = null
}
