-- notification 테이블 성능 최적화를 위한 인덱스 추가

-- created_at 컬럼 인덱스 (배치 정리 작업 최적화)
CREATE INDEX notification_idx_01 ON notification (created_at); 