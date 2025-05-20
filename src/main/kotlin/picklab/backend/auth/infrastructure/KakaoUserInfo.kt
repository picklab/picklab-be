package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.OAuthUserInfo
import java.time.LocalDate

class KakaoUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    override fun getSocialId(): String = attributes["id"].asText() ?: throw IllegalArgumentException("SocialId is required")

    // 이름의 경우 사업자 등록번호를 등록해야 수집 가능 -> nickname 으로 임시 대체
    override fun getName(): String =
        attributes["properties"]?.get("nickname")?.asText() ?: throw IllegalArgumentException("Name is required")

    override fun getEmail(): String =
        attributes["kakao_account"]?.get("email")?.asText() ?: throw IllegalArgumentException("Email is required")

    override fun getProfileImage(): String =
        attributes["properties"]?.get("profile_image")?.asText() ?: throw IllegalArgumentException("Profile image is required")

    // 생일, 출생연도의 경우 사업자 등록번호를 등록해야 수집 가능
    override fun getBirthdate(): LocalDate? = null
}
