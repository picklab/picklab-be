package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.util.logger

class JwtAuthenticationEntryPoint(
    val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    val log = logger()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        log.warn("commence error: ${authException.message}, method=${request.method}, uri=${request.requestURI}")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json; charset=UTF-8"
        val errorBody = ResponseWrapper.error(ErrorCode.UNAUTHORIZED)
        objectMapper.writeValue(response.writer, errorBody)
    }
}
