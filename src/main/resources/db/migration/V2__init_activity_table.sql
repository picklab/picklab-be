-- 활동 테이블 생성
CREATE TABLE IF NOT EXISTS activity
(
    id                      BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동 ID',
    series_id               BIGINT      NOT NULL COMMENT '활동 그룹 ID',
    title                   VARCHAR(50) NOT NULL COMMENT '활동명',
    activity_type           VARCHAR(50) NOT NULL COMMENT '활동 유형 (대외활동, 공모전/해커톤, 강연/세미나, 교육)',
    organizer               VARCHAR(50) NOT NULL COMMENT '주최 기관/단체명',
    target_audience         VARCHAR(50) NOT NULL COMMENT '참여대상',
    location                VARCHAR(50) NOT NULL COMMENT '모임지역',
    recruitment_start_date  DATETIME    NOT NULL COMMENT '모집 시작일',
    recruitment_end_date    DATETIME    NOT NULL COMMENT '모집 종료일',
    start_date              DATETIME    NOT NULL COMMENT '활동 시작일',
    end_date                DATETIME    NOT NULL COMMENT '활동 종료일',
    status                  VARCHAR(50) NOT NULL COMMENT '모집 상태 (모집 중, 모집 마감)',
    view_count              BIGINT      NOT NULL COMMENT '조회수',
    activity_field          VARCHAR(50) COMMENT '활동 분야(대외활동)',
    domain                  VARCHAR(50) COMMENT '도메인(공모전/해커톤)',
    cost                    BIGINT      COMMENT '시상 규모(공모전/해커톤), 교육비용(교육)',
    duration                INTEGER     COMMENT '활동 기간(일)',
    created_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at              DATETIME    NULL COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동 테이블';

-- 활동 그룹 테이블 생성
-- activity 테이블과 N(활동):1(활동 그룹) 관계
CREATE TABLE IF NOT EXISTS activity_series
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동 그룹 ID',
    name        VARCHAR(100) NOT NULL COMMENT '그룹명',
    description VARCHAR(255) COMMENT '그룹 설명',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동 그룹 테이블';

-- 활동-직무 조인 테이블
CREATE TABLE IF NOT EXISTS activity_job_category
(
    id              BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '활동-직무 연결 ID',
    activity_id     BIGINT   NOT NULL COMMENT '활동 ID',
    job_category_id BIGINT   NOT NULL COMMENT '직무 카테고리 ID',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='활동-직무 조인 테이블';