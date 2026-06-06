CREATE TABLE IF NOT EXISTS review_helpful
(
    id         BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '리뷰 도움돼요 ID',
    member_id  BIGINT   NOT NULL COMMENT '회원 ID',
    review_id  BIGINT   NOT NULL COMMENT '리뷰 ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    CONSTRAINT uk_review_helpful_member_review UNIQUE (member_id, review_id),
    INDEX idx_review_helpful_review_id (review_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='리뷰 도움돼요 테이블';
