@echo off
title L2Open: Login Server Console
:start
echo %DATE% %TIME% Login server is running !!! > login_is_running.tmp
echo Starting L2Open Login Server.
echo.
java -server -Xbootclasspath/p:./lib/jsr167_8.jar -Xms32m -Xmx32m -cp lib/*; l2open.loginserver.L2LoginStart
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
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
del login_is_running.tmp
pause
