#include <Arduino.h>
#include "src/app/app_controller.h"

AppController app;

void setup() {
  app.begin();
}

void loop() {
  app.update();
}
