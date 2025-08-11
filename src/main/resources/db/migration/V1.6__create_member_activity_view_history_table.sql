-- 회원 활동 조회 이력 테이블 생성
CREATE TABLE IF NOT EXISTS member_activity_view_history
(
    id          BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '검색 기록 ID',
    member_id   BIGINT   NOT NULL COMMENT '검색한 회원 ID',
    activity_id BIGINT   NOT NULL COMMENT '활동 ID',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 활동 조회 이력 테이블';