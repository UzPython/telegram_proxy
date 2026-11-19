@echo off
setlocal
if not defined LOCALAPPDATA (
    echo LOCALAPPDATA is not available for this Windows account.
    pause
    exit /b 1
)
set "SECRET_FILE=%LOCALAPPDATA%\TelegramProxy\proxy_secret.txt"
if exist "%SECRET_FILE%" del "%SECRET_FILE%"
if exist "%SECRET_FILE%" (
    echo Could not remove the old secret.
    pause
    exit /b 1
)
echo The old secret was removed. The next launch will create a new secret.
echo Start start.bat to run the proxy and display its new connection link.
pause
