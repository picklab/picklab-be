-- Picklab 회원 테이블 생성
CREATE TABLE IF NOT EXISTS member
(
    id                       BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '회원 ID',
    name                     VARCHAR(20)   NOT NULL COMMENT '회원 이름',
    email                    VARCHAR(100)  NOT NULL COMMENT '회원 이메일',
    company                  VARCHAR(50)   NOT NULL COMMENT '재직중인 회사',
    school                   VARCHAR(50)   NOT NULL COMMENT '최종 학교',
    department               VARCHAR(50)   NOT NULL COMMENT '전공',
    birth_date               DATE          NULL COMMENT '생년월일',
    nickname                 VARCHAR(50)   NOT NULL COMMENT '닉네임',
    profile_image_url        VARCHAR(255)  NULL COMMENT '프로필 이미지 url',
    education_level          VARCHAR(50)   NOT NULL COMMENT '최종 학력',
    graduation_status        VARCHAR(50)   NOT NULL COMMENT '학업 상태',
    employment_status        VARCHAR(50)   NOT NULL COMMENT '재직 상태',
    employment_type          VARCHAR(50)   NULL COMMENT '고용 형태',
    refresh_token            VARCHAR(255)  NULL COMMENT '리프레시 토큰',
    is_completed             BOOLEAN       NOT NULL DEFAULT FALSE COMMENT '회원 가입 완료 여부',
    created_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at               DATETIME      NULL COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 테이블';

-- 회원 사용자 약관 정보 테이블 생성
-- 차후에 법적 문제 등으로 이전 기록을 확인해야하는 경우 JPA Envers를 사용하여 테이블을 생성할 예정
-- member 테이블과 1:1 관계
CREATE TABLE IF NOT EXISTS member_agreement
(
    id                BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 약관 ID',
    member_id         BIGINT   NOT NULL COMMENT '회원 ID',
    email_agreement   BOOLEAN  NOT NULL COMMENT '이메일 수신 동의 여부',
    privacy_agreement BOOLEAN  NOT NULL COMMENT '개인정보 수집 및 이용 동의 여부',
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
);

-- 회원 소셜 로그인 정보 테이블 생성
-- 차후에 법적 문제 등으로 이전 기록을 확인해야하는 경우 JPA Envers를 사용하여 테이블을 생성할 예정
-- member 테이블과 N(소셜 로그인):1(회원) 관계
CREATE TABLE IF NOT EXISTS social_login
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '소셜 로그인 ID',
    member_id   BIGINT       NOT NULL COMMENT '회원 ID',
    social_type VARCHAR(50)  NOT NULL COMMENT '소셜 로그인 타입',
    social_id   VARCHAR(100) NOT NULL COMMENT '소셜 로그인 ID',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
);

