package picklab.backend.auth.infrastructure

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import picklab.backend.common.util.logger

class JwtAccessDeniedHandler : AccessDeniedHandler {
    val log = logger()

    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?,
    ) {
        log.warn("Access denied: ${accessDeniedException?.message}")
        response?.sendError(HttpServletResponse.SC_FORBIDDEN)
    }
}
