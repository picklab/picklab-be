CREATE INDEX idx_participation_member_activity
    ON activity_participation (member_id, activity_id);
