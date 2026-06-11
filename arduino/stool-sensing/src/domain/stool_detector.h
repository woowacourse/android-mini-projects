#ifndef STOOL_DETECTOR_H
#define STOOL_DETECTOR_H

struct DetectionResult {
  bool hasEvent;
  const char* eventType;
};

enum DetectorState {
  DETECTOR_IDLE,
  DETECTOR_VISIT_ACTIVE,
  DETECTOR_POST_VISIT_CHECK,
  DETECTOR_LOCKED
};

class StoolDetector {
public:
  StoolDetector();

  bool begin(float baselineWeightG, float baselineDistanceCm);
  DetectionResult update(float weightG, float distanceCm);

  float getBaselineWeightG();
  float getBaselineDistanceCm();
  float getVisitBaselineWeightG();
  float getVisitBaselineDistanceCm();
  float getLastResidualDeltaG();
  float getLastHeightDeltaCm();
  const char* getStateName();

private:
  float baselineWeightG;
  float baselineDistanceCm;
  float visitBaselineWeightG;
  float visitBaselineDistanceCm;
  float postExitWeightG;
  float postExitDistanceCm;
  float lastResidualDeltaG;
  float lastHeightDeltaCm;

  DetectorState state;

  unsigned long visitStartCandidateAt;
  unsigned long visitEndCandidateAt;
  unsigned long visitStartedAt;
  unsigned long visitEndedAt;
  unsigned long postExitStartedAt;
  unsigned long clearStartedAt;
  unsigned long baselineStableStartedAt;
  unsigned long lastBaselineCalibratedAt;
  bool baselineRefreshRequested;

  bool hasValidSample(float weightG, float distanceCm);
  bool isVisitEnterCandidate(float weightG);
  bool isDogAbsent(float weightG);
  bool isBaselineStable(float weightG, float distanceCm);
  bool canUpdateBaseline(unsigned long now);
  void updateBaselineCalibration(unsigned long now, float weightG, float distanceCm);
  void setBaseline(float weightG, float distanceCm, unsigned long now);
  void startVisit(unsigned long now);
  const char* classifyPostExit(float weightG, float distanceCm);

  DetectionResult noEvent();
  DetectionResult event(const char* eventType);
};

#endif
