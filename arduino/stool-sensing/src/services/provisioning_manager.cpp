#include <Arduino.h>
#include <WiFi.h>
#include "provisioning_manager.h"

const char* ProvisioningManager::SETUP_PASSWORD = "dognal1234";

ProvisioningManager::ProvisioningManager()
  : server(80) {
  portalActive = false;
  routesConfigured = false;
  savedConnectionActive = false;
  savedConnectionFailed = false;
  savedConnectionLogged = false;
  savedConnectionStartedAt = 0;
  savedConnectionTimeoutMs = 0;
  setupSsid = "";
}

void ProvisioningManager::begin() {
  prefs.begin("wifi-creds", false);
  setupSsid = buildSetupSsid();

  Serial.print("Setup SSID: ");
  Serial.println(setupSsid);
}

void ProvisioningManager::update(unsigned long now) {
  handleClient();

  if (!savedConnectionActive) {
    return;
  }

  if (WiFi.status() == WL_CONNECTED) {
    if (!savedConnectionLogged) {
      Serial.print("WiFi connected. IP: ");
      Serial.println(WiFi.localIP());
      savedConnectionLogged = true;
    }

    savedConnectionActive = false;
    savedConnectionFailed = false;
    return;
  }

  if (now - savedConnectionStartedAt >= savedConnectionTimeoutMs) {
    Serial.println("Saved WiFi connection failed.");
    savedConnectionActive = false;
    savedConnectionFailed = true;
  }
}

bool ProvisioningManager::hasSavedCredentials() {
  String ssid = prefs.getString("ssid", "");
  return ssid.length() > 0;
}

void ProvisioningManager::startSavedWiFiConnection(unsigned long timeoutMs) {
  String ssid = prefs.getString("ssid", "");
  String password = prefs.getString("password", "");

  if (ssid.length() == 0) {
    Serial.println("No saved WiFi credentials.");
    savedConnectionFailed = true;
    return;
  }

  Serial.print("Connecting to saved WiFi: ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.setSleep(false);
  WiFi.begin(ssid.c_str(), password.c_str());

  savedConnectionActive = true;
  savedConnectionFailed = false;
  savedConnectionLogged = false;
  savedConnectionStartedAt = millis();
  savedConnectionTimeoutMs = timeoutMs;
}

bool ProvisioningManager::hasSavedConnectionFailed() {
  return savedConnectionFailed;
}

void ProvisioningManager::startPortal() {
  if (portalActive) {
    return;
  }

  Serial.println("Starting SoftAP provisioning portal...");

  WiFi.mode(WIFI_AP_STA);

  IPAddress apIp(192, 168, 4, 1);
  IPAddress gateway(192, 168, 4, 1);
  IPAddress subnet(255, 255, 255, 0);

  WiFi.softAPConfig(apIp, gateway, subnet);
  WiFi.softAP(setupSsid.c_str(), SETUP_PASSWORD);

  IPAddress currentApIp = WiFi.softAPIP();

  Serial.print("SoftAP SSID: ");
  Serial.println(setupSsid);
  Serial.print("SoftAP password: ");
  Serial.println(SETUP_PASSWORD);
  Serial.print("SoftAP IP: ");
  Serial.println(currentApIp);

  configureRoutes();

  server.begin();

  portalActive = true;

  Serial.println("Provisioning portal started.");
  Serial.println("Use http://192.168.4.1");
}

void ProvisioningManager::handleClient() {
  if (portalActive) {
    server.handleClient();
  }
}

bool ProvisioningManager::isPortalActive() {
  return portalActive;
}

bool ProvisioningManager::isConnected() {
  return WiFi.status() == WL_CONNECTED;
}

void ProvisioningManager::clearCredentials() {
  Serial.println("Clearing saved WiFi credentials...");

  prefs.remove("ssid");
  prefs.remove("password");

  WiFi.disconnect(false, true);

  Serial.println("WiFi credentials cleared.");
}

String ProvisioningManager::getSetupSsid() {
  return setupSsid;
}

String ProvisioningManager::buildSetupSsid() {
  uint64_t chipId = ESP.getEfuseMac();
  uint16_t suffixValue = (uint16_t)(chipId & 0xFFFF);

  char suffix[8];
  snprintf(suffix, sizeof(suffix), "%04X", suffixValue);

  String ssid = "DOGNAL-Setup-";
  ssid += suffix;

  return ssid;
}

String ProvisioningManager::jsonEscape(const String& input) {
  String out = "";

  for (int i = 0; i < input.length(); i++) {
    char c = input.charAt(i);

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

void ProvisioningManager::configureRoutes() {
  if (routesConfigured) {
    return;
  }

  server.on("/scan", HTTP_GET, [this]() {
    handleScan();
  });

  server.on("/provision", HTTP_POST, [this]() {
    handleProvision();
  });

  server.on("/status", HTTP_GET, [this]() {
    handleStatus();
  });

  server.onNotFound([this]() {
    handleNotFound();
  });

  routesConfigured = true;
}

void ProvisioningManager::sendJson(int code, const String& body) {
  server.sendHeader("Access-Control-Allow-Origin", "*");
  server.sendHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
  server.sendHeader("Access-Control-Allow-Headers", "Content-Type");
  server.send(code, "application/json", body);
}

void ProvisioningManager::handleScan() {
  Serial.println("GET /scan");

  int n = WiFi.scanNetworks(false, true);

  String json = "[";
  for (int i = 0; i < n; i++) {
    if (i > 0) {
      json += ",";
    }

    bool secure = WiFi.encryptionType(i) != WIFI_AUTH_OPEN;

    json += "{";
    json += "\"ssid\":\"" + jsonEscape(WiFi.SSID(i)) + "\",";
    json += "\"rssi\":" + String(WiFi.RSSI(i)) + ",";
    json += "\"secure\":" + String(secure ? "true" : "false");
    json += "}";
  }
  json += "]";

  WiFi.scanDelete();

  sendJson(200, json);
}

void ProvisioningManager::handleProvision() {
  Serial.println("POST /provision");

  String ssid = server.arg("ssid");
  String password = server.arg("password");

  if (ssid.length() == 0) {
    sendJson(400, "{\"ok\":false,\"error\":\"SSID_REQUIRED\"}");
    return;
  }

  Serial.print("Received SSID: ");
  Serial.println(ssid);

  prefs.putString("ssid", ssid);
  prefs.putString("password", password);

  WiFi.mode(WIFI_AP_STA);
  WiFi.setSleep(false);
  WiFi.begin(ssid.c_str(), password.c_str());

  sendJson(200, "{\"ok\":true,\"message\":\"CONNECTING\"}");
}

void ProvisioningManager::handleStatus() {
  bool connected = WiFi.status() == WL_CONNECTED;

  String json = "{";
  json += "\"connected\":" + String(connected ? "true" : "false") + ",";
  json += "\"setupSsid\":\"" + jsonEscape(setupSsid) + "\",";

  if (connected) {
    json += "\"ip\":\"" + WiFi.localIP().toString() + "\",";
    json += "\"rssi\":" + String(WiFi.RSSI());
  } else {
    json += "\"ip\":null,";
    json += "\"rssi\":null";
  }

  json += "}";

  sendJson(200, json);
}

void ProvisioningManager::handleNotFound() {
  sendJson(404, "{\"ok\":false,\"error\":\"NOT_FOUND\"}");
}
