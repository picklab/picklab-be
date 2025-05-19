package picklab.backend.auth.infrastructure

import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import picklab.backend.auth.domain.TokenResponse

@Component
class AuthCookieCreator {
    fun createCookies(tokens: TokenResponse): List<ResponseCookie> =
        listOf(
            ResponseCookie
                .from("accessToken", tokens.accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("None")
                .build(),
            ResponseCookie
                .from("refreshToken", tokens.refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .sameSite("None")
                .build(),
        )
}
