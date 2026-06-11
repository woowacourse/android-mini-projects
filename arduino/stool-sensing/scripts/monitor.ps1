param(
  [Parameter(Mandatory = $true)]
  [string]$Port,
  [int]$Baud = 115200
)

$ErrorActionPreference = "Stop"

arduino-cli monitor --port $Port --config baudrate=$Baud
