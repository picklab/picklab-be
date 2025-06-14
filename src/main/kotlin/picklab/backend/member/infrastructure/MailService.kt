package picklab.backend.member.infrastructure

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class MailService(
    private val mailSender: JavaMailSender,
) {
    fun sendMail(
        target: String,
        code: String,
    ) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, false, "UTF-8")
        helper.setTo(target)
        helper.setSubject("PickLab 이메일 인증 코드")
        helper.setText(
            "PickLab 이메일 인증 코드입니다. 인증 코드는 $code 입니다. " +
                "인증 코드를 입력하여 이메일 인증을 완료해주세요. 코드는 5분간 유효합니다.",
            true,
        )
        mailSender.send(message)
    }

    fun createVerificationCode(): String {
        val secureRandom = SecureRandom()

        val code = secureRandom.nextInt(1000000).toString()

        return code.padStart(6, '0')
    }
}
