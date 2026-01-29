package picklab.backend.auth.entrypoint

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import picklab.backend.auth.application.AuthUseCase
import picklab.backend.auth.application.OAuthProviderResolver
import picklab.backend.auth.application.TokenUseCase
import picklab.backend.auth.domain.AuthToken
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.member.domain.enums.SocialType
import java.net.URI

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val oAuthProviderResolver: OAuthProviderResolver,
    private val authUseCase: AuthUseCase,
    private val tokenUseCase: TokenUseCase,
) : AuthApi {
    @GetMapping("/login/{provider}")
    override fun login(
        @PathVariable provider: SocialType,
    ): ResponseEntity<Unit> {
        val oauthProvider = oAuthProviderResolver.resolve(provider)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(oauthProvider.getAuthorizationUrl()))
            .build()
    }

    @PostMapping("/callback/{provider}")
    override fun handleCallback(
        @PathVariable provider: SocialType,
        @RequestParam code: String,
    ): ResponseEntity<ResponseWrapper<AuthToken>> {
        val tokens = authUseCase.handleOAuthCallback(provider, code)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.SOCIAL_LOGIN_SUCCESS, tokens))
    }

    @PostMapping("/refresh")
    override fun refreshAccessToken(
        @RequestHeader("Authorization") authorization: String,
    ): ResponseEntity<ResponseWrapper<AuthToken>> {
        val refreshToken = authorization.removePrefix("Bearer ").trim()
        val tokens = tokenUseCase.refreshAccessToken(refreshToken)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseWrapper.success(SuccessCode.ACCESS_TOKEN_REFRESHED, tokens))
    }
}
