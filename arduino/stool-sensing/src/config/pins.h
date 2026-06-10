#ifndef PINS_H
#define PINS_H

// HC-SR04 초음파 센서
#define TRIG_PIN 25
#define ECHO_PIN 26

// 상태 LED
// 일부 ESP32 DevKit은 내장 LED가 GPIO2에 있다.
// 없는 보드라면 외부 LED를 연결하거나 무시해도 된다.
#define STATUS_LED_PIN 2

// BOOT 버튼
// 대부분 ESP32 DevKit에서 BOOT 버튼은 GPIO0이다.
// 길게 누르면 Wi-Fi 설정 초기화 용도로 사용한다.
#define BOOT_BUTTON_PIN 0

// HX711 로드셀 앰프
// DT/DOUT -> GPIO32, SCK -> GPIO33 기준.
#define LOAD_CELL_DOUT_PIN 32
#define LOAD_CELL_SCK_PIN 33

#endif
