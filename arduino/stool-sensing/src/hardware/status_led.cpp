#include <Arduino.h>
#include "status_led.h"

StatusLed::StatusLed(uint8_t pin) {
  this->pin = pin;
  ledOn = false;
  lastToggleAt = 0;
}

void StatusLed::begin() {
  pinMode(pin, OUTPUT);
  setOn(false);
}

void StatusLed::setOn(bool on) {
  ledOn = on;
  digitalWrite(pin, ledOn ? HIGH : LOW);
}

void StatusLed::updateBlink(unsigned long now, unsigned long intervalMs) {
  if (now - lastToggleAt < intervalMs) {
    return;
  }

  lastToggleAt = now;
  setOn(!ledOn);
}
