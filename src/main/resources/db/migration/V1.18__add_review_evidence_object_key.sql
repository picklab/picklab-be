ALTER TABLE review
    ADD COLUMN object_key VARCHAR(255) NULL COMMENT '인증 자료 객체 키' AFTER url;

-- 기존 url은 검증 여부와 저장소 경로를 보장할 수 없어 자동 변환하지 않고 원본을 보존한다.
-- 신규 등록 및 인증 자료 교체부터 object_key를 사용하며 기존 승인 상태는 변경하지 않는다.
