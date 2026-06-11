#include <Arduino.h>
#include "../config/timing.h"
#include "event_sender.h"

EventSender::EventSender(SequenceStore& sequenceStore, SupabaseClient& supabaseClient)
  : sequenceStore(sequenceStore), supabaseClient(supabaseClient) {
  pendingEvent.active = false;
  pendingEvent.seq = 0;
  pendingEvent.eventType = "";
  pendingEvent.lastAttemptAt = 0;
  lastSentEventType = "";
}

bool EventSender::hasPendingEvent() {
  return pendingEvent.active;
}

void EventSender::queueEvent(const char* eventType) {
  if (pendingEvent.active) {
    Serial.print("Cannot queue event. Pending event exists: ");
    Serial.println(pendingEvent.eventType);
    return;
  }

  pendingEvent.active = true;
  pendingEvent.seq = sequenceStore.nextSeq();
  pendingEvent.eventType = String(eventType);
  pendingEvent.lastAttemptAt = 0;

  Serial.print("Queued event: ");
  Serial.print(pendingEvent.eventType);
  Serial.print(" seq=");
  Serial.println(pendingEvent.seq);
}

bool EventSender::update(unsigned long now, bool canSend) {
  if (!pendingEvent.active || !canSend) {
    return false;
  }

  if (pendingEvent.lastAttemptAt != 0 &&
      now - pendingEvent.lastAttemptAt < SEND_RETRY_MS) {
    return false;
  }

  pendingEvent.lastAttemptAt = now;

  Serial.print("Sending pending event: ");
  Serial.print(pendingEvent.eventType);
  Serial.print(" seq=");
  Serial.println(pendingEvent.seq);

  bool success = supabaseClient.sendEvent(
    pendingEvent.seq,
    pendingEvent.eventType.c_str()
  );

  if (!success) {
    Serial.println("Event send failed. Will retry.");
    return false;
  }

  sequenceStore.commitSeq(pendingEvent.seq);
  lastSentEventType = pendingEvent.eventType;

  Serial.print("Event sent successfully: ");
  Serial.println(pendingEvent.eventType);

  pendingEvent.active = false;
  pendingEvent.seq = 0;
  pendingEvent.eventType = "";
  pendingEvent.lastAttemptAt = 0;

  return true;
}

const String& EventSender::getPendingEventType() {
  return pendingEvent.eventType;
}

const String& EventSender::getLastSentEventType() {
  return lastSentEventType;
}
