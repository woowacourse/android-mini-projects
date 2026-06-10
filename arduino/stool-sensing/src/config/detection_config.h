#ifndef DETECTION_CONFIG_H
#define DETECTION_CONFIG_H

// 강아지 몸체로 판단할 절대 거리 기준
const float DOG_PRESENT_ABSOLUTE_CM = 20.0;

// 빈 패드 대비 거리 감소량이 이 값 이상이면 강아지 몸체 후보
const float DOG_BODY_DELTA_CM = 12.0;

// 대변 후보 높이 범위
const float STOOL_MIN_DELTA_CM = 2.5;
const float STOOL_MAX_DELTA_CM = 12.0;

// 빈 패드로 판단하는 허용 오차
const float PAD_CLEAR_DELTA_CM = 1.5;

// 방문 시작/종료 안정화 시간
const unsigned long VISIT_START_HOLD_MS = 1500;
const unsigned long VISIT_END_HOLD_MS = 3000;

// 너무 짧은 방문은 무시
const unsigned long MIN_VISIT_MS = 3000;

// 방문 종료 후 대변 후보가 이 시간 이상 유지되면 STOOL_DETECTED
const unsigned long STOOL_HOLD_MS = 10000;

// 방문 종료 후 이 시간 동안 대변 후보가 없으면 VISIT_DETECTED
const unsigned long POST_VISIT_DECIDE_MS = 5000;

// 이벤트 발생 후 패드가 비어 있는 상태가 이 시간 유지되면 다음 이벤트 준비
const unsigned long CLEAR_RESET_MS = 30000;

#endif
