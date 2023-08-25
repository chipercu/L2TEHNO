@echo off
title L2Open server: Game Console
:start
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (
set ctime=0%ctime:~1,1%
)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
SET java_opts=-Xms32M
SET java_opts=%java_opts% -Xmx32M
SET java_opts=%java_opts% -XX:+DoEscapeAnalysis
SET java_opts=%java_opts% -XX:+OptimizeFill
SET java_opts=%java_opts% -XX:+EliminateLocks
SET java_opts=%java_opts% -XX:AutoBoxCacheMax=65536
SET java_opts=%java_opts% -XX:+OptimizeStringConcat
SET java_opts=%java_opts% -XX:+UseCompressedStrings
SET java_opts=%java_opts% -XX:+UseParNewGC
SET java_opts=%java_opts% -XX:ParallelGCThreads=24
SET java_opts=%java_opts% -XX:+UseConcMarkSweepGC
SET java_opts=%java_opts% -XX:ParallelCMSThreads=24
SET java_opts=%java_opts% -XX:+CMSClassUnloadingEnabled
SET java_opts=%java_opts% -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses

SET java_settings=%java_settings% -Dfile.encoding=UTF-8
SET java_settings=%java_settings% -Djava.net.preferIPv4Stack=true
SET java_settings=%java_settings% -Xbootclasspath/p:./lib/jsr167.jar

java -server %java_settings% %java_opts% -cp ../../kernel/l2openserver.jar;./lib/tools.jar;../../../lib/tools-game.jar;./lib/l2openserver.jar;./lib/*; l2open.gameserver.instancemanager.MMOVoteManager

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
del gameserver_is_running.tmp
pause
