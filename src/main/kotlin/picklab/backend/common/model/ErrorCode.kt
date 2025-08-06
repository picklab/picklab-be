package picklab.backend.common.model

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 금지된 리소스입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),

    /**
     * 소셜 로그인 관련
     */
    SOCIAL_CODE_ERROR(HttpStatus.UNAUTHORIZED, "소셜 로그인 인증 오류가 발생했습니다."),
    SOCIAL_USER_INFO_ERROR(HttpStatus.BAD_GATEWAY, "소셜 로그인 유저 정보 조회를 실패하였습니다."),
    EMPTY_SOCIAL_ID(HttpStatus.BAD_REQUEST, "소셜 ID가 비어있습니다."),
    EMPTY_SOCIAL_NAME(HttpStatus.BAD_REQUEST, "소셜 이름이 비어있습니다."),
    EMPTY_SOCIAL_EMAIL(HttpStatus.BAD_REQUEST, "소셜 이메일이 비어있습니다."),
    EMPTY_SOCIAL_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "소셜 프로필 이미지가 비어있습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    /**
     * 회원 도메인 관련
     */
    INVALID_MEMBER(HttpStatus.UNAUTHORIZED, "회원을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 유효하지 않습니다."),
    EXISTS_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    JOB_CATEGORY_LIMIT(HttpStatus.BAD_REQUEST, "관심 직군은 최대 5개까지 선택할 수 있습니다."),

    /**
     * 활동 도메인 관련
     */
    NOT_FOUND_ACTIVITY(HttpStatus.BAD_REQUEST, "활동 정보를 찾을 수 없습니다."),

    /**
     * 활동 참여 도메인 관련
     */
    NOT_FOUND_ACTIVITY_PARTICIPATION(HttpStatus.NOT_FOUND, "해당 활동에 대한 참여 이력이 존재하지 않습니다."),

    /**
     * 아카이브 도메인 관련
     */
    NOT_FOUND_ARCHIVE(HttpStatus.NOT_FOUND, "아카이브 정보를 찾을 수 없습니다."),

    /**
     * 북마크 도메인 관련
     */
    ALREADY_EXISTS_ACTIVITY_BOOKMARK(HttpStatus.BAD_REQUEST, "이미 북마크된 활동입니다."),
    NOT_FOUND_ACTIVITY_BOOKMARK(HttpStatus.NOT_FOUND, "북마크된 활동을 찾을 수 없습니다."),

    /**
     * 알림 도메인 관련
     */
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정보를 찾을 수 없습니다."),

    /**
     * 리뷰 도메인 관련
     */
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 정보를 찾을 수 없습니다."),
    CANNOT_WRITE_REVIEW(HttpStatus.BAD_REQUEST, "해당 활동에 대해 리뷰를 작성할 수 없는 상태입니다."),
    CANNOT_READ_REVIEW(HttpStatus.FORBIDDEN, "해당 리뷰를 읽을 권한이 없습니다."),
    CANNOT_UPDATE_REVIEW(HttpStatus.FORBIDDEN, "해당 리뷰를 수정할 권한이 없습니다."),
    CANNOT_DELETE_REVIEW(HttpStatus.FORBIDDEN, "해당 리뷰를 삭제할 권한이 없습니다."),
    ALREADY_EXISTS_REVIEW(HttpStatus.BAD_REQUEST, "이미 해당 활동에 대해 리뷰를 작성했습니다."),

    /**
     * 검색 도메인 관련
     */
    SEARCH_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "검색 기록을 찾을 수 없습니다."),
}
