-- 회원 검색 기록 테이블 생성
CREATE TABLE IF NOT EXISTS member_search_history
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '검색 기록 ID',
    member_id   BIGINT       NOT NULL COMMENT '검색한 회원 ID',
    keyword     TEXT         NOT NULL COMMENT '검색 키워드',
    searched_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '검색 실행 시간',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at  DATETIME     NULL COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 검색 기록 테이블';

-- 외래키 제약조건 추가
ALTER TABLE member_search_history
    ADD CONSTRAINT fk_member_search_history_member_id
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;

-- 인덱스 추가 (성능 최적화)
CREATE INDEX idx_member_search_history_member_id ON member_search_history (member_id);
CREATE INDEX idx_member_search_history_keyword ON member_search_history (keyword(255));
CREATE INDEX idx_member_search_history_searched_at ON member_search_history (searched_at DESC);
CREATE INDEX idx_member_search_history_member_keyword ON member_search_history (member_id, keyword(255)); 