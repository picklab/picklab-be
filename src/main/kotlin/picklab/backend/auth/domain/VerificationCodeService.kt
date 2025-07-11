package picklab.backend.auth.domain

import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class VerificationCodeService(
    private val secureRandom: SecureRandom = SecureRandom(),
) {
    fun createCode(): String {
        val secureRandom = SecureRandom()

        val code = secureRandom.nextInt(1000000).toString()

        return code.padStart(6, '0')
    }
}
