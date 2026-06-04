ALTER TABLE archive
    ADD COLUMN participation_id BIGINT NULL COMMENT '활동 참여 ID' AFTER id;

UPDATE archive a
    JOIN activity_participation ap
        ON ap.member_id = a.member_id
        AND ap.activity_id = a.activity_id
        AND ap.deleted_at IS NULL
SET a.participation_id = ap.id;

DELETE aru
FROM archive_reference_url aru
    JOIN archive a ON aru.archive_id = a.id
WHERE a.participation_id IS NULL;

DELETE aufu
FROM archive_upload_file_url aufu
    JOIN archive a ON aufu.archive_id = a.id
WHERE a.participation_id IS NULL;

DELETE FROM archive
WHERE participation_id IS NULL;

ALTER TABLE archive
    MODIFY participation_id BIGINT NOT NULL COMMENT '활동 참여 ID',
    DROP COLUMN member_id,
    DROP COLUMN activity_id,
    DROP COLUMN activity_type,
    DROP COLUMN activity_progress_status;

CREATE INDEX idx_archive_participation
    ON archive (participation_id);
