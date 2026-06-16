#include <Arduino.h>
#include <math.h>
#include "../config/detection_config.h"
#include "stool_detector.h"
#include "event_types.h"

StoolDetector::StoolDetector() {
  baselineWeightG = NAN;
  baselineDistanceCm = -1.0;
  candidateBaselineWeightG = NAN;
  candidateBaselineDistanceCm = -1.0;
  visitBaselineWeightG = NAN;
  visitBaselineDistanceCm = -1.0;
  postExitWeightG = NAN;
  postExitDistanceCm = -1.0;
  lastResidualDeltaG = NAN;
  lastHeightDeltaCm = NAN;

  state = DETECTOR_IDLE;

  visitStartCandidateAt = 0;
  visitEndCandidateAt = 0;
  visitStartedAt = 0;
  visitEndedAt = 0;
  postExitStartedAt = 0;
  clearStartedAt = 0;
  baselineWeightStableStartedAt = 0;
  baselineDistanceStableStartedAt = 0;
  lastWeightBaselineCalibratedAt = 0;
  lastDistanceBaselineCalibratedAt = 0;
  hasBaselineWeightCandidate = false;
  hasBaselineDistanceCandidate = false;
  weightBaselineRefreshRequested = false;
  distanceBaselineRefreshRequested = false;
}

bool StoolDetector::begin(float baselineWeightG, float baselineDistanceCm) {
  if (!hasValidWeight(baselineWeightG)) {
    Serial.println("Baseline calibration failed.");
    return false;
  }

  unsigned long now = millis();
  setBaselineWeight(baselineWeightG, now);

  if (hasValidDistance(baselineDistanceCm)) {
    setBaselineDistance(baselineDistanceCm, now);
  } else {
    Serial.println("Baseline distance pending.");
  }

  return true;
}

DetectionResult StoolDetector::update(float weightG, float distanceCm) {
  if (!hasValidWeight(weightG)) {
    return noEvent();
  }

  unsigned long now = millis();
  bool hasDistance = hasValidDistance(distanceCm);

  switch (state) {
    case DETECTOR_IDLE:
      updateBaselineCalibration(now, weightG, distanceCm);

      if (!hasDistance || baselineDistanceCm < 0) {
        visitStartCandidateAt = 0;
        break;
      }

      if (isVisitEnterCandidate(weightG)) {
        if (visitStartCandidateAt == 0) {
          visitStartCandidateAt = now;
        }

        if (now - visitStartCandidateAt >= VISIT_START_HOLD_MS) {
          startVisit(now);
        }
      } else {
        visitStartCandidateAt = 0;
      }
      break;

    case DETECTOR_VISIT_ACTIVE:
      if (!hasDistance) {
        break;
      }

      if (isDogAbsent(weightG)) {
        if (visitEndCandidateAt == 0) {
          visitEndCandidateAt = now;
        }

        if (now - visitEndCandidateAt >= EXIT_HOLD_MS) {
          visitEndedAt = now;
          postExitStartedAt = now;
          postExitWeightG = weightG;
          postExitDistanceCm = distanceCm;
          state = DETECTOR_POST_VISIT_CHECK;

          Serial.println("Visit ended. Stabilizing post-exit sensors.");
        }
      } else {
        visitEndCandidateAt = 0;
      }
      break;

    case DETECTOR_POST_VISIT_CHECK:
      if (!hasDistance) {
        break;
      }

      if (isVisitEnterCandidate(weightG)) {
        Serial.println("Dog returned during post-exit check. Back to visit active.");
        state = DETECTOR_VISIT_ACTIVE;
        visitEndCandidateAt = 0;
        postExitStartedAt = 0;
        break;
      }

      postExitWeightG = weightG;
      postExitDistanceCm = distanceCm;

      if (now - postExitStartedAt >= POST_EXIT_STABILIZE_MS) {
        const char* eventType = classifyPostExit(postExitWeightG, postExitDistanceCm);

        Serial.print("Final result: ");
        Serial.println(eventType);

        state = DETECTOR_LOCKED;
        clearStartedAt = 0;
        resetBaselineCandidates();
        requestBaselineRefresh();

        return event(eventType);
      }
      break;

    case DETECTOR_LOCKED:
      if (isDogAbsent(weightG)) {
        updateBaselineCalibration(now, weightG, distanceCm);

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
          postExitStartedAt = 0;
          clearStartedAt = 0;
          resetBaselineCandidates();
        }
      } else {
        clearStartedAt = 0;
        resetBaselineCandidates();
      }
      break;
  }

  return noEvent();
}

