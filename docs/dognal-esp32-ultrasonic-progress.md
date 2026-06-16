# DOGNAL ESP32 초음파 배변 감지기 진행 기록

## 1. 프로젝트 목표

DOGNAL 배변 감지기 프로젝트는 ESP32 기반 IoT 기기가 배변패드 주변 상태를 감지하고, 감지 이벤트를 Supabase에 저장하는 구조를 목표로 한다.

이번 단계에서는 로드셀과 HX711 모듈이 아직 준비되지 않았기 때문에, HC-SR04 초음파 센서만 사용해 1차 동작 흐름을 검증했다.

1차 목표는 다음과 같았다.

```text
ESP32 부팅
-> 초음파 센서 거리 측정
-> 초기 설정용 SoftAP 생성
-> 아이폰으로 Wi-Fi 정보 전달
-> ESP32가 핫스팟/Wi-Fi에 연결
-> Supabase RPC 호출
-> DEVICE_READY / VISIT_DETECTED 이벤트 저장
```

## 2. 최종 폴더 구조

Arduino 펌웨어는 VS Code와 Arduino CLI에서 다루기 쉽도록 아래 구조로 정리했다.

```text
arduino/stool-sensing/
├── arduino.json
├── README.md
├── scripts/
│   ├── setup.ps1
│   ├── build.ps1
│   ├── upload.ps1
│   └── monitor.ps1
└── src/
    ├── main.ino
    ├── app/
    │   ├── app_controller.h
    │   └── app_controller.cpp
    ├── config/
    │   ├── config.example.h
    │   ├── config.h
    │   └── pins.h
    ├── drivers/
    │   ├── ultrasonic_sensor.h
    │   ├── ultrasonic_sensor.cpp
    │   ├── load_cell_sensor.h
    │   └── load_cell_sensor.cpp
    ├── domain/
    │   ├── event_types.h
    │   ├── stool_detector.h
    │   └── stool_detector.cpp
    └── services/
        ├── provisioning_manager.h
        ├── provisioning_manager.cpp
        ├── sequence_store.h
        ├── sequence_store.cpp
        ├── supabase_client.h
        └── supabase_client.cpp
```

각 폴더의 역할은 다음과 같다.

```text
app/      전체 펌웨어 실행 흐름 제어
config/   핀 번호, Supabase URL, 기기 코드 등 설정
drivers/  초음파 센서, 로드셀 같은 하드웨어 드라이버
domain/   방문/대변 감지 상태머신과 이벤트 타입
services/ Wi-Fi 설정 포털, Supabase 전송, seq 저장
scripts/  Arduino CLI 빌드/업로드/모니터 실행 스크립트
```

`config.h`에는 실제 Supabase 정보와 기기 secret이 들어가기 때문에 `.gitignore`에 포함했다.

## 3. 사용한 주요 기술

```text
MCU: ESP32 DevKit
Sensor: HC-SR04 ultrasonic sensor
Future sensor: HX711 + load cell
Firmware: Arduino framework
Build tool: Arduino CLI
Network setup: ESP32 SoftAP + HTTP provisioning
Backend: Supabase PostgreSQL + PostgREST RPC
Database function: public.ingest_sensor_event
```

## 4. ESP32 Wi-Fi 설정 방식

처음 부팅하면 ESP32에는 집 Wi-Fi 정보가 없기 때문에 임시 설정용 SoftAP를 생성한다.

```text
SSID: DOGNAL-Setup-XXXX
Password: dognal1234
IP: 192.168.4.1
```

아이폰은 이 임시 Wi-Fi에 연결한 뒤, 단축어 앱을 사용해 ESP32에 실제 Wi-Fi 정보를 전달했다.

```http
POST http://192.168.4.1/provision
Content-Type: application/x-www-form-urlencoded

ssid=연결할 Wi-Fi 이름
password=연결할 Wi-Fi 비밀번호
```

ESP32는 전달받은 SSID와 비밀번호를 Preferences에 저장하고, 이후 재부팅 시 자동으로 같은 Wi-Fi에 연결한다.

이번 테스트에서는 회사 Wi-Fi가 사용자 이름/비밀번호 기반 인증 방식이라 ESP32 연결에 적합하지 않았다. 그래서 아이폰 핫스팟을 사용했다.

## 5. Supabase 설정

ESP32는 Supabase 테이블에 직접 insert하지 않고, PostgreSQL RPC 함수를 호출한다.

```http
POST /rest/v1/rpc/ingest_sensor_event
```

전송 payload 예시는 다음과 같다.

