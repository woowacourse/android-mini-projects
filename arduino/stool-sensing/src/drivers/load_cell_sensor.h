#ifndef LOAD_CELL_SENSOR_H
#define LOAD_CELL_SENSOR_H

#include <Arduino.h>
#include <HX711.h>

class LoadCellSensor {
public:
  LoadCellSensor(uint8_t doutPin, uint8_t sckPin);

  bool begin(float calibrationFactor, int tareSamples);
  bool isReady();
  float readWeightG(int samples);
  void tare(int samples);

private:
  HX711 scale;
  uint8_t doutPin;
  uint8_t sckPin;
  bool ready;
};

#endif
