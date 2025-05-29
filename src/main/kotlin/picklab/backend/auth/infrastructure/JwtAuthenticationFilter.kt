package picklab.backend.auth.infrastructure

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import picklab.backend.auth.domain.AuthException
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.util.logger

class JwtAuthenticationFilter(
    val accessTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    val log = logger()

    companion object {
        private const val ACCESS_COOKIE_NAME = "accessToken"
        private const val ACCESS_TOKEN_TYPE = "access"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken = resolveCookie(request, ACCESS_COOKIE_NAME)

        if (!accessToken.isNullOrEmpty()) {
            try {
                val accessTokenType: String = accessTokenProvider.getTokenType(accessToken)
                if (ACCESS_TOKEN_TYPE == accessTokenType) {
                    val userDetails: MemberPrincipal = getUserDetails(accessToken)
                    setAuthenticationUser(userDetails, request)
                }
            } catch (e: ExpiredJwtException) {
                throw AuthException(ErrorCode.TOKEN_EXPIRED)
            } catch (e: JwtException) {
                throw AuthException(ErrorCode.INVALID_TOKEN)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveCookie(
        request: HttpServletRequest,
        cookieName: String,
    ): String? = request.cookies?.firstOrNull { it.name == cookieName }?.value

    private fun getUserDetails(accessToken: String): MemberPrincipal {
        val userId = accessTokenProvider.getSubject(accessToken).toLong()

        return MemberPrincipal(userId)
    }

    private fun setAuthenticationUser(
        userDetails: MemberPrincipal,
        request: HttpServletRequest,
    ) {
        val authentication =
            UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                emptyList<GrantedAuthority>(),
            )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        log.info("Authenticated User:  {}", userDetails.memberId)
    }
}
