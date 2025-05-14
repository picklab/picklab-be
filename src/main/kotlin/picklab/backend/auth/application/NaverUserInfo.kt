package picklab.backend.auth.application

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NaverUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    override fun getSocialId(): String = attributes["response"].get("id").asText()

    override fun getName(): String? = attributes["response"]?.get("name")?.asText()

    // 네이버 연락처 이메일 -> ~~@naver.com 형식이 아닐 수 있음
    override fun getEmail(): String? = attributes["response"]?.get("email")?.asText()

    override fun getProfileImage(): String? = attributes["response"]?.get("profile_image")?.asText()

    override fun getBirthdate(): LocalDate? {
        val birthday = attributes["response"]?.get("birthday")?.asText()
        val birthyear = attributes["response"]?.get("birthyear")?.asText()

        var birthDate: LocalDate? = null
        if (!birthyear.isNullOrBlank() && !birthday.isNullOrBlank()) {
            val fullDate = "$birthyear-$birthday"
            birthDate = LocalDate.parse(fullDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        return birthDate
    }
}
