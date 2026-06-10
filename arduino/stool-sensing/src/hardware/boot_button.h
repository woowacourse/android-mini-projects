#ifndef BOOT_BUTTON_H
#define BOOT_BUTTON_H

#include <Arduino.h>

class BootButton {
public:
  explicit BootButton(uint8_t pin);

  void begin();
  bool updateLongPress(unsigned long now, unsigned long holdMs);

private:
  uint8_t pin;
  unsigned long pressedAt;
  bool longPressTriggered;
};

#endif
