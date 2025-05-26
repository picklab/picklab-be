package picklab.backend.auth.infrastructure

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class RefreshTokenProvider(
    @Value("\${jwt.refresh.secret}")
    private val refreshSecret: String,
    @Value("\${jwt.refresh.expiration}")
    private val refreshExpiration: Long,
) : JwtTokenProvider {
    private val key: SecretKey = Keys.hmacShaKeyFor(refreshSecret.toByteArray(StandardCharsets.UTF_8))

    override fun generateToken(memberId: Long): String =
        Jwts
            .builder()
            .header()
            .type("JWT")
            .and()
            .issuer("picklab")
            .subject(memberId.toString())
            .claim("tokenType", "refresh")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(key)
            .compact()

    override fun validateToken(token: String): Boolean =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }

    override fun getSubject(token: String): String {
        val claims =
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        return claims.subject
    }

    override fun getExpiration(token: String): Date =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .expiration
}
