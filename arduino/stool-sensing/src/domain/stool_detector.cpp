#include <Arduino.h>
#include <math.h>
#include "../config/detection_config.h"
#include "stool_detector.h"
#include "event_types.h"

StoolDetector::StoolDetector() {
  baselineCm = -1.0;
  state = DETECTOR_IDLE;

  visitStartCandidateAt = 0;
  visitEndCandidateAt = 0;
  visitStartedAt = 0;
  visitEndedAt = 0;
  stoolCandidateStartedAt = 0;
  clearStartedAt = 0;
}

bool StoolDetector::begin(float baselineDistanceCm) {
  if (baselineDistanceCm < 0) {
    Serial.println("Baseline calibration failed.");
    return false;
  }

  baselineCm = baselineDistanceCm;

  Serial.print("Baseline distance: ");
  Serial.print(baselineCm, 1);
  Serial.println(" cm");

  return true;
}

DetectionResult StoolDetector::update(float distanceCm) {
  if (distanceCm < 0 || baselineCm < 0) {
    return noEvent();
  }

  unsigned long now = millis();

  bool dogPresent = isDogPresent(distanceCm);
  bool stoolCandidate = isStoolCandidate(distanceCm);
  bool padClear = isPadClear(distanceCm);

  switch (state) {
    case DETECTOR_IDLE:
      if (dogPresent) {
        if (visitStartCandidateAt == 0) {
          visitStartCandidateAt = now;
        }

        if (now - visitStartCandidateAt >= VISIT_START_HOLD_MS) {
          state = DETECTOR_VISIT_ACTIVE;
          visitStartedAt = now;
          visitEndCandidateAt = 0;
          stoolCandidateStartedAt = 0;
          clearStartedAt = 0;

          Serial.println("Visit started.");
        }
      } else {
        visitStartCandidateAt = 0;
      }
      break;

    case DETECTOR_VISIT_ACTIVE:
      if (!dogPresent) {
        if (visitEndCandidateAt == 0) {
          visitEndCandidateAt = now;
        }

        if (now - visitEndCandidateAt >= VISIT_END_HOLD_MS) {
          unsigned long visitDuration = now - visitStartedAt;

          if (visitDuration < MIN_VISIT_MS) {
            Serial.println("Visit too short. Ignored.");
            state = DETECTOR_IDLE;
            visitStartCandidateAt = 0;
            visitEndCandidateAt = 0;
            visitStartedAt = 0;
            break;
          }

          visitEndedAt = now;
          stoolCandidateStartedAt = 0;
          state = DETECTOR_POST_VISIT_CHECK;

          Serial.println("Visit ended. Start post-visit check.");
        }
      } else {
        visitEndCandidateAt = 0;
      }
      break;

    case DETECTOR_POST_VISIT_CHECK:
      if (dogPresent) {
        Serial.println("Dog returned. Back to visit active.");
        state = DETECTOR_VISIT_ACTIVE;
        visitEndCandidateAt = 0;
        stoolCandidateStartedAt = 0;
        break;
      }

      if (stoolCandidate) {
        if (stoolCandidateStartedAt == 0) {
          stoolCandidateStartedAt = now;
          Serial.println("Stool candidate started.");
        }

        if (now - stoolCandidateStartedAt >= STOOL_HOLD_MS) {
          Serial.println("Final result: STOOL_DETECTED");

          state = DETECTOR_LOCKED;
          clearStartedAt = 0;

          return event(EVENT_STOOL_DETECTED);
        }
      } else {
        stoolCandidateStartedAt = 0;
      }

      if (now - visitEndedAt >= POST_VISIT_DECIDE_MS) {
        Serial.println("Final result: VISIT_DETECTED");

        state = DETECTOR_LOCKED;
        clearStartedAt = 0;

        return event(EVENT_VISIT_DETECTED);
      }
      break;

    case DETECTOR_LOCKED:
      if (padClear) {
        if (clearStartedAt == 0) {
          clearStartedAt = now;
          Serial.println("Pad clear timer started.");
        }

        if (now - clearStartedAt >= CLEAR_RESET_MS) {
          Serial.println("Detector reset. Ready for next event.");

          state = DETECTOR_IDLE;
          visitStartCandidateAt = 0;
          visitEndCandidateAt = 0;
          visitStartedAt = 0;
          visitEndedAt = 0;
          stoolCandidateStartedAt = 0;
          clearStartedAt = 0;
        }
      } else {
        clearStartedAt = 0;
      }
      break;
  }

  return noEvent();
}

bool StoolDetector::isDogPresent(float distanceCm) {
  float delta = baselineCm - distanceCm;

  return distanceCm <= DOG_PRESENT_ABSOLUTE_CM ||
         delta >= DOG_BODY_DELTA_CM;
}

bool StoolDetector::isStoolCandidate(float distanceCm) {
  float delta = baselineCm - distanceCm;

  return distanceCm > DOG_PRESENT_ABSOLUTE_CM &&
         delta >= STOOL_MIN_DELTA_CM &&
         delta <= STOOL_MAX_DELTA_CM;
}

bool StoolDetector::isPadClear(float distanceCm) {
  float delta = fabs(baselineCm - distanceCm);
  return delta <= PAD_CLEAR_DELTA_CM;
}

float StoolDetector::getBaselineCm() {
  return baselineCm;
}

const char* StoolDetector::getStateName() {
  switch (state) {
    case DETECTOR_IDLE:
      return "IDLE";
    case DETECTOR_VISIT_ACTIVE:
      return "VISIT_ACTIVE";
    case DETECTOR_POST_VISIT_CHECK:
      return "POST_VISIT_CHECK";
    case DETECTOR_LOCKED:
      return "LOCKED";
    default:
      return "UNKNOWN";
  }
}

DetectionResult StoolDetector::noEvent() {
  DetectionResult result;
  result.hasEvent = false;
  result.eventType = "";
  return result;
}

DetectionResult StoolDetector::event(const char* eventType) {
  DetectionResult result;
  result.hasEvent = true;
  result.eventType = eventType;
  return result;
}
