package picklab.backend.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
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
            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                readOnlyUrl.forEach { path -> authorize(HttpMethod.GET, path, permitAll) }
                authorize(anyRequest, authenticated)
            }
        }

        return http.build()
    }
}
