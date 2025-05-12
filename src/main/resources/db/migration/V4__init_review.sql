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