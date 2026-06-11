param(
  [Parameter(Mandatory = $true)]
  [string]$Port,
  [string]$Fqbn = "esp32:esp32:esp32"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

arduino-cli compile --fqbn $Fqbn --upload --port $Port $ProjectRoot
