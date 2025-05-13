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