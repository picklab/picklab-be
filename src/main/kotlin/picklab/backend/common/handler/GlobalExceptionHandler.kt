package picklab.backend.common.handler

import com.fasterxml.jackson.databind.JsonMappingException
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponseException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import picklab.backend.auth.domain.AuthException
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.ErrorField
import picklab.backend.common.model.ErrorResponseWrapper
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.util.logger

@RestControllerAdvice
class GlobalExceptionHandler {
    val log = logger()

    @ExceptionHandler(
        HttpRequestMethodNotSupportedException::class,
        HttpMediaTypeNotSupportedException::class,
        HttpMediaTypeNotAcceptableException::class,
        MissingPathVariableException::class,
        MissingServletRequestParameterException::class,
        MissingServletRequestPartException::class,
        ServletRequestBindingException::class,
        HandlerMethodValidationException::class,
        ErrorResponseException::class,
        MaxUploadSizeExceededException::class,
        ConversionNotSupportedException::class,
        TypeMismatchException::class,
    )
    fun handleMvcException(e: Exception): ResponseEntity<ErrorResponseWrapper> {
        log.warn("[handleMvcException] ${e.message}", e)

        return ResponseEntity
            .status(ErrorCode.BAD_REQUEST.status)
            .body(ErrorResponseWrapper.error(ErrorCode.BAD_REQUEST))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponseWrapper> {
        log.warn("[handleMethodValidationException] ${e.message}", e)

        val errors =
            e.bindingResult.fieldErrors.map { fieldError ->
                ErrorField(
                    field = fieldError.field,
                    message = fieldError.defaultMessage,
                )
            }

        return ResponseEntity
            .status(ErrorCode.BAD_REQUEST.status)
            .body(
                ErrorResponseWrapper.error(
                    code = ErrorCode.BAD_REQUEST,
                    message = "요청 검증에 실패했습니다.",
                    errors = errors,
                ),
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponseWrapper> {
        log.warn("[handleHttpMessageNotReadableException] ${e.message}", e)

        if (e.cause is JsonMappingException) {
            val mappingException = e.cause as JsonMappingException

            val errors =
                mappingException.path
                    .mapNotNull { path ->
                        ErrorField(
                            field = path.fieldName,
                            message = "${path.fieldName} 필드가 올바르지 않습니다.",
                        )
                    }

            return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.status)
                .body(
                    ErrorResponseWrapper.error(
                        code = ErrorCode.BAD_REQUEST,
                        message = "요청 검증에 실패했습니다.",
                        errors = errors,
                    ),
                )
        }

        return ResponseEntity
            .status(ErrorCode.BAD_REQUEST.status)
            .body(
                ErrorResponseWrapper.error(
                    code = ErrorCode.BAD_REQUEST,
                ),
            )
    }

    @ExceptionHandler(
        NoHandlerFoundException::class,
        NoResourceFoundException::class,
    )
    fun handleMvcNotFoundException(e: Exception): ResponseEntity<ErrorResponseWrapper> {
        log.warn("[handleNotFoundExceptions] ${e.message}", e)

        return ResponseEntity
            .status(ErrorCode.NOT_FOUND.status)
            .body(ErrorResponseWrapper.error(ErrorCode.NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ResponseWrapper<Unit>> {
        log.error("[handleException] ${e.message}", e)

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(ResponseWrapper.error(ErrorCode.INTERNAL_SERVER_ERROR))
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(e: AuthException): ResponseEntity<ErrorResponseWrapper> {
        log.warn("[handleAuthException] ${e.message}", e)

        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponseWrapper.error(e.errorCode))
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponseWrapper> {
        log.warn("[handleBusinessException] ${e.message}", e)

        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponseWrapper.error(e.errorCode))
    }
}
