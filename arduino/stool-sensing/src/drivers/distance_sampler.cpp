#include <Arduino.h>
#include "../config/timing.h"
#include "distance_sampler.h"

DistanceSampler::DistanceSampler(UltrasonicSensor& sensor, int sampleCount)
  : sensor(sensor) {
  this->sampleCount = sampleCount;
  collectedCount = 0;
  sumCm = 0.0;
  lastAverageCm = -1.0;
  averageReady = false;
  lastSampleAt = 0;
}

void DistanceSampler::begin() {
  collectedCount = 0;
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
  if (distanceCm >= 2.0 && distanceCm <= 250.0) {
    sumCm += distanceCm;
    collectedCount++;
  }

  if (collectedCount >= sampleCount) {
    lastAverageCm = sumCm / collectedCount;
    averageReady = true;
    sumCm = 0.0;
    collectedCount = 0;
  }
}

bool DistanceSampler::hasNewAverage() {
  return averageReady;
}

float DistanceSampler::readAverageCm() {
  averageReady = false;
  return lastAverageCm;
}
