package picklab.backend.common.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ErrorResponseWrapper(
    val code: Int,
    val message: String,
    val errors: List<ErrorField>,
) {
    companion object {
        fun error(
            code: ErrorCode,
            message: String,
            errors: List<ErrorField>,
        ): ErrorResponseWrapper =
            ErrorResponseWrapper(
                code = code.status.value(),
                message = message,
                errors = errors,
            )

        fun error(code: ErrorCode): ErrorResponseWrapper =
            ErrorResponseWrapper(
                code = code.status.value(),
                message = code.message,
                errors = emptyList(),
            )
    }
}
