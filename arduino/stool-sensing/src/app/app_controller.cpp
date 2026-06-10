#include <Arduino.h>
#include <math.h>

#include "app_controller.h"
#include "../config/config.h"
#include "../config/pins.h"
#include "../config/timing.h"
#include "../domain/event_types.h"

AppController::AppController()
  : ultrasonicSensor(TRIG_PIN, ECHO_PIN),
    distanceSampler(ultrasonicSensor, 5),
    loadCellSensor(LOAD_CELL_DOUT_PIN, LOAD_CELL_SCK_PIN),
    supabaseClient(SUPABASE_URL, SUPABASE_API_KEY, DEVICE_CODE, DEVICE_SECRET),
    eventSender(sequenceStore, supabaseClient),
    statusLed(STATUS_LED_PIN),
    bootButton(BOOT_BUTTON_PIN) {
  isDetectorReady = false;
  isCalibrationComplete = false;
  isLoadCellReady = false;
  isDeviceReadySent = false;
  lastDistanceCm = -1.0;
  lastWeightGram = NAN;
  lastDebugPrintAt = 0;
}

void AppController::begin() {
  beginSerial();
  beginHardware();
  beginServices();
  beginNetwork();
}

void AppController::update() {
  unsigned long now = millis();

  updateProvisioning(now);
  updateStatusLed(now);
  updateBootButton(now);
  updateDeviceReadyEvent();
  updateSensors(now);
  updateEventSending(now);
  updateDebugPrint(now);
}

void AppController::beginSerial() {
  Serial.begin(115200);
  // USB CDC/Serial Monitor가 부팅 로그를 받을 수 있도록 하는 짧은 안정화 지연이다.
  delay(300);

  Serial.println();
  Serial.println("====================================");
  Serial.println("DOGNAL ESP32 stool-sensing firmware");
  Serial.println("====================================");
}

void AppController::beginHardware() {
  statusLed.begin();
  bootButton.begin();
  ultrasonicSensor.begin();
  distanceSampler.begin();

  isLoadCellReady = loadCellSensor.begin(
    LOAD_CELL_CALIBRATION_FACTOR,
    LOAD_CELL_TARE_SAMPLES
  );

  Serial.println("Calibrating empty pad baseline...");
  Serial.println("센서 아래에 아무것도 두지 말고 기다리세요.");
}

void AppController::beginServices() {
  sequenceStore.begin();
  provisioningManager.begin();
}

void AppController::beginNetwork() {
  if (provisioningManager.hasSavedCredentials()) {
    provisioningManager.startSavedWiFiConnection(WIFI_CONNECT_TIMEOUT_MS);
  } else {
    provisioningManager.startPortal();
  }
}

void AppController::updateProvisioning(unsigned long now) {
  provisioningManager.update(now);

  if (provisioningManager.hasSavedConnectionFailed() &&
      !provisioningManager.isPortalActive()) {
    provisioningManager.startPortal();
  }
}

void AppController::updateStatusLed(unsigned long now) {
  if (provisioningManager.isConnected()) {
    statusLed.setOn(true);
  } else {
    statusLed.updateBlink(now, STATUS_LED_BLINK_MS);
  }
}

void AppController::updateBootButton(unsigned long now) {
  if (!bootButton.updateLongPress(now, BOOT_BUTTON_RESET_MS)) {
    return;
  }

  Serial.println("BOOT button long press detected.");
  Serial.println("Resetting WiFi credentials and starting SoftAP portal.");

  provisioningManager.clearCredentials();
  provisioningManager.startPortal();

  isDeviceReadySent = false;
}

void AppController::updateDeviceReadyEvent() {
  if (!provisioningManager.isConnected()) {
    return;
  }

  if (!isCalibrationComplete || isDeviceReadySent || eventSender.hasPendingEvent()) {
    return;
  }

  if (isDetectorReady) {
    eventSender.queueEvent(EVENT_DEVICE_READY);
  } else {
    eventSender.queueEvent(EVENT_SENSOR_ERROR);
  }
}

void AppController::updateSensors(unsigned long now) {
  distanceSampler.update(now);

  if (!distanceSampler.hasNewAverage()) {
    return;
  }

  float distanceCm = distanceSampler.readAverageCm();
  handleDistanceSample(distanceCm);
}

void AppController::handleDistanceSample(float distanceCm) {
  lastDistanceCm = distanceCm;
  lastWeightGram = isLoadCellReady ? loadCellSensor.readWeightG(5) : NAN;

  if (!isCalibrationComplete) {
    isDetectorReady = stoolDetector.begin(distanceCm);
    isCalibrationComplete = true;
    return;
  }

  if (!isDeviceReadySent || eventSender.hasPendingEvent()) {
    return;
  }

  DetectionResult result = stoolDetector.update(distanceCm);

  if (result.hasEvent) {
    eventSender.queueEvent(result.eventType);
  }
}

void AppController::updateEventSending(unsigned long now) {
  bool sent = eventSender.update(now, provisioningManager.isConnected());

  if (sent) {
    handleSentEvent();
  }
}

void AppController::handleSentEvent() {
  const String& eventType = eventSender.getLastSentEventType();

  if (eventType == EVENT_DEVICE_READY ||
      eventType == EVENT_SENSOR_ERROR) {
    isDeviceReadySent = true;
  }
}

void AppController::updateDebugPrint(unsigned long now) {
  if (now - lastDebugPrintAt < DEBUG_PRINT_MS) {
    return;
  }

  lastDebugPrintAt = now;
  printRuntimeDebug();
}

void AppController::printRuntimeDebug() {
  Serial.print("Distance: ");

  if (lastDistanceCm < 0) {
    Serial.print("INVALID");
  } else {
    Serial.print(lastDistanceCm, 1);
    Serial.print(" cm");
  }

  Serial.print(" | Baseline: ");
  Serial.print(stoolDetector.getBaselineCm(), 1);
  Serial.print(" cm");

  Serial.print(" | Detector: ");
  Serial.print(stoolDetector.getStateName());

  Serial.print(" | Weight: ");
  if (isnan(lastWeightGram)) {
    Serial.print("N/A");
  } else {
    Serial.print(lastWeightGram, 1);
    Serial.print(" g");
  }

  Serial.print(" | WiFi: ");
  Serial.print(provisioningManager.isConnected() ? "CONNECTED" : "DISCONNECTED");

  Serial.print(" | Pending: ");
  Serial.println(eventSender.hasPendingEvent() ? eventSender.getPendingEventType() : "NONE");
}
