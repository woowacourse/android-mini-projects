#ifndef DETECTION_CONFIG_H
#define DETECTION_CONFIG_H

// 패드 크기. 현재 초음파 센서는 패드의 긴 방향을 바라본다고 가정한다.
const float PAD_WIDTH_CM = 40.0;
const float PAD_LENGTH_CM = 50.0;

// 패드 길이보다 먼 초음파 값은 패드 외부 물체로 보고 무시한다.
// 센서를 대각선 방향으로 설치했다면 약 64cm로 조정한다.
const float ULTRASONIC_MIN_VALID_DISTANCE_CM = 2.0;
const float ULTRASONIC_PAD_MAX_DISTANCE_CM = PAD_LENGTH_CM;

// 기준 무게보다 이 값 이상 증가하면 강아지 방문 후보
const float VISIT_ENTER_DELTA_G = 700.0;

// 기준 무게보다 증가량이 이 값 미만이면 강아지 퇴장 후보
const float DOG_ABSENCE_DELTA_G = 500.0;

// 잔여 무게가 이 값 미만이면 단순 방문으로 판단
const float VISIT_ONLY_DELTA_G = 20.0;

// 초음파 거리 감소가 이 값 이상이면 대변으로 판단
const float FECES_HEIGHT_DELTA_CM = 2.0;

// 잔여 무게는 있지만 초음파 변화가 이 값 이내면 소변으로 판단
const float URINE_DISTANCE_TOLERANCE_CM = 1.0;

// 방문 시작/종료 안정화 시간
const unsigned long VISIT_START_HOLD_MS = 1500;
const unsigned long EXIT_HOLD_MS = 3000;

// 퇴장 후 잔여 무게와 초음파 값이 안정될 때까지 기다리는 시간
const unsigned long POST_EXIT_STABILIZE_MS = 2000;

// 기준값 주기 보정 설정
const unsigned long CALIBRATION_INTERVAL_MS = 10UL * 60UL * 1000UL;
const unsigned long BASELINE_STABLE_HOLD_MS = 5000;

// 이벤트 발생 후 다음 이벤트를 받을 준비가 될 때까지의 최소 안정 시간
const unsigned long CLEAR_RESET_MS = 30000;

// 기준값 보정 중 센서값이 이 범위 안에서 유지되면 안정 상태로 본다.
const float BASELINE_WEIGHT_STABLE_DELTA_G = 10.0;
const float BASELINE_DISTANCE_STABLE_DELTA_CM = 1.0;

#endif
