#ifndef EVENT_SENDER_H
#define EVENT_SENDER_H

#include "../app/pending_event.h"
#include "sequence_store.h"
#include "supabase_client.h"

class EventSender {
public:
  EventSender(SequenceStore& sequenceStore, SupabaseClient& supabaseClient);

  bool hasPendingEvent();
  void queueEvent(const char* eventType);
  bool update(unsigned long now, bool canSend);
  const String& getPendingEventType();
  const String& getLastSentEventType();

private:
  SequenceStore& sequenceStore;
  SupabaseClient& supabaseClient;
  PendingEvent pendingEvent;
  String lastSentEventType;
};

#endif