```json
{
  "p_device_code": "pad-001",
  "p_device_secret": "replace-with-device-secret",
  "p_seq": 1,
  "p_event_type": "DEVICE_READY",
  "p_schema_version": 1,
  "p_rssi": -53,
  "p_ip_address": "172.20.10.3"
}
```

기기 정보는 `public.devices`에 등록한다.

```text
device_code: pad-001
device_secret_hash: replace-with-device-secret의 hash 값
```

ESP32에는 원문 secret이 들어가지만, DB에는 원문이 아니라 `device_secret_hash`를 저장하는 방향으로 정리했다.

Supabase SQL 파일은 다음 역할을 가진다.

```text
supabase/sql/002_ingest_sensor_event_rpc.sql
  ESP32 이벤트 수신 RPC와 potty_records 자동 생성 trigger

supabase/sql/003_seed_test_device.sql
  테스트용 pad-001 기기 등록

supabase/sql/004_rpc_test.sql
  SQL Editor에서 RPC 동작 확인
```

## 6. PC 개발환경 확인

PowerShell에서 Arduino CLI와 ESP32 core를 확인했다.

```powershell
arduino-cli version
arduino-cli core list
arduino-cli lib list
```

필요 라이브러리로 HX711을 설치했다. 현재는 모듈이 없어도 빌드에는 필요하다.

```powershell
arduino-cli lib install HX711
```

프로젝트 빌드 명령은 다음과 같다.

```powershell
cd D:\android-mini-projects\arduino\stool-sensing
.\scripts\build.ps1
```

빌드 성공 결과:

```text
Sketch uses 1080972 bytes (82%) of program storage space.
Global variables use 48992 bytes (14%) of dynamic memory.
```

## 7. ESP32 연결과 업로드

ESP32를 USB로 연결한 뒤 포트를 확인했다.

```powershell
arduino-cli board list
```

확인된 포트:

```text
COM11 serial Serial Port (USB)
```

업로드 명령:

```powershell
.\scripts\upload.ps1 -Port COM11
```

시리얼 모니터 명령:

```powershell
.\scripts\monitor.ps1 -Port COM11
```

## 8. 초음파 센서 배선

HC-SR04 초음파 센서는 다음 핀으로 연결했다.

```text
HC-SR04 VCC  -> ESP32 5V/VIN
HC-SR04 GND  -> ESP32 GND
HC-SR04 TRIG -> ESP32 GPIO25
HC-SR04 ECHO -> 전압분배 후 ESP32 GPIO26
```

주의할 점:

```text
HC-SR04 ECHO는 5V 출력이다.
ESP32 GPIO는 3.3V 기준이므로 ECHO를 바로 연결하면 안 된다.
전압분배 회로를 거쳐야 한다.
```

## 9. 감지 로직

초음파 센서는 부팅 시 빈 패드 기준 거리를 측정한다.

예시 로그:

```text
Calibrating empty pad baseline...
Baseline distance: 216.3 cm
```

이후 거리 변화에 따라 상태머신이 동작한다.

```text
IDLE
-> 물체가 가까이 감지됨
-> VISIT_ACTIVE
-> 물체가 사라짐
-> POST_VISIT_CHECK
-> 일정 시간 후 VISIT_DETECTED 또는 STOOL_DETECTED
```

현재 테스트용 기준:

```text
VISIT_START_HOLD_MS = 1500ms
VISIT_END_HOLD_MS = 3000ms
POST_VISIT_DECIDE_MS = 5000ms
STOOL_HOLD_MS = 10000ms
```

처음에는 방문 후 판단 대기 시간이 15초였지만, 테스트 편의를 위해 5초로 줄였다.

## 10. 성공 로그

ESP32가 Wi-Fi에 연결되고 Supabase로 `DEVICE_READY`를 보낸 로그:

```text
Connecting to saved WiFi: 노윤지
WiFi connected. IP: 172.20.10.3
Queued event: DEVICE_READY seq=2
Sending pending event: DEVICE_READY seq=2
HTTP code: 200
Event sent successfully: DEVICE_READY
```

`DEVICE_READY`는 기기 온라인 상태 확인용 신호라서 현재 정책에서는 `sensor_events`에 저장하지 않는다. 대신 `devices.last_seen_at`과 `device_status`만 갱신한다.

초음파 센서로 방문을 감지한 로그:

```text
Distance: 3.3 cm | Detector: IDLE
Visit started.
Distance: 216.1 cm | Detector: VISIT_ACTIVE
Visit ended. Start post-visit check.
Final result: VISIT_DETECTED
Queued event: VISIT_DETECTED seq=4
Sending pending event: VISIT_DETECTED seq=4
HTTP code: 200
Event sent successfully: VISIT_DETECTED
```

