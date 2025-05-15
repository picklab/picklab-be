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