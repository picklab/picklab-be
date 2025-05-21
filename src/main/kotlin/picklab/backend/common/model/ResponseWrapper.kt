package picklab.backend.common.model

import org.springframework.http.HttpStatus

data class ResponseWrapper<T>(
    val code: Int,
    val message: String,
    val data: T?,
) {
    companion object {
        fun <T> success(
            code: HttpStatus,
            message: String,
            data: T?,
        ): ResponseWrapper<T> =
            ResponseWrapper(
                code = code.value(),
                message = message,
                data = data,
            )

        fun success(
            code: HttpStatus,
            message: String,
        ): ResponseWrapper<Unit> =
            ResponseWrapper(
                code = code.value(),
                message = message,
                data = Unit,
            )

        fun error(code: ErrorCode): ResponseWrapper<Unit> = ResponseWrapper(code = code.status.value(), code.message, Unit)
    }
}
