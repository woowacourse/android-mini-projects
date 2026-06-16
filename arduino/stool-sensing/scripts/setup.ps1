param(
  [string]$Core = "esp32:esp32",
  [string]$Library = "HX711"
)

$ErrorActionPreference = "Stop"

arduino-cli core update-index
arduino-cli core install $Core
arduino-cli lib install $Library

Write-Host "Arduino CLI setup complete."
