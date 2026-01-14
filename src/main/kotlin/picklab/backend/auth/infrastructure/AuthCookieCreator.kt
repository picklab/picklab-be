package picklab.backend.auth.infrastructure

import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import picklab.backend.auth.domain.AuthToken

@Component
class AuthCookieCreator {
    fun createCookies(tokens: AuthToken): List<ResponseCookie> =
        listOf(
            ResponseCookie
                .from("accessToken", tokens.accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build(),
            ResponseCookie
                .from("refreshToken", tokens.refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("Lax")
                .build(),
        )
}
