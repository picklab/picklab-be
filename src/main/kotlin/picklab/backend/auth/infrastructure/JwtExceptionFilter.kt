package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import picklab.backend.auth.domain.AuthException
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.util.logger

class JwtExceptionFilter(
    val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    val log = logger()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: AuthException) {
            log.error("JWT Exception: {}", e.message, e)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            objectMapper.writeValue(response.writer, ResponseWrapper.error(e.errorCode))
        }
    }
}
