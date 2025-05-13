-- 회원 탈퇴 사유 관리 테이블
CREATE TABLE IF NOT EXISTS member_withdrawal
(
    id                       BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '관심 직무 ID',
    member_id                BIGINT   NULL COMMENT '회원 ID',
    withdrawal_reason        VARCHAR(255)  NOT NULL COMMENT '탈퇴 사유',
    withdrawal_reason_detail VARCHAR(2000) NULL COMMENT '탈퇴 상세 사유',
    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 관심 직무 조인 테이블';