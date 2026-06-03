# 링커리어 데이터 -> Picklab DB 적재 변환 가이드

링커리어에서 크롤링한 `linkareer.csv` 데이터를 Picklab 활동 테이블과 enum 값에 맞게 변환하는 기준이다.

적재 값은 화면 표시용 한글 라벨이 아니라 **DB/API enum name**을 사용한다. 한글은 판단을 돕는 설명으로만 사용한다.

---

## 1. 입력 데이터 컬럼

| 컬럼명 | 설명 |
|--------|------|
| `상세링크` 또는 `/상세링크` | 링커리어 상세 링크 |
| `활동유형` | 대외활동 / 공모전/해커톤 / 교육 / 강연/세미나 |
| `출처` | 링커리어 |
| `제목` | 활동 제목 |
| `주최기관` | 기관명. `organizer` 컬럼에 저장 |
| `기업형태` | 링커리어의 주최 기관 유형 |
| `참여대상` | 링커리어의 참여 대상 |
| `접수기간` | 예: `2026.02.25 ~ 2026.03.08`, `2026.04.29 ~ 상시모집`, `2026.04.29 ~ 모집 시 마감` |
| `활동기간` | 예: `26.3 ~ 26.10`, `2026.05.17 ~ 2026.08.07`, `-` |
| `모집인원` | DB 적재 대상 아님 |
| `모임지역` | 대외활동/교육/강연·세미나의 지역 분류 |
| `홈페이지` | 외부 링크. `activity_homepage_url`에 저장 |
| `공모분야` | 공모전 도메인 분류 |
| `활동분야` | 대외활동 분야 분류 |
| `비용/시상규모` | 교육 비용 또는 공모전 시상 금액 |
| `썸네일이미지` | `activity_thumbnail_url`에 저장 |
| `상세이미지` | 필요 시 `activity_upload_file`에 이미지 URL별로 저장 |
| `상세내용` | `description`에 저장 |

---

## 2. 공통 필드 변환

### activity_type

| 링커리어 값 | Picklab enum |
|------------|--------------|
| 대외활동 | `EXTRACURRICULAR` |
| 공모전/해커톤 | `COMPETITION` |
| 교육 | `EDUCATION` |
| 강연/세미나 | `SEMINAR` |

위 값에 없는 활동유형은 적재하지 않는다.

### title

- `제목`을 저장한다.
- DB/API 제한은 50자다.
- 50자를 초과하면 앞 47자를 사용하고 `...`을 붙여 총 50자로 만든다.
- 제목이 비어 있으면 적재하지 않는다.

### organizer

- `주최기관` 값을 그대로 저장한다.
- 현재 `organizer` DB 컬럼은 `VARCHAR(100)`이고, 현재 `linkareer.csv`의 최대 기관명 길이는 50자라 별도 축약하지 않는다.
- 값이 비어 있으면 `null`로 저장한다.

### organizer_type

`기업형태`가 있으면 우선 사용하고, 값이 비어 있거나 애매하면 `주최기관`, `홈페이지`, `상세내용`을 함께 보고 판단한다. 그래도 확실하지 않으면 `ETC`로 저장한다.

| 링커리어 값 | Picklab enum |
|------------|--------------|
| 대기업 | `LARGE_CORPORATION` |
| 중견기업 | `MEDIUM_CORPORATION` |
| 중소기업 | `SMALL_CORPORATION` |
| 공공기관/공기업 | `PUBLIC_ORGANIZATION` |
| 외국계기업 | `FOREIGN_CORPORATION` |
| 비영리단체/협회/재단 | `NON_PROFIT` |
| 스타트업 | `STARTUP` |
| 금융권 | `FINANCIAL_INSTITUTION` |
| 병원 | `HOSPITAL` |
| 기타 | `ETC` |
| 동아리/학생자치단체 | `ETC` |
| 비어 있음 | 본문/기관명으로 판단, 불명확하면 `ETC` |

판단 보조 규칙:
- 기관명에 `시청`, `구청`, `도청`, `교육청`, `공사`, `공단`, `진흥원`, `정부`, `부처`, `대학교`, `대학`, `공공` 등이 명확하면 `PUBLIC_ORGANIZATION`을 우선 고려한다.
- 기관명에 `재단`, `협회`, `사단법인`, `사회복지관`, `복지센터`, `비영리` 등이 명확하면 `NON_PROFIT`을 우선 고려한다.
- `주식회사`, `(주)`, `회사`, `아카데미`, `학원`, `캠프`, `부트캠프` 등만으로 규모 판단이 어려우면 `ETC`로 둔다.

### target_audience

`target_audience`는 필수 enum이므로 `null`로 저장하지 않는다.

| 링커리어 값 | Picklab enum |
|------------|--------------|
| 대학생 | `UNIVERSITY_STUDENT` |
| 직장인/일반인 | `WORKER` |
| 대상 제한 없음 | `ALL` |
| 청소년 포함 | `ALL` |
| 복수 대상 | `ALL` |
| 비어 있음 | 본문/제목으로 판단, 불명확하면 `ALL` |

