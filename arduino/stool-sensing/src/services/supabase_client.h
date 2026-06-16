#ifndef SUPABASE_CLIENT_H
#define SUPABASE_CLIENT_H

#include <Arduino.h>

class SupabaseClient {
public:
  SupabaseClient(
    const char* url,
    const char* apiKey,
    const char* deviceCode,
    const char* deviceSecret
  );

  bool sendEvent(int seq, const char* eventType,
                 float weightG, float distanceCm,
                 float baselineWeightG, float baselineDistanceCm);

private:
  const char* url;
  const char* apiKey;
  const char* deviceCode;
  const char* deviceSecret;

  String jsonEscape(const char* input);
};

#endif
