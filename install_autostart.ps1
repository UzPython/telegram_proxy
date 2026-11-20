$ErrorActionPreference = 'Stop'

$launcher = Join-Path $PSScriptRoot 'start_proxy.ps1'
if (-not (Test-Path -LiteralPath $launcher)) {
    throw "Background launcher not found: $launcher"
}

$startupFolder = [Environment]::GetFolderPath('Startup')
$shortcutPath = Join-Path $startupFolder 'Telegram Proxy.lnk'
$shell = New-Object -ComObject WScript.Shell
$shortcut = $shell.CreateShortcut($shortcutPath)
$shortcut.TargetPath = Join-Path $PSHOME 'powershell.exe'
$shortcut.Arguments = '-NoProfile -ExecutionPolicy Bypass -WindowStyle Hidden -File "' + $launcher + '"'
$shortcut.WorkingDirectory = $PSScriptRoot
$shortcut.WindowStyle = 7
$shortcut.Description = 'Start Telegram Proxy at Windows sign-in'
$shortcut.Save()

Write-Output 'Telegram Proxy will start in the background when this user signs in.'
