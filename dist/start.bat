@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"
title Telegram Proxy Server
color 0A

echo ===================================================
echo    TELEGRAM PROXY SERVER ISHGA TUSHMOQDA...
echo ===================================================
echo.
echo Telegram sozlamalariga (Nastroyka) kirib, quyidagi
echo ma'lumotlarni kiriting:
echo.
echo   - Hostname (Server): 127.0.0.1
echo   - Port: 8444
echo   - Secret: 8def4f5184d6aa6aaf744d2475bac7ef
echo.
echo Yoki tayyor havolani brauzerga tashlang:
echo tg://proxy?server=127.0.0.1^&port=8444^&secret=8def4f5184d6aa6aaf744d2475bac7ef
echo.
echo ===================================================
echo Diqqat: Telegram ishlab turishi uchun bu oynani yopmang!
echo ===================================================
echo.

TelegramProxy.exe
pause