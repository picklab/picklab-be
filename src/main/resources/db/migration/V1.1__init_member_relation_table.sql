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
