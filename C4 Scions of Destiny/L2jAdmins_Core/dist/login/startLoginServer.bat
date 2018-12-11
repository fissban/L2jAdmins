@echo off
title Login Server L2jAdmins Console

:start

java -Xmx128m -cp ./../libs/*;./../libs/Core.jar l2j.loginserver.LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormally
echo.
:end
echo.
echo server terminated
echo.
pause
