package picklab.backend.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import picklab.backend.auth.infrastructure.JwtAuthenticationFilter
import picklab.backend.auth.infrastructure.JwtExceptionFilter
import picklab.backend.auth.infrastructure.JwtTokenProvider

@Configuration
class JwtFilterConfig(
    private val objectMapper: ObjectMapper,
    @Qualifier("accessTokenProvider")
    private val accessTokenProvider: JwtTokenProvider,
) {
    @Bean
    fun jwtExceptionFilter(): JwtExceptionFilter = JwtExceptionFilter(objectMapper)

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter = JwtAuthenticationFilter(accessTokenProvider)
}
