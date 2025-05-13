-- 아카이브 참고 url 테이블 생성
-- archive 테이블과 1(아카이브):N(참고 URL) 관계
CREATE TABLE IF NOT EXISTS archive_reference_url
(
    id                       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '아카이브 참고 URL ID',
    archive_id               BIGINT         NOT NULL COMMENT '아카이브 ID',
    url                      VARCHAR(2084)  NOT NULL COMMENT '참고 URL',
    created_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='아카이브 참고 URL 테이블';

-- 아카이브 업로드 파일 url 테이블 생성
-- archive 테이블과 1(아카이브):N(업로드 파일 URL) 관계
CREATE TABLE IF NOT EXISTS archive_upload_file_url
(
    id                       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '아카이브 업로드 파일 URL ID',
    archive_id               BIGINT         NOT NULL COMMENT '아카이브 ID',
    url                      VARCHAR(2084)  NOT NULL COMMENT '업로드 파일 URL',
    created_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_at               DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='아카이브 업로드 파일 URL 테이블';