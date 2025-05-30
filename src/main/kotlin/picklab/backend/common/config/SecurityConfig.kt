package picklab.backend.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import picklab.backend.auth.infrastructure.JwtAccessDeniedHandler
import picklab.backend.auth.infrastructure.JwtAuthenticationEntryPoint
import picklab.backend.auth.infrastructure.JwtAuthenticationFilter
import picklab.backend.auth.infrastructure.JwtExceptionFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtExceptionFilter: JwtExceptionFilter,
    private val objectMapper: ObjectMapper,
) {
    private val readOnlyUrl =
        arrayOf(
            "/favicon.ico",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger",
            "/v1/auth/login/*",
            "/v1/auth/callback/*",
        )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            formLogin { disable() }
            logout { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
            addFilterBefore<JwtAuthenticationFilter>(jwtExceptionFilter)
            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                readOnlyUrl.forEach { path -> authorize(HttpMethod.GET, path, permitAll) }
                authorize(anyRequest, authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = JwtAuthenticationEntryPoint(objectMapper)
                accessDeniedHandler = JwtAccessDeniedHandler(objectMapper)
            }
        }

        return http.build()
    }
}
