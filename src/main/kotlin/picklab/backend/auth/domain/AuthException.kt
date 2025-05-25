package picklab.backend.auth.domain

import picklab.backend.common.model.ErrorCode

class AuthException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
