package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NaverUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    private val socialId = attributes["id"]?.asText() ?: throw IllegalArgumentException("SocialId is required")
    private val name = attributes["name"]?.asText() ?: throw IllegalArgumentException("Name is required")
    private val email = attributes["email"]?.asText() ?: throw IllegalArgumentException("Email is required")
    private val profileImage =
        attributes["profile_image"]?.asText() ?: throw IllegalArgumentException("Profile image is required")
    private val birthYear = attributes["birthyear"]?.asText()
    private val birthDay = attributes["birthday"]?.asText()

    override fun getSocialId(): String = socialId

    override fun getName(): String = name

    // 네이버 연락처 이메일 -> ~~@naver.com 형식이 아닐 수 있음
    override fun getEmail(): String = email

    override fun getProfileImage(): String = profileImage

    override fun getBirthdate(): LocalDate? {
        var birthDate: LocalDate? = null
        if (!birthYear.isNullOrBlank() && !birthDay.isNullOrBlank()) {
            val fullDate = "$birthYear-$birthDay"
            birthDate = LocalDate.parse(fullDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        return birthDate
    }
}
