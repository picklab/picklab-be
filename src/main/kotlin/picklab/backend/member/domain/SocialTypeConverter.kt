package picklab.backend.member.domain

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import picklab.backend.member.domain.enums.SocialType

@Component
class SocialTypeConverter : Converter<String, SocialType> {
    override fun convert(source: String): SocialType =
        SocialType.entries.firstOrNull { it.name.equals(source, ignoreCase = true) }
            ?: throw IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: $source")
}
