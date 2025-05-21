package picklab.backend.common.handler

import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.method.MethodValidationException
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
import picklab.backend.common.model.ErrorCode
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
        MethodArgumentNotValidException::class,
        HandlerMethodValidationException::class,
        ErrorResponseException::class,
        MaxUploadSizeExceededException::class,
        ConversionNotSupportedException::class,
        TypeMismatchException::class,
        HttpMessageNotReadableException::class,
        MethodValidationException::class,
    )
    fun handleMvcException(e: Exception): ResponseEntity<ResponseWrapper<Unit>> {
        log.warn("[handleMvcException] ${e.message}", e)

        return ResponseEntity
            .status(ErrorCode.BAD_REQUEST.status)
            .body(ResponseWrapper.error(ErrorCode.BAD_REQUEST))
    }

    @ExceptionHandler(
        NoHandlerFoundException::class,
        NoResourceFoundException::class,
    )
    fun handleMvcNotFoundException(e: Exception): ResponseEntity<ResponseWrapper<Unit>> {
        log.warn("[handleNotFoundExceptions] ${e.message}", e)

        return ResponseEntity
            .status(ErrorCode.NOT_FOUND.status)
            .body(ResponseWrapper.error(ErrorCode.NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ResponseWrapper<Unit>> {
        log.error("[handleException] ${e.message}", e)

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.status)
            .body(ResponseWrapper.error(ErrorCode.INTERNAL_SERVER_ERROR))
    }
}
