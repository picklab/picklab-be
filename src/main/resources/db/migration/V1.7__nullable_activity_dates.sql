ALTER TABLE activity
    MODIFY COLUMN recruitment_end_date DATE NULL COMMENT '모집 종료일',
    MODIFY COLUMN end_date DATE NULL COMMENT '활동 종료일';
