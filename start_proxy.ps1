$ErrorActionPreference = 'Stop'

$port = 8444
if ($env:PORT) {
    $port = [int]$env:PORT
}

$localAppData = $env:LOCALAPPDATA
if (-not $localAppData) {
    $localAppData = Join-Path $env:USERPROFILE 'AppData\Local'
}

$logDirectory = Join-Path $localAppData 'TelegramProxy'
$logPath = Join-Path $logDirectory 'startup.log'
New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null

function Write-StartupStatus {
    param([string]$Message)

    $entry = '[{0}] {1}' -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), $Message
    Add-Content -LiteralPath $logPath -Value $entry -Encoding UTF8
    Write-Output $Message
}

$exeCandidates = @(
    (Join-Path $PSScriptRoot 'release\TelegramProxy.exe'),
    (Join-Path $PSScriptRoot 'TelegramProxy.exe'),
    (Join-Path $PSScriptRoot 'dist\TelegramProxy.exe')
)
$exePath = $null
foreach ($candidate in $exeCandidates) {
    if (Test-Path -LiteralPath $candidate) {
        $exePath = $candidate
        break
    }
}

if (-not $exePath) {
    Write-StartupStatus 'TelegramProxy.exe was not found.'
    exit 1
}

$listener = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
if ($listener) {
    Write-StartupStatus "TCP port $port is already in use; no second instance was started."
    exit 0
}

try {
    Start-Process -FilePath $exePath -WorkingDirectory (Split-Path -Parent $exePath) `
        -WindowStyle Hidden -ErrorAction Stop | Out-Null
} catch {
    Write-StartupStatus "Could not launch the proxy: $($_.Exception.Message)"
    exit 1
}

for ($attempt = 0; $attempt -lt 20; $attempt++) {
    $listener = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($listener) {
        Write-StartupStatus "Proxy is running in the background on TCP port $port."
        exit 0
    }
    Start-Sleep -Seconds 1
}

Write-StartupStatus "The proxy did not start listening on TCP port $port. Check the proxy configuration."
exit 1
