-- 탈퇴한 회원의 연결을 해제하여 기존 탈퇴 회원도 재가입할 수 있도록 한다.
DELETE sl
FROM social_login sl
         JOIN member m ON m.id = sl.member_id
WHERE m.deleted_at IS NOT NULL;

-- 활성 회원 사이의 중복 연결은 임의로 삭제하지 않고 제약 추가 시 검출한다.
ALTER TABLE social_login
    ADD CONSTRAINT uk_social_login_type_id UNIQUE (social_type, social_id);
