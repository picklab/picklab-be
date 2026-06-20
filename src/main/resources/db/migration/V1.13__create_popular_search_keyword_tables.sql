CREATE TABLE IF NOT EXISTS popular_search_keyword_event
(
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '인기 검색어 집계 이벤트 ID',
    keyword      VARCHAR(255) NOT NULL COMMENT '정규화된 검색 키워드',
    searcher_key VARCHAR(100) NOT NULL COMMENT '검색자 식별 키',
    search_hour  DATETIME     NOT NULL COMMENT '검색 집계 시간대',
    searched_at  DATETIME     NOT NULL COMMENT '검색 실행 시간',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    CONSTRAINT uk_popular_search_keyword_event_hour
        UNIQUE (keyword, searcher_key, search_hour)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='인기 검색어 집계 이벤트 테이블';

CREATE INDEX idx_popular_search_keyword_event_01
    ON popular_search_keyword_event (search_hour, keyword);

CREATE INDEX idx_popular_search_keyword_event_02
    ON popular_search_keyword_event (search_hour, searched_at);

CREATE TABLE IF NOT EXISTS blocked_search_keyword
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '차단 검색어 ID',
    keyword    VARCHAR(255) NOT NULL COMMENT '정규화된 차단 검색어',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    CONSTRAINT uk_blocked_search_keyword UNIQUE (keyword)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='인기 검색어 차단 키워드 테이블';
