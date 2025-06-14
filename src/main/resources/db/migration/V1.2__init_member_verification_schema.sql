-- 회원 인증 코드 저장 테이블
-- 사용자의 이메일 인증 코드를 저장하기 위한 테이블
-- member 테이블과 1(회원):N(코드) 관계
CREATE TABLE IF NOT EXISTS member_verification
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '회원 인증 ID',
    member_id           BIGINT       NOT NULL COMMENT '회원 ID',
    email               VARCHAR(100) NOT NULL COMMENT '인증 이메일',
    code                VARCHAR(10)  NOT NULL COMMENT '인증 코드',
    expired_at          DATETIME     NOT NULL COMMENT '인증 만료 시간',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at          DATETIME     NULL DEFAULT NULL COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 인증 코드 테이블';