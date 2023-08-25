@echo off
title L2Open server: Game Server Console
:start
set user=root
set pass=
set DBname=l2pdb
set DBHost=localhost
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (
set ctime=0%ctime:~1,1%
)
set ctime=%ctime%.%TIME:~3,2%.%TIME:~6,2%
echo.
echo Making a full backup into %DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe %Ignore% --add-drop-table -h %DBHost% -u %user% --password=%pass% %DBname% > D:/bakx50/%DATE%-%ctime%_backup_full.sql
 echo.
echo Backup complite %DATE%-%ctime%_backup_full.sql
echo.
rem ======== Optimize memory settings =======
rem Minimal size with geodata is 1.5G, w/o geo 1G
rem Make sure -Xmn value is always 1/4 the size of -Xms and -Xmx.
rem -Xms and -Xmx should always be equal.
rem ==========================================
REM одинаковый размер памяти для Xms и Xmx, JVM пытается удержать размер heap'а минимальным, и если его нужно меньше, чем в Xmx - гоняет GC понапрасну
SET java_opts=-Xms8G
SET java_opts=%java_opts% -Xmx8G

REM Альтернативные настройки самой JVM.
SET java_opts=%java_opts% -XX:+DoEscapeAnalysis
REM Циклы заполнения/копирования массивов заменяются на прямые машинные инструкции для ускорения работы.
SET java_opts=%java_opts% -XX:+OptimizeFill
REM Опция, устраняет лишние блокировки путем их объединения.
SET java_opts=%java_opts% -XX:+EliminateLocks
REM Позволяет расширить диапазон кешируемых значений для целых типов при старте виртуальной машины.
SET java_opts=%java_opts% -XX:AutoBoxCacheMax=65536
REM TODO: Изучить данный конфиг, как он будет вести себя у нас в сборке. 4000 - по умолчанию, 500 - при включенной AggressiveOpts
REM SET java_opts=%java_opts% -XX:BiasedLockingStartupDelay=4000
REM TODO: Изучить профит у нас в сборке, от данной опции.
SET java_opts=%java_opts% -XX:+OptimizeStringConcat
REM SET java_opts=%java_opts% -XX:+UseCompressedStrings
REM TODO: Не совсем понятный параметр.
REM ! SET java_opts=%java_opts% XX:+EliminateAutoBox
REM TODO: Провести тесты, на сколько сильно, это нам секономит потребление ОЗУ.
REM SET java_opts=%java_opts% -XX:+UseCompressedOops
REM TODO: Есть ли нам профит от данной опции?
REM SET java_opts=%java_opts% -XX:+UseStringCache
REM Включает опции которые выше + использует альтернативную библиотеку...
REM ! SET java_opts=%java_opts% -XX:+AggressiveOpts

REM Настройки сборщиков мусора.
REM ###################################################################################
REM SET java_settings=%java_settings% -Xincgc
REM SET java_settings=%java_settings% -Xnoclassgc
REM -----------------------------------------------------------
SET java_opts=%java_opts% -XX:+UseParNewGC
REM SET java_opts=%java_opts% -XX:+UseParallelOldGC
REM Параметр равен, количеству потоков ЦП.
SET java_opts=%java_opts% -XX:ParallelGCThreads=24

REM -----------------------------------------------------------
REM Хуеватый вариант, по предварительной оценке:)
REM SET java_opts=%java_opts% -XX:+UseG1GC
REM SET java_opts=%java_opts% -XX:MaxGCPauseMillis=50
REM SET java_opts=%java_opts% -XX:GCPauseIntervalMillis=60000
REM -----------------------------------------------------------
REM -----------------------------------------------------------
REM Дает очень частые задержки, но в замен держит ОЗУ в чистоте и порядке.
SET java_opts=%java_opts% -XX:+UseConcMarkSweepGC
REM 
REM Параметр равен, количеству потоков ЦП.
SET java_opts=%java_opts% -XX:ParallelCMSThreads=24
REM SET java_opts=%java_opts% -XX:+ExplicitGCInvokesConcurrent
REM SET java_opts=%java_opts% -XX:+CMSParallelRemarkEnabled
REM SET java_opts=%java_opts% -XX:+CMSScavengeBeforeRemark

SET java_opts=%java_opts% -XX:+CMSClassUnloadingEnabled
SET java_opts=%java_opts% -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses

REM SET java_opts=%java_opts% -XX:+UseCMSInitiatingOccupancyOnly
REM SET java_opts=%java_opts% -XX:CMSInitiatingOccupancyFraction=60
REM -----------------------------------------------------------
REM ###################################################################################
REM Логирование, использовать только для отладки.
md .\gc_log\
REM SET java_opts=%java_opts% -verbose:gc
REM SET java_opts=%java_opts% -XX:+PrintHeapAtGC
REM SET java_opts=%java_opts% -XX:+PrintGCDetails
REM SET java_opts=%java_opts% -XX:+PrintGCDateStamps
REM SET java_opts=%java_opts% -XX:+PrintGCApplicationStoppedTime
REM SET java_opts=%java_opts% -XX:+PrintGC
REM SET java_opts=%java_opts% -Xloggc:.\gc_log\garbage_collector%DATE%-%ctime%.log
REM SET java_opts=%java_opts% -XX:+PrintGCTimeStamps
REM SET java_opts=%java_opts% -XX:+PrintTenuringDistribution

SET java_settings=-Dfile.encoding=UTF-8
SET java_settings=%java_settings% -Djava.net.preferIPv4Stack=true
SET java_settings=%java_settings% -Xbootclasspath/p:./lib/jsr167_8.jar

REM Используется для апдейта ядра/скриптов при рестарте сервера.
REM На всякий пожарный делается бекап библиотек.
if exist .\update\l2openserver.jar (md .\backup%DATE%-%ctime%\) else ( if exist .\update\tsrsvp.jar (md .\backup%DATE%-%ctime%\)else ( if exist .\update\geodata\*.l2j (md .\backup%DATE%-%ctime%\)))
if exist .\update\l2openserver.jar move .\lib\l2openserver.jar .\backup%DATE%-%ctime%\
if exist .\update\tsrsvp.jar move .\lib\tsrsvp.jar .\backup%DATE%-%ctime%\
if exist .\update\l2openserver.jar move .\update\l2openserver.jar .\lib\
if exist .\update\tsrsvp.jar move .\update\tsrsvp.jar .\lib\
if exist .\update\geodata\*.l2j move .\geodata\*.l2j .\backup%DATE%-%ctime%\
if exist .\update\geodata\*.l2j move .\update\geodata\*.l2j .\geodata\


java -server %java_settings% %java_opts% -Duser.timezone=GMT+3 -cp ../../scripts/tsrsvp.jar;../../kernel/l2openserver.jar;./lib/tools.jar;../../../lib/tools-game.jar;./lib/l2openserver.jar;./lib/*; l2open.gameserver.GameStart

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
