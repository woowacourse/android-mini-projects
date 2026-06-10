param(
  [string]$Fqbn = "esp32:esp32:esp32"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

arduino-cli compile --fqbn $Fqbn $ProjectRoot
