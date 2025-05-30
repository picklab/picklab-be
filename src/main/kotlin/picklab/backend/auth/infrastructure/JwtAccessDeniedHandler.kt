package picklab.backend.auth.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.util.logger

class JwtAccessDeniedHandler(
    val objectMapper: ObjectMapper,
) : AccessDeniedHandler {
    val log = logger()

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        log.warn("Access denied: ${accessDeniedException.message}, method=${request.method}, uri=${request.requestURI}")
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json; charset=UTF-8"
        val errorBody = ResponseWrapper.error(ErrorCode.FORBIDDEN)
        objectMapper.writeValue(response.writer, errorBody)
    }
}
