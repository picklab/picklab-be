package picklab.backend.auth.infrastructure

import java.util.*

interface JwtTokenProvider {
    fun generateToken(memberId: Long): String

    fun validateToken(token: String): Boolean

    fun getSubject(token: String): String

    fun getExpiration(token: String): Date
}
