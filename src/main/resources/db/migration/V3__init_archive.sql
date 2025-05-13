-- 아카이브 테이블 생성
-- member 테이블과 1(회원):N(아카이브) 관계
-- activity 테이블과 1(활동):N(아카이브) 관계
CREATE TABLE IF NOT EXISTS archive
(
    id                       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '아카이브 ID',
    member_id                BIGINT         NOT NULL COMMENT '회원 ID',
    activity_id              BIGINT         NOT NULL COMMENT '활동 ID',
    user_start_date          DATE           NOT NULL COMMENT '활동 시작일',
    user_end_date            DATE           NOT NULL COMMENT '활동 종료일',
    role                     VARCHAR(50)    NOT NULL COMMENT '활동 역할 (기획 / 디자인 / 개발 / 마케팅 / AI)',
    detail_role              VARCHAR(50)    NOT NULL COMMENT '상세 역할',
    custom_role              VARCHAR(255)   NULL     COMMENT '상세 역할에서 기타를 선택하여 직접 입력한 역할',
    activity_record          TEXT           NOT NULL COMMENT '활동 기록',
    activity_type            VARCHAR(50)    NOT NULL COMMENT '활동 구분 (대외활동 / 강연 / 세미나 / 교육 / 공모전/해커톤)',
    activity_progress_status VARCHAR(50)    NOT NULL COMMENT '활동 진행 상태 (수료 완료 / 중도 포기)',
    created_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    deleted_at               DATETIME       NULL     COMMENT '삭제일'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='아카이브 테이블';