#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>
#include "supabase_client.h"

SupabaseClient::SupabaseClient(
  const char* url,
  const char* apiKey,
  const char* deviceCode,
  const char* deviceSecret
) {
  this->url = url;
  this->apiKey = apiKey;
  this->deviceCode = deviceCode;
  this->deviceSecret = deviceSecret;
}

String SupabaseClient::jsonEscape(const char* input) {
  String source = String(input);
  String out = "";

  for (int i = 0; i < source.length(); i++) {
    char c = source.charAt(i);

    if (c == '\"') {
      out += "\\\"";
    } else if (c == '\\') {
      out += "\\\\";
    } else if (c == '\n') {
      out += "\\n";
    } else if (c == '\r') {
      out += "\\r";
    } else if (c == '\t') {
      out += "\\t";
    } else {
      out += c;
    }
  }

  return out;
}

bool SupabaseClient::sendEvent(int seq, const char* eventType,
                               float weightG, float distanceCm,
                               float baselineWeightG, float baselineDistanceCm) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Cannot send event: WiFi not connected.");
    return false;
  }

  WiFiClientSecure client;

  // POC 단계에서는 인증서 검증을 생략한다.
  // 실제 제품화 단계에서는 Supabase 루트 인증서 검증을 적용하는 것이 좋다.
  client.setInsecure();

  HTTPClient http;

  String endpoint = String(url) + "/rest/v1/rpc/ingest_sensor_event";

  if (!http.begin(client, endpoint)) {
    Serial.println("HTTP begin failed.");
    return false;
  }

  http.addHeader("Content-Type", "application/json");
  http.addHeader("apikey", apiKey);
  http.addHeader("Authorization", String("Bearer ") + apiKey);

  auto floatOrNull = [](float v) -> String {
    return isnan(v) ? "null" : String(v, 2);
  };

  String payload = "{";
  payload += "\"p_device_code\":\"" + jsonEscape(deviceCode) + "\",";
  payload += "\"p_device_secret\":\"" + jsonEscape(deviceSecret) + "\",";
  payload += "\"p_seq\":" + String(seq) + ",";
  payload += "\"p_event_type\":\"" + jsonEscape(eventType) + "\",";
  payload += "\"p_schema_version\":1,";
  payload += "\"p_rssi\":" + String(WiFi.RSSI()) + ",";
  payload += "\"p_ip_address\":\"" + WiFi.localIP().toString() + "\",";
  payload += "\"p_weight_g\":" + floatOrNull(weightG) + ",";
  payload += "\"p_distance_cm\":" + floatOrNull(distanceCm) + ",";
  payload += "\"p_baseline_weight_g\":" + floatOrNull(baselineWeightG) + ",";
  payload += "\"p_baseline_distance_cm\":" + floatOrNull(baselineDistanceCm);
  payload += "}";

  Serial.println("POST Supabase RPC:");
  Serial.println(endpoint);
  Serial.println(payload);

  int httpCode = http.POST(payload);
  String response = http.getString();

  http.end();

  Serial.print("HTTP code: ");
  Serial.println(httpCode);

  if (response.length() > 0) {
    Serial.print("Response: ");
    Serial.println(response);
  }

  return httpCode >= 200 && httpCode < 300;
}
