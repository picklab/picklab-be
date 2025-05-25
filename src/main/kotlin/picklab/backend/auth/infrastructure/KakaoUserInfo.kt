package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.JsonNode
import picklab.backend.auth.domain.AuthException
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.common.model.ErrorCode

class KakaoUserInfo(
    private val attributes: JsonNode,
) : OAuthUserInfo {
    private val socialId = attributes["id"]?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_ID)
    private val name = attributes["properties"]?.get("nickname")?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_NAME)
    private val email = attributes["kakao_account"]?.get("email")?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_EMAIL)
    private val profileImage =
        attributes["properties"]?.get("profile_image")?.asText() ?: throw AuthException(ErrorCode.EMPTY_SOCIAL_PROFILE_IMAGE)
    private val birthdate = null

    override fun getSocialId() = socialId

    // 이름의 경우 사업자 등록번호를 등록해야 수집 가능 -> nickname 으로 임시 대체
    override fun getName(): String = name

    override fun getEmail() = email

    override fun getProfileImage() = profileImage

    // 생일, 출생연도의 경우 사업자 등록번호를 등록해야 수집 가능
    override fun getBirthdate() = birthdate
}
