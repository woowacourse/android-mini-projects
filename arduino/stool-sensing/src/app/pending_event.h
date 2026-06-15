#ifndef PENDING_EVENT_H
#define PENDING_EVENT_H

#include <Arduino.h>

struct PendingEvent {
  bool active;
  int seq;
  String eventType;
  unsigned long lastAttemptAt;
  float weightG;
  float distanceCm;
  float baselineWeightG;
  float baselineDistanceCm;
};

#endif
