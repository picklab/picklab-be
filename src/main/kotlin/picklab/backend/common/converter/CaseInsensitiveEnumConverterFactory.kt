package picklab.backend.common.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

class CaseInsensitiveEnumConverterFactory : ConverterFactory<String, Enum<*>> {
    override fun <T : Enum<*>> getConverter(targetType: Class<T>): Converter<String, T> = CaseInsensitiveEnumConverter(targetType)

    private class CaseInsensitiveEnumConverter<T : Enum<*>>(
        private val enumType: Class<T>,
    ) : Converter<String, T> {
        override fun convert(source: String): T =
            enumType.enumConstants.firstOrNull {
                it.name.equals(source, ignoreCase = true)
            } ?: throw IllegalArgumentException()
    }
}
