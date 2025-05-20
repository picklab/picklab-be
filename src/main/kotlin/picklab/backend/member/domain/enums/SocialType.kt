package picklab.backend.member.domain.enums

enum class SocialType {
    KAKAO,
    NAVER,
    GOOGLE,
    GITHUB, ;

    companion object {
        fun from(value: String): SocialType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다: $value")
    }
}