bool StoolDetector::hasValidWeight(float weightG) {
  return !isnan(weightG);
}

bool StoolDetector::hasValidDistance(float distanceCm) {
  return distanceCm >= 0;
}

bool StoolDetector::hasValidSample(float weightG, float distanceCm) {
  return hasValidWeight(weightG) && hasValidDistance(distanceCm);
}

bool StoolDetector::isVisitEnterCandidate(float weightG) {
  if (isnan(baselineWeightG)) {
    return false;
  }

  return weightG - baselineWeightG >= VISIT_ENTER_DELTA_G;
}

bool StoolDetector::isDogAbsent(float weightG) {
  float referenceWeightG = isnan(visitBaselineWeightG) ? baselineWeightG : visitBaselineWeightG;
  return weightG - referenceWeightG < DOG_ABSENCE_DELTA_G;
}

bool StoolDetector::isBaselineWeightCandidateStable(float weightG) {
  return hasBaselineWeightCandidate &&
         fabs(weightG - candidateBaselineWeightG) <= BASELINE_WEIGHT_STABLE_DELTA_G;
}

bool StoolDetector::isBaselineDistanceCandidateStable(float distanceCm) {
  return hasBaselineDistanceCandidate &&
         fabs(distanceCm - candidateBaselineDistanceCm) <= BASELINE_DISTANCE_STABLE_DELTA_CM;
}

bool StoolDetector::canUpdateWeightBaseline(unsigned long now, float weightG) {
  return weightBaselineRefreshRequested ||
         isnan(baselineWeightG) ||
         now - lastWeightBaselineCalibratedAt >= CALIBRATION_INTERVAL_MS ||
         weightG < baselineWeightG - BASELINE_WEIGHT_STABLE_DELTA_G;
}

bool StoolDetector::canUpdateDistanceBaseline(unsigned long now) {
  return distanceBaselineRefreshRequested ||
         baselineDistanceCm < 0 ||
         now - lastDistanceBaselineCalibratedAt >= CALIBRATION_INTERVAL_MS;
}

void StoolDetector::updateBaselineCalibration(unsigned long now, float weightG, float distanceCm) {
  if (!isDogAbsent(weightG)) {
    resetBaselineCandidates();
    return;
  }

  updateWeightBaselineCalibration(now, weightG);

  if (hasValidDistance(distanceCm)) {
    updateDistanceBaselineCalibration(now, distanceCm);
  } else {
    resetBaselineDistanceCandidate();
  }
}

void StoolDetector::updateWeightBaselineCalibration(unsigned long now, float weightG) {
  if (!canUpdateWeightBaseline(now, weightG)) {
    resetBaselineWeightCandidate();
    return;
  }

  if (!isBaselineWeightCandidateStable(weightG)) {
    baselineWeightStableStartedAt = now;
    candidateBaselineWeightG = weightG;
    hasBaselineWeightCandidate = true;
    return;
  }

  if (now - baselineWeightStableStartedAt >= BASELINE_STABLE_HOLD_MS) {
    setBaselineWeight(weightG, now);
    weightBaselineRefreshRequested = false;
  }
}

void StoolDetector::updateDistanceBaselineCalibration(unsigned long now, float distanceCm) {
  if (!canUpdateDistanceBaseline(now)) {
    resetBaselineDistanceCandidate();
    return;
  }

  if (!isBaselineDistanceCandidateStable(distanceCm)) {
    baselineDistanceStableStartedAt = now;
    candidateBaselineDistanceCm = distanceCm;
    hasBaselineDistanceCandidate = true;
    return;
  }

  if (now - baselineDistanceStableStartedAt >= BASELINE_STABLE_HOLD_MS) {
    setBaselineDistance(distanceCm, now);
    distanceBaselineRefreshRequested = false;
  }
}

