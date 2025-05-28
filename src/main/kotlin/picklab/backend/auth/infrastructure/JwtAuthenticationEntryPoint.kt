package picklab.backend.auth.infrastructure

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import picklab.backend.common.util.logger

class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    val log = logger()

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?,
    ) {
        log.error("commence error: ${authException?.message}")
        response?.sendError(HttpServletResponse.SC_UNAUTHORIZED)
    }
}