이 로그는 다음이 모두 성공했음을 의미한다.

```text
초음파 거리 측정 성공
방문 시작 감지 성공
방문 종료 감지 성공
VISIT_DETECTED 이벤트 생성 성공
Supabase RPC 호출 성공
```

## 11. DB 확인 방법

원본 센서 이벤트는 `public.sensor_events`에서 확인한다.

```sql
select
  d.device_code,
  se.seq,
  se.event_type,
  se.received_at,
  se.created_at
from public.sensor_events se
join public.devices d on d.id = se.device_id
where d.device_code = 'pad-001'
order by se.received_at desc
limit 20;
```

기기 상태는 `public.device_status`에서 확인한다.

```sql
select
  d.device_code,
  ds.connection_state,
  ds.ultrasonic_ok,
  ds.rssi,
  ds.ip_address,
  ds.updated_at
from public.device_status ds
join public.devices d on d.id = ds.device_id
where d.device_code = 'pad-001';
```

앱 표시용 기록은 `public.potty_records`에서 확인한다.

```sql
select
  pr.id,
  d.device_code,
  pr.record_type,
  pr.source,
  pr.occurred_at,
  pr.created_at
from public.potty_records pr
left join public.devices d on d.id = pr.device_id
order by pr.created_at desc
limit 20;
```

단, `potty_records`는 기기가 사용자 `user_id`에 연결되어 있어야 생성된다.

## 12. 진행 중 해결한 문제

### 12.1 Arduino CLI 스케치 구조 문제

`src/main.ino`를 직접 Arduino CLI에 넘기면 스케치 폴더 이름 규칙 때문에 실패했다.

해결:

```text
scripts/build.ps1
scripts/upload.ps1
```

에서 `src` 내용을 `build/sketch/main` 임시 스케치로 구성한 뒤 컴파일하도록 수정했다.

### 12.2 헤더 중복 include 문제

하위 폴더 구조로 정리한 후 같은 헤더가 경로 차이로 중복 포함되었다.

해결:

```text
#pragma once
```

대신 모든 헤더에 명시적인 include guard를 추가했다.

### 12.3 Supabase RPC 404

초기에는 다음 에러가 발생했다.

```text
Could not find the function public.ingest_sensor_event
```

원인:

```text
RPC 함수가 Supabase에 아직 생성되지 않았거나 schema cache가 갱신되지 않음
```

해결:

```sql
-- 002_ingest_sensor_event_rpc.sql 실행
notify pgrst, 'reload schema';
```

### 12.4 device_secret_hash 컬럼 불일치

초기 DB 스키마에는 `device_secret` 컬럼이 있었고, RPC는 `device_secret_hash`를 기대했다.

결정:

```text
장기적으로 device_secret_hash를 사용하는 방식이 더 안전하므로
DB를 device_secret_hash 기준으로 맞추기로 함
```

### 12.5 crypt 함수 경로 문제

Supabase RPC 내부에서 다음 에러가 발생했다.

```text
function crypt(text, text) does not exist
```

원인:

```text
pgcrypto 함수가 public search_path에서 바로 보이지 않음
```

해결:

```sql
extensions.crypt(...)
extensions.gen_salt(...)
```

처럼 스키마를 명시했다.

## 13. 현재 완료 상태

현재 완료된 항목:

```text
코드 구조 정리 완료
Arduino CLI 빌드 성공
ESP32 업로드 성공
시리얼 모니터 확인 성공
초음파 센서 거리 측정 성공
SoftAP 설정 포털 동작 성공
아이폰 단축어로 Wi-Fi 정보 전달 성공
ESP32 Wi-Fi 연결 성공
Supabase RPC 호출 성공
DEVICE_READY 전송 성공
VISIT_DETECTED 전송 성공
```

이번 단계의 결론:

```text
초음파 기반 방문 감지 1차 검증은 성공했다.
```

## 14. 다음 단계

다음으로 진행할 수 있는 작업:

```text
1. mock 데이터 정리 후 실제 이벤트만 DB에 쌓이도록 테스트
2. device_secret_hash 스키마를 실제 Supabase DB에 완전히 반영
3. pad-001 기기를 실제 사용자 user_id에 연결
4. potty_records 자동 생성 확인
5. 감지 기준값 튜닝
6. HX711/로드셀 배송 후 무게 변화 감지 추가
7. STOOL / URINE / VISIT 최종 판정 로직 확장
8. 앱 화면에서 device_status와 sensor_events 표시
```
