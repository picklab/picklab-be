package picklab.backend.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import picklab.backend.auth.infrastructure.JwtAuthenticationFilter
import picklab.backend.auth.infrastructure.JwtExceptionFilter
import picklab.backend.auth.infrastructure.JwtTokenProvider
import picklab.backend.auth.infrastructure.WithdrawnMemberTokenBlocklist
import java.time.Duration

@Configuration
class JwtFilterConfig(
    private val objectMapper: ObjectMapper,
    @Qualifier("accessTokenProvider")
    private val accessTokenProvider: JwtTokenProvider,
) {
    @Bean
    fun jwtExceptionFilter(): JwtExceptionFilter = JwtExceptionFilter(objectMapper)

    @Bean
    fun jwtAuthenticationFilter(blocklist: WithdrawnMemberTokenBlocklist): JwtAuthenticationFilter =
        JwtAuthenticationFilter(accessTokenProvider, blocklist)

    @Bean
    fun withdrawnMemberTokenBlocklist(
        @Value("\${jwt.access.expiration}") accessExpiration: Long,
    ): WithdrawnMemberTokenBlocklist = WithdrawnMemberTokenBlocklist(Duration.ofMillis(accessExpiration))
}
