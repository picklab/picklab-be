package picklab.backend.common.model

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val status: HttpStatus,
    val message: String,
) {
    SOCIAL_LOGIN_SUCCESS(HttpStatus.OK, "소셜 로그인 성공"),
    JOB_DETAILS_RETRIEVED(HttpStatus.OK, "직무 상세 목록 조회에 성공했습니다."),
}
