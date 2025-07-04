package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import picklab.backend.auth.domain.AuthException
import picklab.backend.common.model.ErrorResponseWrapper
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
            log.error("JWT Exception: {}", e.message)
            response.status = e.errorCode.status.value()
            response.contentType = "application/json"
            objectMapper.writeValue(response.writer, ErrorResponseWrapper.error(e.errorCode))
        }
    }
}
