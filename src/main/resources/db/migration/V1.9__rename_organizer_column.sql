ALTER TABLE activity RENAME COLUMN organizer TO organizer_type;
ALTER TABLE activity ADD COLUMN organizer VARCHAR(100) NULL AFTER title;

UPDATE activity a
    JOIN activity_group g ON g.id = a.group_id
SET a.organizer = SUBSTRING_INDEX(g.description, ' | host:', -1)
WHERE g.description LIKE 'linkareer:% | host:%';
