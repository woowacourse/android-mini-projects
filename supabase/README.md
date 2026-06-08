# Supabase

멍끄적(Mungjeok) 프로젝트의 Supabase 데이터베이스 및 백엔드 인프라 구성 파일을 관리한다.

---

# 목적

본 디렉토리는 다음 항목을 관리한다.

* PostgreSQL 데이터베이스 스키마
* 조회용 View
* RLS(Row Level Security) 정책
* Mock 데이터
* Edge Function
* ESP32 ↔ Supabase 데이터 계약

---

# 디렉토리 구조

```text
supabase/
├── README.md
├── migrations/
│   ├── 001_create_schema.sql
│   ├── 002_create_views.sql
│   ├── 003_create_rls.sql
│   └── 004_seed_mock_data.sql
│
└── 
```

---

# 초기 세팅 순서

Supabase Dashboard에서 프로젝트를 생성한 후 SQL Editor에서 아래 순서대로 실행한다.

## 1. Schema 생성

실행 파일

```text
001_create_schema.sql
```

생성 항목

```text
Enum
profiles
pets
devices
device_status
device_settings
sensor_events
potty_records
record_edit_logs
```

---

## 2. View 생성

실행 파일

```text
002_create_views.sql
```

생성 View

```text
v_daily_summaries
v_latest_potty_record
v_device_connection_status
v_potty_record_timeline
```

---

## 3. RLS 정책 적용

실행 파일

```text
003_create_rls.sql
```

적용 대상

```text
profiles
pets
devices
device_status
device_settings
sensor_events
potty_records
record_edit_logs
```

---

## 4. Mock 데이터 삽입

실행 파일

```text
004_seed_mock_data.sql
```

주의

실행 전 Auth에서 테스트 계정을 생성한 뒤 UUID를 수정해야 한다.

예시

```sql
'11111111-1111-1111-1111-111111111111'
```

↓

```sql
'<실제 Auth User UUID>'
```

---

# 앱 조회용 View

CMP 앱은 테이블을 직접 조회하지 않고 View를 우선 사용한다.

## Home

```text
v_daily_summaries
v_latest_potty_record
```

표시 항목

```text
오늘 소변 횟수
오늘 대변 횟수
오늘 방문 횟수
마지막 감지 시간
```

---

## Device Connect

```text
v_device_connection_status
```

표시 항목

```text
기기 이름
연결 상태
로드셀 상태
초음파 상태
보정 여부
```

---

## Timeline

```text
v_potty_record_timeline
```

표시 항목

```text
패드 방문
소변
대변
발생 시간
```

---

# ESP32 이벤트

ESP32는 데이터베이스에 직접 접근하지 않는다.

모든 이벤트는 Edge Function으로 전송한다.

```http
POST /functions/v1/device-events
```

대표 이벤트

```text
DEVICE_READY
HEARTBEAT
CALIBRATION_DONE
VISIT_START
VISIT_END
URINE_DETECTED
FECES_DETECTED
UNKNOWN
SENSOR_ERROR
```

---

# 개발 규칙

## 금지

ESP32에 아래 값을 저장하지 않는다.

```text
service_role key
database password
```

---

## 허용

ESP32에는 아래 정보만 사용한다.

```text
device_code
device_secret
Edge Function URL
```

---

# 관련 문서

```text
docs/database/supabase-design.md
docs/api/frontend-contract.md
docs/api/openapi.yaml
```

---

# 변경 이력

## v0.3

* 패드 포화도 기능 제거
* pad_sessions 제거
* saturation_percent 제거
* PAD_CHANGED 이벤트 제거
* 배변 기록 중심 구조로 변경

```
