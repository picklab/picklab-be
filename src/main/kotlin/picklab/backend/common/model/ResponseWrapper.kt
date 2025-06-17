package picklab.backend.common.model

data class ResponseWrapper<T>(
    val code: Int,
    val message: String,
    val data: T?,
) {
    companion object {
        fun <T> success(
            code: SuccessCode,
            data: T?,
        ): ResponseWrapper<T> =
            ResponseWrapper(
                code = code.status.value(),
                message = code.message,
                data = data,
            )

        fun success(code: SuccessCode): ResponseWrapper<Unit> =
            ResponseWrapper(
                code = code.status.value(),
                message = code.message,
                data = Unit,
            )
    }
}
