package picklab.backend.auth.entrypoint

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.auth.application.AuthUseCase
import picklab.backend.auth.application.OAuthProviderResolver
import picklab.backend.auth.infrastructure.AuthCookieFactory
import java.net.URI

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val oAuthProviderResolver: OAuthProviderResolver,
    private val authUseCase: AuthUseCase,
    private val authCookieFactory: AuthCookieFactory,
) : AuthApi {
    @GetMapping("/login/{provider}")
    override fun login(
        @PathVariable provider: String,
    ): ResponseEntity<Unit> {
        val oauthProvider = oAuthProviderResolver.resolve(provider)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(oauthProvider.getAuthorizationUrl()))
            .build()
    }

    @GetMapping("/callback/{provider}")
    override fun handleCallback(
        @PathVariable provider: String,
        @RequestParam code: String,
    ): ResponseEntity<Unit> {
        val tokenResponse = authUseCase.handleOAuthCallback(provider, code)

        val cookies = authCookieFactory.createCookies(tokenResponse)

        val responseBuilder = ResponseEntity.ok()
        cookies.forEach { cookie ->
            responseBuilder.header("Set-Cookie", cookie.toString())
        }

        return responseBuilder.build()
    }
}
