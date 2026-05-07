ALTER TABLE activity
    ADD COLUMN recruitment_end_type VARCHAR(50) NOT NULL DEFAULT 'FIXED' COMMENT '모집 종료 유형 (날짜 지정, 상시모집, 모집 시 마감)';
