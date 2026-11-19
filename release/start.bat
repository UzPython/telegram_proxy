@echo off
setlocal
cd /d "%~dp0"
title Telegram Proxy Server
echo Starting Telegram Proxy. Keep this window open.
echo The connection link and secret will appear below.
echo.
TelegramProxy.exe
pause