-- 관심 직무 카테고리 테이블
CREATE TABLE IF NOT EXISTS job_category
(
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '직무 카테고리 ID',
    job_group  VARCHAR(50) NOT NULL COMMENT '직무 대분류 (기획 / 디자인 / 개발 / 마케팅 등)',
    job_detail VARCHAR(50) NULL COMMENT '직무 세부 분류 (nullable)',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='직무 카테고리 테이블';

-- 회원 관심 직무 조인 테이블
CREATE TABLE IF NOT EXISTS member_interest_job_category
(
    id              BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '관심 직무 ID',
    member_id       BIGINT   NOT NULL COMMENT '회원 ID',
    job_category_id BIGINT   NOT NULL COMMENT '직무 카테고리 ID',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 관심 직무 조인 테이블';

-- 회원 인증 코드 테이블
CREATE TABLE IF NOT EXISTS member_auth_code
(
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '인증 코드 ID',
    member_id  BIGINT      NOT NULL COMMENT '회원 ID',
    code       VARCHAR(20) NOT NULL COMMENT '인증 코드',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 인증 코드 테이블';

-- 회원 알림 설정 테이블
CREATE TABLE IF NOT EXISTS member_notification_preference
(
    id                         BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '알림 설정 ID',
    member_id                  BIGINT   NOT NULL COMMENT '회원 ID',
    notify_popular_activity    BOOLEAN  NOT NULL COMMENT '인기 알림 수신 동의 여부',
    notify_bookmarked_activity BOOLEAN  NOT NULL COMMENT '북마크 알림 수신 동의 여부',
    created_at                 DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at                 DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 알림 설정 테이블';

-- 회원 탈퇴 사유 관리 테이블
CREATE TABLE IF NOT EXISTS member_withdrawal
(
    id                       BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '탈퇴 사유 ID',
    member_id                BIGINT   NULL COMMENT '회원 ID',
    withdrawal_reason        VARCHAR(255)  NOT NULL COMMENT '탈퇴 사유',
    withdrawal_reason_detail VARCHAR(2000) NULL COMMENT '탈퇴 상세 사유',
    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 관심 직무 조인 테이블';

-- 활동 테이블 생성
CREATE TABLE IF NOT EXISTS activity
(
    id                     BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동 ID',
    group_id               BIGINT      NOT NULL COMMENT '활동 그룹 ID',
    title                  VARCHAR(50) NOT NULL COMMENT '활동명',
    activity_type          VARCHAR(50) NOT NULL COMMENT '활동 유형 (대외활동, 공모전/해커톤, 강연/세미나, 교육)',
    organizer              VARCHAR(50) NOT NULL COMMENT '주최 기관/단체명',
    target_audience        VARCHAR(50) NOT NULL COMMENT '참여대상',
    location               VARCHAR(50) COMMENT '모임지역 (대외활동, 강연/세미나, 교육)',
    recruitment_start_date DATE        NOT NULL COMMENT '모집 시작일',
    recruitment_end_date   DATE        NOT NULL COMMENT '모집 종료일',
    start_date             DATE        NOT NULL COMMENT '활동 시작일',
    end_date               DATE        NOT NULL COMMENT '활동 종료일',
    status                 VARCHAR(50) NOT NULL COMMENT '모집 상태 (모집 중, 모집 마감)',
    view_count             BIGINT      NOT NULL COMMENT '조회수',
    activity_field         VARCHAR(50) COMMENT '활동 분야(대외활동)',
    domain                 VARCHAR(50) COMMENT '도메인(공모전/해커톤)',
    cost                   BIGINT COMMENT '시상 규모(공모전/해커톤), 교육비용(교육)',
    duration               INTEGER     NOT NULL COMMENT '활동 기간(일)',
    description            VARCHAR(2000) COMMENT '활동 설명',
    benefit                VARCHAR(2000) COMMENT '활동 혜택',
    created_at             DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at             DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at             DATETIME    NULL COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동 테이블';

-- 활동 그룹 테이블 생성
-- activity 테이블과 N(활동):1(활동 그룹) 관계
CREATE TABLE IF NOT EXISTS activity_group
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동 그룹 ID',
    name        VARCHAR(100) NOT NULL COMMENT '그룹명',
    description VARCHAR(255) COMMENT '그룹 설명',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동 그룹 테이블';

-- 활동-직무 조인 테이블
CREATE TABLE IF NOT EXISTS activity_job_category
(
    id              BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동-직무 연결 ID',
    activity_id     BIGINT   NOT NULL COMMENT '활동 ID',
    job_category_id BIGINT   NOT NULL COMMENT '직무 카테고리 ID',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동-직무 조인 테이블';

-- 활동 파일 업로드 테이블
CREATE TABLE IF NOT EXISTS activity_upload_file
(
    id              BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동-직무 연결 ID',
    activity_id     BIGINT   NOT NULL COMMENT '활동 ID',
    name            VARCHAR(255)  NOT NULL COMMENT '파일명(ex. 지원 PDF)',
    url             VARCHAR(2084) NOT NULL COMMENT '업로드 파일 URL',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동에 관련된 업로드 파일 테이블';

-- 북마크 테이블
CREATE TABLE IF NOT EXISTS bookmark
(
    id          BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '북마크 ID',
    member_id   BIGINT   NOT NULL COMMENT '회원 ID',
    activity_id BIGINT   NOT NULL COMMENT '활동 ID',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='북마크 테이블';

-- 아카이브 테이블 생성
-- member 테이블과 1(회원):N(아카이브) 관계
-- activity 테이블과 1(활동):N(아카이브) 관계
CREATE TABLE IF NOT EXISTS archive
(
    id                       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '아카이브 ID',
    member_id                BIGINT         NOT NULL COMMENT '회원 ID',
    activity_id              BIGINT         NOT NULL COMMENT '활동 ID',
    user_start_date          DATE           NOT NULL COMMENT '활동 시작일',
    user_end_date            DATE           NOT NULL COMMENT '활동 종료일',
    role                     VARCHAR(50)    NOT NULL COMMENT '활동 역할 (기획 / 디자인 / 개발 / 마케팅 / AI)',
    detail_role              VARCHAR(50)    NOT NULL COMMENT '상세 역할',
    custom_role              VARCHAR(255)   NULL     COMMENT '상세 역할에서 기타를 선택하여 직접 입력한 역할',
    activity_record          TEXT           NOT NULL COMMENT '활동 기록',
    activity_type            VARCHAR(50)    NOT NULL COMMENT '활동 구분 (대외활동 / 강연 / 세미나 / 교육 / 공모전/해커톤)',
    activity_progress_status VARCHAR(50)    NOT NULL COMMENT '활동 진행 상태 (수료 완료 / 중도 포기)',
    write_status             VARCHAR(50)    NOT NULL COMMENT '작성 상태 (미작성 / 작성 중 / 작성 완료)',
    created_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at               DATETIME       NULL     COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='아카이브 테이블';

-- 리뷰 테이블 생성
-- member 테이블과 1(회원):N(리뷰) 관계
-- activity 테이블과 1(활동):N(리뷰) 관계
CREATE TABLE IF NOT EXISTS review
(
    id                   BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '리뷰 ID',
    member_id            BIGINT         NOT NULL  COMMENT '회원 ID',
    activity_id          BIGINT         NOT NULL  COMMENT '활동 ID',
    overall_score        TINYINT        NOT NULL  COMMENT '총 평점',
    info_score           TINYINT        NOT NULL  COMMENT '정보 점수',
    difficulty_score     TINYINT        NOT NULL  COMMENT '강도 점수',
    benefit_score        TINYINT        NOT NULL  COMMENT '혜택 점수',
    summary              VARCHAR(255)   NOT NULL  COMMENT '한줄 평',
    strength             VARCHAR(1000)  NOT NULL  COMMENT '장점',
    weakness             VARCHAR(1000)  NOT NULL  COMMENT '단점',
    tips                 VARCHAR(1000)  NULL      COMMENT '꿀팁',
    job_relevance_score  TINYINT        NOT NULL  COMMENT '직무 연관성 점수',
    url                  VARCHAR(255)   NULL      COMMENT '인증 자료 URL',
    created_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at           DATETIME       NULL      COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='리뷰 테이블';

-- 알림 테이블 생성
-- member 테이블과 1(회원):N(알림) 관계
CREATE TABLE IF NOT EXISTS notification
(
    id          BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '알림 ID',
    member_id   BIGINT         NOT NULL COMMENT '회원 ID',
    title       VARCHAR(255)   NOT NULL COMMENT '알림 제목',
    type        VARCHAR(50)    NOT NULL COMMENT '알림 타입',
    link        VARCHAR(255)   NOT NULL COMMENT '클릭 시 이동할 링크',
    is_read     BOOLEAN        NOT NULL DEFAULT FALSE COMMENT '읽음 여부',
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at  DATETIME       NULL     COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='알림 테이블';

-- 아카이브 참고 url 테이블 생성
-- archive 테이블과 1(아카이브):N(참고 URL) 관계
CREATE TABLE IF NOT EXISTS archive_reference_url
(
    id                       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '아카이브 참고 URL ID',
    archive_id               BIGINT         NOT NULL COMMENT '아카이브 ID',
    url                      VARCHAR(2084)  NOT NULL COMMENT '참고 URL',
    created_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='아카이브 참고 URL 테이블';

-- 아카이브 업로드 파일 url 테이블 생성
-- archive 테이블과 1(아카이브):N(업로드 파일 URL) 관계
CREATE TABLE IF NOT EXISTS archive_upload_file_url
(
    id                       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '아카이브 업로드 파일 URL ID',
    archive_id               BIGINT         NOT NULL COMMENT '아카이브 ID',
    url                      VARCHAR(2084)  NOT NULL COMMENT '업로드 파일 URL',
    created_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='아카이브 업로드 파일 URL 테이블';