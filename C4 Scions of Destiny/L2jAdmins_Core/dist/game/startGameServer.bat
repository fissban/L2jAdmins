@echo off
title Game Server L2jAdmins Console

:start

java -server -Xms1g -Xmx1g -XX:MetaspaceSize=256M -cp ./../libs/*;./../libs/Core.jar l2j.gameserver.GameServer
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