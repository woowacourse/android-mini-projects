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

  bool begin(float baselineDistanceCm);
  DetectionResult update(float distanceCm);

  float getBaselineCm();
  const char* getStateName();

private:
  float baselineCm;

  DetectorState state;

  unsigned long visitStartCandidateAt;
  unsigned long visitEndCandidateAt;
  unsigned long visitStartedAt;
  unsigned long visitEndedAt;
  unsigned long stoolCandidateStartedAt;
  unsigned long clearStartedAt;

  bool isDogPresent(float distanceCm);
  bool isStoolCandidate(float distanceCm);
  bool isPadClear(float distanceCm);

  DetectionResult noEvent();
  DetectionResult event(const char* eventType);
};

#endif
