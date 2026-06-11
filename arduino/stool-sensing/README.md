# stool-sensing ESP32 Firmware

ESP32 + HC-SR04 초음파 센서 + HX711 로드셀 기반 배변패드 방문/대변 후보 감지 펌웨어.

## 폴더 구조

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
    ├── app/
    │   └── app_controller.h / .cpp
    ├── config/
    │   ├── config.example.h
    │   ├── config.h
    │   └── pins.h
    ├── domain/
    │   ├── event_types.h
    │   └── stool_detector.h / .cpp
    ├── drivers/
    │   ├── ultrasonic_sensor.h / .cpp
    │   └── load_cell_sensor.h / .cpp
    ├── hardware/
    │   ├── boot_button.h / .cpp
    │   └── status_led.h / .cpp
    └── services/
        ├── provisioning_manager.h / .cpp
        ├── sequence_store.h / .cpp
        ├── event_sender.h / .cpp
        └── supabase_client.h / .cpp
```

## 준비

1. Arduino CLI를 설치한다.
2. `src/config/config.example.h`를 참고해 `src/config/config.h`의 Supabase 값을 수정한다.
3. 아래 명령으로 ESP32 core와 HX711 라이브러리를 설치한다.

```powershell
cd arduino/stool-sensing
.\scripts\setup.ps1
```

## 빌드

```powershell
.\scripts\build.ps1
```

기본 FQBN은 `esp32:esp32:esp32`이다.
메인 스케치는 Arduino 규칙에 맞춰 폴더명과 같은 `stool-sensing.ino`이다.

## 업로드

포트는 환경에 맞게 바꾼다.

```powershell
.\scripts\upload.ps1 -Port COM3
```

## 시리얼 모니터

```powershell
.\scripts\monitor.ps1 -Port COM3
```

## 핀 연결

- HC-SR04 VCC -> ESP32 5V/VIN
- HC-SR04 GND -> ESP32 GND
- HC-SR04 TRIG -> ESP32 GPIO25
- HC-SR04 ECHO -> 전압분배 후 ESP32 GPIO26
- HX711 DT/DOUT -> ESP32 GPIO32
- HX711 SCK -> ESP32 GPIO33
- HX711 VCC -> ESP32 3.3V 또는 모듈 사양에 맞는 전원
- HX711 GND -> ESP32 GND

주의: HC-SR04 ECHO는 5V 출력이므로 ESP32 GPIO에 직접 연결하지 않는다.

## Wi-Fi 구조

최종적으로 ESP32가 집 Wi-Fi에 접속해서 Supabase에 이벤트를 보낸다.
초기 설정 때만 ESP32가 임시 SoftAP를 연다.

- SoftAP SSID: DOGNAL-Setup-XXXX
- SoftAP Password: dognal1234
- SoftAP IP: 192.168.4.1

앱은 설정 중에만 `DOGNAL-Setup-XXXX`에 접속하고, `POST /provision`으로 집 Wi-Fi 정보를 전달한다.

## SoftAP API

- `GET /scan`: 주변 Wi-Fi 목록 반환
- `POST /provision`: `application/x-www-form-urlencoded` 형식으로 `ssid`, `password` 전달
- `GET /status`: `connected`, `setupSsid`, `ip`, `rssi` 반환

## Supabase 이벤트

ESP32는 아래 RPC를 호출한다.

```http
POST /rest/v1/rpc/ingest_sensor_event
```

현재 이벤트 범위:

- DEVICE_READY
- VISIT_DETECTED
- STOOL_DETECTED
- SENSOR_ERROR

`URINE_DETECTED`는 아직 보내지 않는다.
