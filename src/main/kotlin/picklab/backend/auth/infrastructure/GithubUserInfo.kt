package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.AuthException
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.common.model.ErrorCode
import java.time.LocalDate

class GithubUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    private val socialId = attributes["id"]?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_ID)
    private val name = attributes["name"]?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_NAME)
    private val email = attributes["email"]?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_EMAIL)
    private val profileImage = attributes["avatar_url"]?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_PROFILE_IMAGE)
    private val birthdate = null

    override fun getSocialId(): String = socialId

    override fun getName(): String = name

    override fun getEmail(): String = email

    override fun getProfileImage(): String = profileImage

    // github 의 경우 생일정보를 제공하지 않음
    override fun getBirthdate(): LocalDate? = birthdate
}