본문 판단 보조 규칙:
- `대학생`, `대학(원)생`, `재학생`, `휴학생`, `졸업예정자` 중심이면 `UNIVERSITY_STUDENT`.
- `직장인`, `재직자`, `구직자`, `일반인`, `예비창업자`, `청년`, `누구나`, `제한 없음`이거나 여러 대상이 섞이면 `ALL`.
- 교육 데이터처럼 참여대상이 비어 있고 본문에서도 제한이 명확하지 않으면 `ALL`로 저장한다.

### recruitment_start_date, recruitment_end_date, recruitment_end_type

`접수기간`을 파싱한다.

| 접수기간 형태 | recruitment_start_date | recruitment_end_date | recruitment_end_type |
|---------------|------------------------|----------------------|----------------------|
| `2026.02.25 ~ 2026.03.08` | 시작일 | 종료일 | `FIXED` |
| `2026.02.27 ~ 상시모집` | 시작일 | `null` | `ALWAYS_OPEN` |
| `2026.04.29 ~ 모집 시 마감` | 시작일 | `null` | `CLOSE_ON_HIRE` |

`recruitment_end_type = FIXED`이면 `recruitment_end_date`는 반드시 있어야 한다.

### start_date, end_date, duration

`활동기간`을 파싱한다.

- `26.3 ~ 26.10`은 `2026-03-01 ~ 2026-10-01`처럼 월 첫날로 변환한다.
- `2026.05.17 ~ 2026.08.07`은 해당 날짜 그대로 변환한다.
- `-` 또는 비어 있으면 `start_date = recruitment_start_date`, `end_date = null`, `duration = 0`으로 저장한다.
- 기간 계산이 가능하면 `duration`은 시작일과 종료일 사이의 일수로 저장한다.

### status

- 기본값은 `OPEN`.
- 모집 종료 유형이 `FIXED`이고 `recruitment_end_date`가 적재일 기준 과거면 `CLOSED`.
- `ALWAYS_OPEN`은 `OPEN`.
- `CLOSE_ON_HIRE`는 원문에 마감/종료 표현이 명확하지 않으면 `OPEN`.

### URL, 이미지, 본문

- `홈페이지` -> `activity_homepage_url`
- `상세링크` 또는 `/상세링크` -> `activity_application_url`. 상대경로이면 링커리어 도메인을 붙여 절대 URL로 만든다.
- `썸네일이미지` -> `activity_thumbnail_url`
- `상세내용` -> `description`
- URL 필드는 255자 제한이다. `activity_homepage_url`이 255자를 초과하면 잘라서 깨진 URL을 저장하지 말고 `null`로 둔다.
- `description`은 2000자 제한이다. 이모지 등으로 서버 검증 길이가 달라질 수 있으므로 핵심 모집 정보가 남도록 1900자 이내로 줄이고 `...`을 붙인다.
- `benefit`은 본문에서 혜택이 명확히 분리될 때만 추출하고, 불명확하면 빈 문자열로 둔다.

---

## 3. 대외활동 전용 필드

### activity_field

대외활동은 `activity_field`가 필수다. `활동분야`가 복수이면 아래 우선순위로 대표값 하나를 선택한다.

우선순위: `SUPPORTERS` > `MARKETER` > `MENTORING` > `PRESS` > `DOMESTIC_VOLUNTEER` > `OVERSEAS_VOLUNTEER` > `LECTURE` > `ETC`

| 링커리어 값 | Picklab enum |
|------------|--------------|
| 서포터즈 | `SUPPORTERS` |
| 마케터 | `MARKETER` |
| 멘토링 | `MENTORING` |
| 기자단 | `PRESS` |
| 봉사단-국내 | `DOMESTIC_VOLUNTEER` |
| 봉사단-해외 | `OVERSEAS_VOLUNTEER` |
| 해외탐방-유료 | `OVERSEAS_VOLUNTEER` |
| 해외탐방-무료 | `OVERSEAS_VOLUNTEER` |
| 강연 | `LECTURE` |
| 기타 | `ETC` |
| 비어 있음/판단 불가 | `ETC` |

### location

| 링커리어 값 | Picklab enum |
|------------|--------------|
| 지역 제한없음 | `ALL` |
| 서울 포함 | `SEOUL_INCHEON` |
| 인천 포함 | `SEOUL_INCHEON` |
| 경기 포함 | `GYEONGGI_GANGWON` |
| 강원 포함 | `GYEONGGI_GANGWON` |
| 대전/세종/충청/충남 포함 | `DAEJEON_SEJONG_CHUNGNAM` |
| 부산/대구/울산/경상 포함 | `BUSAN_DAEGU_GYEONGSANG` |
| 광주/전라 포함 | `GWANGJU_JEOLLA` |
| 제주 포함 | `JEJU` |
| 해외 포함 | `OVERSEAS` |
| 복수 권역 포함 | `ALL` |
| 비어 있음/판단 불가 | `ALL` |

---

## 4. 교육 전용 필드

교육 데이터는 `기업형태`와 `참여대상`이 비어 있는 경우가 많다. 그래도 현재 생성 API 기준으로 `organizer_type`, `target_audience`, `location`, `cost`, `cost_type`, `education_format`은 채워야 한다.

