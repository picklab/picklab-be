package picklab.backend.auth.infrastructure

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class AccessTokenProvider(
    @Value("\${jwt.access.secret}")
    private val accessSecret: String,
    @Value("\${jwt.access.expiration}")
    private val accessExpiration: Long,
) : JwtTokenProvider {
    private val key: SecretKey = Keys.hmacShaKeyFor(accessSecret.toByteArray(StandardCharsets.UTF_8))

    override fun generateToken(memberId: Long): String =
        Jwts
            .builder()
            .header()
            .type("JWT")
            .and()
            .issuer("picklab")
            .subject(memberId.toString())
            .claim("tokenType", "access")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + accessExpiration))
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

    override fun getTokenType(token: String): String =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload["tokenType"] as String
}
