package picklab.backend.auth.application

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class GithubUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    override fun getSocialId(): String = attributes["id"].asText()

    override fun getName(): String? = attributes["name"]?.asText()

    override fun getEmail(): String? = attributes["email"]?.asText()

    override fun getProfileImage(): String? = attributes["avatar_url"]?.asText()

    // github 의 경우 생일정보를 제공하지 않음
    override fun getBirthdate(): LocalDate? = null
}
