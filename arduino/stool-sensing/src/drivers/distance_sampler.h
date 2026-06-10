#ifndef DISTANCE_SAMPLER_H
#define DISTANCE_SAMPLER_H

#include <Arduino.h>
#include "ultrasonic_sensor.h"

class DistanceSampler {
public:
  DistanceSampler(UltrasonicSensor& sensor, int sampleCount);

  void begin();
  void update(unsigned long now);
  bool hasNewAverage();
  float readAverageCm();

private:
  UltrasonicSensor& sensor;
  int sampleCount;
  int attemptedCount;
  int validCount;
  float sumCm;
  float lastAverageCm;
  bool averageReady;
  unsigned long lastSampleAt;
};

#endif
