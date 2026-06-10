#include <Arduino.h>
#include "../config/detection_config.h"
#include "../config/timing.h"
#include "distance_sampler.h"

DistanceSampler::DistanceSampler(UltrasonicSensor& sensor, int sampleCount)
  : sensor(sensor) {
  this->sampleCount = sampleCount;
  attemptedCount = 0;
  validCount = 0;
  sumCm = 0.0;
  lastAverageCm = -1.0;
  averageReady = false;
  lastSampleAt = 0;
}

void DistanceSampler::begin() {
  attemptedCount = 0;
  validCount = 0;
  sumCm = 0.0;
  lastAverageCm = -1.0;
  averageReady = false;
  lastSampleAt = 0;
}

void DistanceSampler::update(unsigned long now) {
  if (lastSampleAt != 0 && now - lastSampleAt < SENSOR_SAMPLE_INTERVAL_MS) {
    return;
  }

  lastSampleAt = now;

  float distanceCm = sensor.readCm();
  attemptedCount++;

  if (distanceCm >= ULTRASONIC_MIN_VALID_DISTANCE_CM &&
      distanceCm <= ULTRASONIC_PAD_MAX_DISTANCE_CM) {
    sumCm += distanceCm;
    validCount++;
  }

  if (attemptedCount >= sampleCount) {
    lastAverageCm = validCount > 0 ? sumCm / validCount : -1.0;
    averageReady = true;
    sumCm = 0.0;
    attemptedCount = 0;
    validCount = 0;
  }
}

bool DistanceSampler::hasNewAverage() {
  return averageReady;
}

float DistanceSampler::readAverageCm() {
  averageReady = false;
  return lastAverageCm;
}
