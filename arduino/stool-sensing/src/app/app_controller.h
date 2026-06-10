#ifndef APP_CONTROLLER_H
#define APP_CONTROLLER_H

#include <Arduino.h>
#include "../drivers/distance_sampler.h"
#include "../drivers/load_cell_sensor.h"
#include "../drivers/ultrasonic_sensor.h"
#include "../domain/stool_detector.h"
#include "../hardware/boot_button.h"
#include "../hardware/status_led.h"
#include "../services/event_sender.h"
#include "../services/provisioning_manager.h"
#include "../services/sequence_store.h"
#include "../services/supabase_client.h"

class AppController {
public:
  AppController();

  void begin();
  void update();

private:
  UltrasonicSensor ultrasonicSensor;
  DistanceSampler distanceSampler;
  LoadCellSensor loadCellSensor;
  StoolDetector stoolDetector;
  ProvisioningManager provisioningManager;
  SupabaseClient supabaseClient;
  SequenceStore sequenceStore;
  EventSender eventSender;
  StatusLed statusLed;
  BootButton bootButton;

  bool isDetectorReady;
  bool isCalibrationComplete;
  bool isLoadCellReady;
  bool isDeviceReadySent;
  float lastDistanceCm;
  float lastWeightGram;
  unsigned long lastDebugPrintAt;

  void beginSerial();
  void beginHardware();
  void beginServices();
  void beginNetwork();

  void updateProvisioning(unsigned long now);
  void updateStatusLed(unsigned long now);
  void updateBootButton(unsigned long now);
  void updateDeviceReadyEvent();
  void updateSensors(unsigned long now);
  void updateEventSending(unsigned long now);
  void updateDebugPrint(unsigned long now);

  void handleDistanceSample(float distanceCm);
  void handleSentEvent();
  void printRuntimeDebug();
};

#endif
