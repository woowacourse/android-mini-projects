#include <Arduino.h>
#include "../config/timing.h"
#include "load_cell_sensor.h"

LoadCellSensor::LoadCellSensor(uint8_t doutPin, uint8_t sckPin) {
  this->doutPin = doutPin;
  this->sckPin = sckPin;
  ready = false;
}

bool LoadCellSensor::begin(float calibrationFactor, int tareSamples) {
  Serial.println("Initializing HX711 load cell...");

  scale.begin(doutPin, sckPin);
  scale.set_scale(calibrationFactor);

  unsigned long startedAt = millis();

  // HX711은 전원 인가 직후 첫 ready 신호가 늦게 들어올 수 있어 초기화 때만 짧게 기다린다.
  while (!scale.is_ready() && millis() - startedAt < LOAD_CELL_WAIT_TIMEOUT_MS) {
    delay(LOAD_CELL_READY_POLL_MS);
  }

  ready = scale.is_ready();

  if (!ready) {
    Serial.println("HX711 not ready. Weight readings will be skipped.");
    return false;
  }

  tare(tareSamples);
  Serial.println("HX711 ready.");
  return true;
}

bool LoadCellSensor::isReady() {
  return ready && scale.is_ready();
}

float LoadCellSensor::readWeightG(int samples) {
  if (!isReady()) {
    return NAN;
  }

  return scale.get_units(samples);
}

void LoadCellSensor::tare(int samples) {
  if (!scale.is_ready()) {
    return;
  }

  scale.tare(samples);
}
