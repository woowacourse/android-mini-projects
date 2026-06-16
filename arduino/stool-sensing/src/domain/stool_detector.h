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
  float candidateBaselineWeightG;
  float candidateBaselineDistanceCm;
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
  unsigned long baselineWeightStableStartedAt;
  unsigned long baselineDistanceStableStartedAt;
  unsigned long lastWeightBaselineCalibratedAt;
  unsigned long lastDistanceBaselineCalibratedAt;
  bool hasBaselineWeightCandidate;
  bool hasBaselineDistanceCandidate;
  bool weightBaselineRefreshRequested;
  bool distanceBaselineRefreshRequested;

  bool hasValidWeight(float weightG);
  bool hasValidDistance(float distanceCm);
  bool hasValidSample(float weightG, float distanceCm);
  bool isVisitEnterCandidate(float weightG);
  bool isDogAbsent(float weightG);
  bool isBaselineWeightCandidateStable(float weightG);
  bool isBaselineDistanceCandidateStable(float distanceCm);
  bool canUpdateWeightBaseline(unsigned long now, float weightG);
  bool canUpdateDistanceBaseline(unsigned long now);
  void updateBaselineCalibration(unsigned long now, float weightG, float distanceCm);
  void updateWeightBaselineCalibration(unsigned long now, float weightG);
  void updateDistanceBaselineCalibration(unsigned long now, float distanceCm);
  void requestBaselineRefresh();
  void resetBaselineWeightCandidate();
  void resetBaselineDistanceCandidate();
  void resetBaselineCandidates();
  void setBaselineWeight(float weightG, unsigned long now);
  void setBaselineDistance(float distanceCm, unsigned long now);
  void startVisit(unsigned long now);
  const char* classifyPostExit(float weightG, float distanceCm);

  DetectionResult noEvent();
  DetectionResult event(const char* eventType);
};

#endif
