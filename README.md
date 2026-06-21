# 도그널 (Dognal)

강아지 배변패드 방문·소변·대변을 자동으로 감지하고 기록하는 IoT 앱 프로젝트입니다.
ESP32 하드웨어가 센서로 이벤트를 감지하면 Supabase를 거쳐 Android/iOS 앱에 푸시 알림이 전송되고, 앱에서 기록을 조회·관리할 수 있습니다.

> 우아한테크코스 8기 Android 미니 프로젝트 · 2026.05.28 ~ 2026.06.18 (4주)

피그마 디자인: [링크 바로가기](https://www.figma.com/design/HtTu8krbr2kOYGiWt2uLZ9/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%AF%B8%EB%8B%88-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8?node-id=0-1&t=bFMTjGwftHtI5Dw9-1)

---

## 시연

> 📹 센서 감지 → 푸시 알림 → 앱 기록 흐름

| 기기 등록 | 홈 화면 | 기록 목록 |
|---|---|---|
| <img width="180" height="405" alt="image" src="https://github.com/user-attachments/assets/be86ff42-e82d-4001-be78-5f27f3b29518" /> | <img width="180" height="405" alt="image" src="https://github.com/user-attachments/assets/df6c3fb1-e262-45e0-b884-5b0dba2ca530" /> | <img width="180" height="405" alt="image" src="https://github.com/user-attachments/assets/99a2d338-ff9a-46aa-986b-09ea4b753841" /> |

| 아두이노 기기 시연 | 푸시 알림 |
| --- | --- |
| <img width="4000" height="2252" alt="image" src="https://github.com/user-attachments/assets/83076804-f387-4e57-a8df-4e8a7d3ba86e" /> | <img width="1440" height="1041" alt="image" src="https://github.com/user-attachments/assets/99c64a08-4b6e-4f77-ae76-1d34f47d5170" /> |

---

## 팀 구성 및 담당

4인 팀으로 진행했으며, 각자 아래 영역을 담당했습니다.

| 담당 | 영역 |
|------|------|
| 허닛 - 팀장 [@BaekCCI](https://github.com/BaekCCI) | 아두이노 회로 연결, 아두이노 센서 및 통신 로직 구현, supabase edge functions 구현  |
| 모스 [@katie0109](https://github.com/katie0109) | 아두이노 회로 설계, 아두이노 센서 및 통신 로직 구현, supabase database 스키마 설계 구축  |
| 별터 [@todays-sun-day](https://github.com/todays-sun-day) | Android/iOS 앱 UI 구현 (Compose Multiplatform), Supabase 연동 |
| 로미 [@parkhyomi](https://github.com/parkhyomi) | Android/iOS 앱 UI 구현 (Compose Multiplatform), 기록 추가 시 Android 앱 푸시 알림 처리 (FCM 연동) |

---

## 시스템 구성

```
[ESP32 (로드셀 + 초음파)]
        │ HTTP POST (RPC)
        ▼
[Supabase potty_records 테이블]
        │ DB Webhook
        ▼
[Edge Function: notify-potty-record-insert]
        │ FCM
        ▼
[Dognal Android / iOS 앱 푸시 알림]
        │
        ▼
[앱에서 기록 조회 · 수동 추가 · 수정 · 삭제]
```

---

## 저장소 구조

```
android-mini-projects/
├── Dognal/          # Kotlin Multiplatform 앱 (Android + iOS)
├── supabase/        # Supabase Edge Function + SQL 마이그레이션
├── arduino/         # ESP32 펌웨어 (C++)
├── docs/            # 설계 문서
└── reports/         # 주차별 회고
```

---

## Dognal 앱 (Kotlin Multiplatform)

### 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| 언어 | Kotlin 2.3.21 |
| UI | Compose Multiplatform 1.11.0 |
| 아키텍처 | MVVM + MVI (UiState / ViewModel / Repository) |
| 네트워크 | Supabase Kotlin SDK 3.6.0 + Ktor 3.5.0 |
| 로컬 저장소 | Multiplatform Settings |
| 푸시 알림 | Firebase Cloud Messaging (Android) |
| 내비게이션 | Navigation Compose |
| 빌드 | Gradle + BuildKonfig |

### 모듈 구성

```
Dognal/
├── androidApp/    # Android 진입점 (MainActivity, AndroidPushTokenProvider)
└── shared/        # 공유 모듈
    └── src/
        ├── commonMain/   # UI · 비즈니스 로직 · 데이터 계층 전부
        ├── androidMain/  # Android 전용 구현 (DatePicker 등)
        └── iosMain/      # iOS 전용 구현 (NativePicker 등)
```

### 화면 흐름

```
앱 시작
 ├── 기기 미등록 → [기기 등록 안내] → [코드·시크릿 입력] → [홈]
 └── 기기 등록됨 → [홈]
                      ↕ 탭 전환
                   [기록 목록]
```

| 화면 | 기능 |
|------|------|
| 기기 등록 안내 | Arduino 키트 등록 전 체크리스트 안내 |
| 기기 코드 입력 | `device_code + device_secret`으로 인증 후 deviceId 로컬 저장, FCM 토큰 등록 |
| 홈 | 오늘 방문·소변·대변 횟수 요약, 마지막 기록 시각 표시 |
| 기록 목록 | 전체 기록 타임라인 조회, 수동 기록 추가·수정·삭제 |

### 로컬 빌드

#### 1. Supabase 키 설정
`Dognal/local.properties`에 추가:

```properties
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_KEY=<anon-key>
```

2. Firebase 설정 (푸시 알림)

Firebase Console에서 Android 앱을 등록하고 `google-services.json`을 다운로드한 뒤
`Dognal/androidApp/google-services.json`에 위치시킵니다.

3. Android 실행

Android Studio에서 `androidApp` 모듈 실행

---

## Supabase 백엔드

### 주요 테이블 및 함수

| 객체 | 역할 |
|------|------|
| `public.devices` | 기기 정보 (`device_code`, `device_secret_hash`) |
| `public.potty_records` | 배변 기록 (`record_type`, `occurred_at`, `source`, `note`) |
| `public.device_push_tokens` | FCM 토큰 저장 |
| `ingest_sensor_event(...)` | ESP32가 호출하는 이벤트 수신 RPC |
| `verify_device(...)` | 앱이 호출하는 기기 인증 RPC |
| `register_device_push_token(...)` | FCM 토큰 등록 RPC |

> `device_secret` 원문은 ESP32와 사용자 입력에서만 사용하고, DB에는 해시(`device_secret_hash`)만 저장합니다.

### SQL 마이그레이션 실행 순서

새 DB를 초기화할 때:

```bash
001_create_schema.sql
002_ingest_sensor_event_rpc.sql
003_seed_test_device.sql
```

푸시 알림을 활성화할 때:

```bash
008_device_push_tokens.sql
009_push_notification_permissions.sql
# Edge Function 배포 후:
010_notify_potty_record_insert_webhook.sql  # WEBHOOK_SECRET 치환 필요
```

### Edge Function 배포

```bash
supabase functions deploy notify-potty-record-insert --no-verify-jwt
supabase secrets set FCM_SERVICE_ACCOUNT_JSON='{ ... }'
supabase secrets set FIREBASE_PROJECT_ID='your-firebase-project-id'
supabase secrets set WEBHOOK_SECRET='replace-with-long-random-string'
```

자세한 내용은 [supabase/README.md](supabase/README.md)를 참고하세요.

---

## Arduino 펌웨어 (ESP32)

### 하드웨어 구성

| 센서 | 용도 |
|------|------|
| HC-SR04 초음파 센서 | 배변패드 접근 거리 감지 |
| HX711 + 로드셀 | 무게 변화로 배변 여부 감지 |

### 핀 연결

| 핀 | ESP32 GPIO |
|----|------------|
| HC-SR04 TRIG | GPIO 25 |
| HC-SR04 ECHO | GPIO 26 (전압분배 필수) |
| HX711 DT | GPIO 32 |
| HX711 SCK | GPIO 33 |

### 빌드 및 업로드

```powershell
cd arduino/stool-sensing

# 의존성 설치 (최초 1회)
.\scripts\setup.ps1

# 빌드
.\scripts\build.ps1

# 업로드 (포트는 환경에 맞게)
.\scripts\upload.ps1 -Port COM3

# 시리얼 모니터
.\scripts\monitor.ps1 -Port COM3
```

Wi-Fi 초기 설정은 `src/config/config.example.h`를 복사해 `config.h`를 만들고 Supabase 값을 채웁니다.

자세한 내용은 [arduino/stool-sensing/README.md](arduino/stool-sensing/README.md)를 참고하세요.
