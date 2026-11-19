@echo off
setlocal
title Telegram Proxy Server
echo Telegram Proxy fonda ishga tushirilmoqda...
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\start_proxy.ps1"
if errorlevel 1 (
    echo Ishga tushmadi. Jurnal: %LOCALAPPDATA%\TelegramProxy\startup.log
    pause
    exit /b 1
)
echo Oyna yopilsa ham proxy fonda ishlashda davom etadi.