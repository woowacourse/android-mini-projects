#ifndef STATUS_LED_H
#define STATUS_LED_H

#include <Arduino.h>

class StatusLed {
public:
  explicit StatusLed(uint8_t pin);

  void begin();
  void setOn(bool on);
  void updateBlink(unsigned long now, unsigned long intervalMs);

private:
  uint8_t pin;
  bool ledOn;
  unsigned long lastToggleAt;
};

#endif
