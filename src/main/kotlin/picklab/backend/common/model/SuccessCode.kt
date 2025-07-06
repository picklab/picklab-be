package picklab.backend.common.model

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val status: HttpStatus,
    val message: String,
) {
    SOCIAL_LOGIN_SUCCESS(HttpStatus.OK, "소셜 로그인 성공"),
    JOB_DETAILS_RETRIEVED(HttpStatus.OK, "직무 상세 목록 조회에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK, "회원 가입에 성공했습니다."),

    // Member 도메인 관련
    MEMBER_INFO_UPDATED(HttpStatus.OK, "회원 정보 수정에 성공했습니다."),
    MEMBER_JOB_CATEGORY_UPDATED(HttpStatus.OK, "관심 직무 수정에 성공했습니다."),
    MEMBER_PROFILE_IMAGE_UPDATED(HttpStatus.OK, "프로필 이미지 변경에 성공했습니다."),
    MEMBER_EMAIL_UPDATED(HttpStatus.OK, "이메일 변경에 성공했습니다."),
    SEND_EMAIL_CODE(HttpStatus.OK, "인증 코드 발송에 성공했습니다."),
    VERIFY_EMAIL_CODE(HttpStatus.OK, "이메일 코드 인증에 성공했습니다."),
    UPDATE_EMAIL_AGREEMENT(HttpStatus.OK, "이메일 마케팅 수신 동의 정보 수정에 성공했습니다."),
    GET_MEMBER_SOCIAL_LOGINS(HttpStatus.OK, "소셜 로그인 연동 정보 조회에 성공했습니다."),
    MEMBER_WITHDRAW(HttpStatus.OK, "회원 탈퇴에 성공했습니다."),
    SUBMIT_SURVEY(HttpStatus.OK, "탈퇴 설문 제출에 성공했습니다."),
    MEMBER_NOTIFICATION_UPDATED(HttpStatus.OK, "알림 변경에 성공했습니다."),

    // Activity 도메인 관련
    GET_ACTIVITIES(HttpStatus.OK, "활동 목록 조회에 성공했습니다."),

    // Archive 도메인 관련
    CREATE_ARCHIVE_SUCCESS(HttpStatus.OK, "아카이브 생성에 성공했습니다."),
    UPDATE_ARCHIVE_SUCCESS(HttpStatus.OK, "아카이브 수정에 성공했습니다."),

    // Notification 도메인 관련
    SEND_NOTIFICATION_SUCCESS(HttpStatus.OK, "알림 전송에 성공했습니다."),
    GET_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "알림 목록 조회에 성공했습니다."),
    GET_RECENT_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "최근 알림 목록 조회에 성공했습니다."),
    MARK_NOTIFICATION_READ_SUCCESS(HttpStatus.OK, "알림 읽음 처리에 성공했습니다."),
    MARK_ALL_NOTIFICATIONS_READ_SUCCESS(HttpStatus.OK, "모든 알림 읽음 처리에 성공했습니다."),
    GET_UNREAD_COUNT_SUCCESS(HttpStatus.OK, "읽지 않은 알림 개수 조회에 성공했습니다.")
}