void StoolDetector::requestBaselineRefresh() {
  weightBaselineRefreshRequested = true;
  distanceBaselineRefreshRequested = true;
}

void StoolDetector::resetBaselineWeightCandidate() {
  candidateBaselineWeightG = NAN;
  baselineWeightStableStartedAt = 0;
  hasBaselineWeightCandidate = false;
}

void StoolDetector::resetBaselineDistanceCandidate() {
  candidateBaselineDistanceCm = -1.0;
  baselineDistanceStableStartedAt = 0;
  hasBaselineDistanceCandidate = false;
}

void StoolDetector::resetBaselineCandidates() {
  resetBaselineWeightCandidate();
  resetBaselineDistanceCandidate();
}

void StoolDetector::setBaselineWeight(float weightG, unsigned long now) {
  baselineWeightG = weightG;
  lastWeightBaselineCalibratedAt = now;
  resetBaselineWeightCandidate();

  Serial.print("Baseline weight: ");
  Serial.print(baselineWeightG, 1);
  Serial.println(" g");
}

void StoolDetector::setBaselineDistance(float distanceCm, unsigned long now) {
  baselineDistanceCm = distanceCm;
  lastDistanceBaselineCalibratedAt = now;
  resetBaselineDistanceCandidate();

  Serial.print("Baseline distance: ");
  Serial.print(baselineDistanceCm, 1);
  Serial.println(" cm");
}

void StoolDetector::startVisit(unsigned long now) {
  state = DETECTOR_VISIT_ACTIVE;
  visitStartedAt = now;
  visitEndCandidateAt = 0;
  postExitStartedAt = 0;
  resetBaselineCandidates();

  visitBaselineWeightG = baselineWeightG;
  visitBaselineDistanceCm = baselineDistanceCm;

  Serial.print("Visit started. Snapshot weight: ");
  Serial.print(visitBaselineWeightG, 1);
  Serial.print(" g | Snapshot distance: ");
  Serial.print(visitBaselineDistanceCm, 1);
  Serial.println(" cm");
}

const char* StoolDetector::classifyPostExit(float weightG, float distanceCm) {
  lastResidualDeltaG = weightG - visitBaselineWeightG;
  lastHeightDeltaCm = visitBaselineDistanceCm - distanceCm;

  Serial.print("Residual delta: ");
  Serial.print(lastResidualDeltaG, 1);
  Serial.print(" g | Height delta: ");
  Serial.print(lastHeightDeltaCm, 1);
  Serial.println(" cm");

  if (lastResidualDeltaG < VISIT_ONLY_DELTA_G) {
    return EVENT_VISIT_DETECTED;
  }

  if (lastHeightDeltaCm >= FECES_HEIGHT_DELTA_CM) {
    return EVENT_STOOL_DETECTED;
  }

  if (fabs(lastHeightDeltaCm) <= URINE_DISTANCE_TOLERANCE_CM) {
    return EVENT_URINE_DETECTED;
  }

  // 잔여 무게는 있지만 대변 높이 기준에는 못 미치는 애매한 경우는 소변으로 본다.
  return EVENT_URINE_DETECTED;
}

float StoolDetector::getBaselineWeightG() {
  return baselineWeightG;
}

float StoolDetector::getBaselineDistanceCm() {
  return baselineDistanceCm;
}

float StoolDetector::getVisitBaselineWeightG() {
  return visitBaselineWeightG;
}

float StoolDetector::getVisitBaselineDistanceCm() {
  return visitBaselineDistanceCm;
}

float StoolDetector::getLastResidualDeltaG() {
  return lastResidualDeltaG;
}

float StoolDetector::getLastHeightDeltaCm() {
  return lastHeightDeltaCm;
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
