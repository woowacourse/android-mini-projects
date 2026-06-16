#include <Arduino.h>
#include "boot_button.h"

BootButton::BootButton(uint8_t pin) {
  this->pin = pin;
  pressedAt = 0;
  longPressTriggered = false;
}

void BootButton::begin() {
  pinMode(pin, INPUT_PULLUP);
}

bool BootButton::updateLongPress(unsigned long now, unsigned long holdMs) {
  bool pressed = digitalRead(pin) == LOW;

  if (!pressed) {
    pressedAt = 0;
    longPressTriggered = false;
    return false;
  }

  if (pressedAt == 0) {
    pressedAt = now;
  }

  if (!longPressTriggered && now - pressedAt >= holdMs) {
    longPressTriggered = true;
    return true;
  }

  return false;
}
