#ifndef SEQUENCE_STORE_H
#define SEQUENCE_STORE_H

class SequenceStore {
public:
  void begin();
  int nextSeq();
  void commitSeq(int seq);
  int getLastCommittedSeq();

private:
  int lastCommittedSeq;
};

#endif