### location

대외활동의 location 규칙과 동일하게 매핑한다.

### cost, cost_type

`비용/시상규모`를 금액과 비용 유형으로 나누어 저장한다.

| 원본 값 | cost | cost_type |
|---------|------|-----------|
| 무료 | `0` | `FREE` |
| 무료(국비지원) | `0` | `FULLY_GOVERNMENT` |
| 4만원 | `40000` | `PAID` |
| 4만원(국비지원) | `40000` | `PARTIALLY_GOVERNMENT` |
| 비어 있음/파싱 불가 | `0` | `FREE` |

### education_format

`education_format`은 현재 API 필수값이므로 `null`로 저장하지 않는다.

| 판단 근거 | Picklab enum |
|-----------|--------------|
| 온라인, 비대면, Zoom, ZOOM, 줌, 메타버스, 실시간 온라인 | `ONLINE` |
| 오프라인, 대면, 현장, 센터, 캠퍼스, 강의장, 지역 주소 | `OFFLINE` |
| 온라인과 오프라인이 모두 명시됨 | `ALL` |
| 판단 불가 | `ALL` |

---

## 5. 공모전/해커톤 전용 필드

### domain

`공모분야`, `제목`, `상세내용`을 함께 보고 하나의 `DomainType`을 선택한다. 불명확하면 `ETC`로 저장한다.

| 링커리어 공모분야 | 우선 고려 Picklab enum |
|------------------|------------------------|
| 과학/공학 | `AI`, `ELECTRONICS_ROBOTICS`, `IOT` 중 본문 기준 판단 |
| 기획/아이디어 | `BUSINESS_ENTERPRISE` |
| 창업 | `BUSINESS_ENTERPRISE` |
| 사진/영상/UCC | `CONTENT_SOCIAL` |
| 문학/시나리오 | `CONTENT_SOCIAL` |
| 디자인/순수미술/공예 | `CONTENT_SOCIAL` |
| 캐릭터/만화/게임 | `GAME_ENTERTAINMENT` |
| 네이밍/슬로건 | `CONTENT_SOCIAL` |
| 학술 | `EDUCATION` |
| 광고/마케팅 | `CONTENT_SOCIAL` 또는 `BUSINESS_ENTERPRISE` 중 본문 기준 판단 |
| 건축/건설/인테리어 | `ETC` |
| 전시/페스티벌 | `CONTENT_SOCIAL` |
| 예체능/패션 | `CONTENT_SOCIAL` |
| 해외 | `ETC` |
| 기타 | `ETC` |

### cost

공모전의 `cost`는 구간 라벨이 아니라 **원 단위 Long 금액**으로 저장한다. 구간 표시는 FE 또는 검색 필터가 담당한다.

금액이 여러 개 나오면 전체 시상 총액을 우선 사용하고, 총액이 없으면 최고 상금을 사용한다.

예시:
- `300만 원` -> `3000000`
- `1280만 원` -> `12800000`
- `4000만 원` -> `40000000`
- `1억 250만 원` -> `102500000`
- `600억 원` -> `60000000000`
- `-`, 비어 있음, 파싱 불가 -> `0`

---

## 6. 강연/세미나 전용 필드

### location

대외활동의 location 규칙과 동일하게 매핑한다.

---

## 7. 적재 제외 케이스

다음 row는 적재하지 않는다.

- `활동유형`이 매핑 표에 없는 경우
- `제목`이 비어 있는 경우
- `FIXED` 모집인데 모집 종료일을 파싱할 수 없는 경우
- 타입별 필수 필드를 위 규칙으로도 채울 수 없는 경우

---

## 8. 처리 예시

### 공모전 예시

입력:

```text
활동유형: 공모전/해커톤
제목: 2026 방콘진 혁신 방송 신기술 상용화 지원 공모 사업
주최기관: 방송콘텐츠진흥재단
기업형태: 비영리단체/협회/재단
참여대상: 대학생, 직장인/일반인
접수기간: 2026.02.25 ~ 2026.03.08
공모분야: 기획/아이디어; 과학/공학
비용/시상규모: 4000만 원
```

출력:

```text
activity_type: COMPETITION
title: 2026 방콘진 혁신 방송 신기술 상용화 지원 공모 사업
organizer: 방송콘텐츠진흥재단
organizer_type: NON_PROFIT
target_audience: ALL
recruitment_start_date: 2026-02-25
recruitment_end_date: 2026-03-08
recruitment_end_type: FIXED
domain: BUSINESS_ENTERPRISE
cost: 40000000
```

### 교육 예시

입력:

```text
활동유형: 교육
주최기관: 이스트소프트
기업형태:
참여대상:
모임지역: 지역 제한없음
비용/시상규모: 무료(국비지원)
상세내용: 비 수도권 100% 비대면 수업 가능
```

출력:

```text
activity_type: EDUCATION
organizer: 이스트소프트
organizer_type: ETC
target_audience: ALL
location: ALL
cost: 0
cost_type: FULLY_GOVERNMENT
education_format: ONLINE
```
