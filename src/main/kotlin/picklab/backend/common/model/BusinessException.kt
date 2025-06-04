package picklab.backend.common.model

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
