package picklab.backend.search.entrypoint

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class SearcherKeyResolver(
    @Value("\${app.search.guest-key-salt:picklab-search-guest-key}") private val guestKeySalt: String,
) {
    companion object {
        private const val MEMBER_PREFIX = "MEMBER:"
        private const val GUEST_PREFIX = "GUEST:"
        private const val X_FORWARDED_FOR = "X-Forwarded-For"
        private const val X_REAL_IP = "X-Real-IP"
    }

    fun resolve(
        memberId: Long?,
        request: HttpServletRequest,
    ): String =
        if (memberId != null) {
            "$MEMBER_PREFIX$memberId"
        } else {
            "$GUEST_PREFIX${hash(resolveClientIp(request))}"
        }

    private fun resolveClientIp(request: HttpServletRequest): String {
        val forwardedFor =
            request
                .getHeader(X_FORWARDED_FOR)
                ?.split(",")
                ?.firstOrNull()
                ?.trim()

        return forwardedFor
            ?.takeIf { it.isNotBlank() }
            ?: request.getHeader(X_REAL_IP)?.takeIf { it.isNotBlank() }
            ?: request.remoteAddr
    }

    private fun hash(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("$guestKeySalt:$value".toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
