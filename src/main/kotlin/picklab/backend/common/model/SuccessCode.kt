package picklab.backend.common.model

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val status: HttpStatus,
    val message: String,
) {
    JOB_DETAILS_RETRIEVED(HttpStatus.OK, "직무 상세 목록 조회에 성공했습니다."),
}
