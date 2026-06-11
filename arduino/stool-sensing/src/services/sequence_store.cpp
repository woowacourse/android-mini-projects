#include <Arduino.h>
#include <Preferences.h>
#include "sequence_store.h"

Preferences sequencePrefs;

void SequenceStore::begin() {
  sequencePrefs.begin("seq-store", false);
  lastCommittedSeq = sequencePrefs.getInt("lastSeq", 0);

  Serial.print("Last committed seq: ");
  Serial.println(lastCommittedSeq);
}

int SequenceStore::nextSeq() {
  return lastCommittedSeq + 1;
}

void SequenceStore::commitSeq(int seq) {
  lastCommittedSeq = seq;
  sequencePrefs.putInt("lastSeq", lastCommittedSeq);

  Serial.print("Committed seq: ");
  Serial.println(lastCommittedSeq);
}

int SequenceStore::getLastCommittedSeq() {
  return lastCommittedSeq;
}
