@echo off
setlocal
cd /d "%~dp0"
title Telegram Proxy Server

REM Eski jarayonlarni tozalash
taskkill /F /IM TelegramProxy.exe 2>nul
taskkill /F /IM python.exe 2>nul

REM config.py ni release papkasiga nusxalash
if exist config.py copy config.py release\config.py >nul

echo ===================================================
echo    TELEGRAM PROXY SERVER ISHGA TUSHMOQDA...
echo ===================================================
echo.
echo Telegram proxy sozlamalariga quyidagilarni kiriting:
echo   - Hostname: 127.0.0.1
echo   - Port: 8444
echo   - Secret (Fake-TLS): ee8def4f5184d6aa6aaf744d2475bac7ef7777772e676f6f676c652e636f6d
echo.
echo ===================================================
echo Proxy ishlamoqda. Bu oynani yopmang!
echo ===================================================
echo.

cd release
TelegramProxy.exe
pause
