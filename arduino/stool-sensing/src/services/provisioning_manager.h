#ifndef PROVISIONING_MANAGER_H
#define PROVISIONING_MANAGER_H

#include <Arduino.h>
#include <WebServer.h>
#include <Preferences.h>

class ProvisioningManager {
public:
  ProvisioningManager();

  void begin();
  void update(unsigned long now);

  bool hasSavedCredentials();
  void startSavedWiFiConnection(unsigned long timeoutMs);
  bool hasSavedConnectionFailed();

  void startPortal();
  void handleClient();

  bool isPortalActive();
  bool isConnected();

  void clearCredentials();

  String getSetupSsid();

private:
  WebServer server;
  Preferences prefs;

  bool portalActive;
  bool routesConfigured;
  bool savedConnectionActive;
  bool savedConnectionFailed;
  bool savedConnectionLogged;
  unsigned long savedConnectionStartedAt;
  unsigned long savedConnectionTimeoutMs;

  String setupSsid;

  static const char* SETUP_PASSWORD;

  String buildSetupSsid();
  String jsonEscape(const String& input);

  void configureRoutes();

  void sendJson(int code, const String& body);

  void handleScan();
  void handleProvision();
  void handleStatus();
  void handleNotFound();
};

#endif
