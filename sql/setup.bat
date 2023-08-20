@echo off
TITLE L2 Open-Team Setup
REM ######################################## Automatic updater for L2 Open - Do not edit !!!
goto answer%ERRORLEVEL%
:answerTrue
set fastend=yes
goto upgrade_db
:answer0
set fastend=no

set user=root
set pass=68464846l2
set DBname=l2tehno
set DBHost=localhost

set Generaltables=accounts augmentations clanhall gameservers banned_ips loginserv_log character_friends character_hennas character_macroses character_quests character_recipebook character_shortcuts character_skills character_effects_save character_skills_save character_subclasses characters character_variables clanhall_bids clanhall_data clan_data clanhall_decorations_bids ally_data clan_wars items pets server_variables seven_signs seven_signs_festival siege_clans killcount dropcount craftcount game_log petitions seven_signs_status global_tasks raidboss_status manor_crop manor_seeds hellbound multisell_log character_vote
set Ignore=--ignore-table=%DBname%.game_log --ignore-table=%DBname%.loginserv_log --ignore-table=%DBname%.petitions

REM ########################################
mysql.exe -h %DBHost% -u %user% --password=%pass% --execute="CREATE DATABASE IF NOT EXISTS %DBname%"
if not exist backup (
mkdir backup
)

REM ######################################## :main_menu
:main_menu
cls
echo.L2 Open Setup
echo.
echo.### Main Menu ###
echo.
echo.(1) Install Login Server
echo.(2) Install Game Server
echo.(3) Upgrade DB
echo.(4) Backup DB
echo.(5) Restore DB
echo.(6) Lost data in DB
echo.(7) Install option data
echo.(q) Quit
echo.
set button=x
set /p button=What do you want ?:
if /i %button%==1 goto Install_Login_Server_menu
if /i %button%==2 goto Install_Game_Server_menu
if /i %button%==3 goto upgrade_menu
if /i %button%==4 goto backup_menu
if /i %button%==5 goto restore_menu
if /i %button%==6 goto lost_data_menu
if /i %button%==7 goto install_option_data
if /i %button%==q goto end
goto main_menu

REM ######################################## :Install_Login_Server_menu
:Install_Login_Server_menu
cls
echo.L2 Open Setup
echo.
echo.### Install Login Server ###
echo.
echo.(i) Install Login Server. Warning !!! accounts, gameservers, banned_ips will be deleted !!!
echo.(m) Main menu
echo.(q) Quit
echo.
set button=x
set /p button=Your choice ?:
if /i %button%==i goto Install_Login_Server
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto Install_Login_Server_menu

REM ######################################## :Install_Game_Server_menu
:Install_Game_Server_menu
cls
echo.L2 Open Setup
echo.
echo.### Install Game Server ###
echo.
echo.(i) Install Game Server. Warning !!! All Game Server Database will be deleted !!!
echo.(m) Main menu
echo.(q) Quit
echo.
set button=x
set /p button=Your choice ?:
if /i %button%==i goto Install_Game_Server
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto Install_Game_Server_menu

REM ######################################## :upgrade_menu
:upgrade_menu
cls
echo.L2 Open Setup
echo.
echo.### Upgrade Menu ###
echo.
echo.(u) Upgrade Database
echo.(m) Main menu
echo.(q) Quit
echo.
set button=x
set /p button=Your choice ?:
if /i %button%==u goto upgrade_db
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto upgrade_menu

REM ######################################## :backup_menu
:backup_menu
cls
echo.L2 Open Setup
echo.
echo.### Backup Menu ###
echo.
echo.(1) Full backup
echo.(2) General tables backup only
echo.(m) Main menu
echo.(q) Quit
echo.
set button=x
set /p button=Select backup type ?:
if /i %button%==1 goto full_backup
if /i %button%==2 goto general_backup
if /i %button%==m goto setup
if /i %button%==q goto end
goto backup_menu

REM ######################################## :restore_menu
:restore_menu
cls
echo.List all files in dir "/backup" !
echo.
dir backup /B /P
echo.
echo.L2 Open Setup
echo.
echo.### Restore Menu ###
echo.
echo.Enter a full filename do you want to restore to the database !
echo.(m) Main menu
echo.(q) Quit
echo.
set filename=x
set /p filename=Enter filename ?:
if /i %filename%==m goto main_menu
if /i %filename%==q goto end
if /i %filename%==%filename% goto restore_DB
goto restore_menu

REM ######################################## :lost_data_menu
:lost_data_menu
cls
echo.L2 Open Setup
echo.
echo.### Lost Data Menu ###
echo.
echo.(1) Show lost data
echo.(2) Delete lost data
echo.(m) Main menu
echo.(q) Quit
echo.
set button=x
set /p button=Select backup type ?:
if /i %button%==1 goto show_lost_data
if /i %button%==2 goto delete_lost_data
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto lost_data_menu

:show_lost_data
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < tools/maintenance/lost_data_show.sql
pause
goto lost_data_menu

:delete_lost_data
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < tools/maintenance/lost_data_del.sql
echo.
echo.All lost data deleted !!!
echo.
pause
goto lost_data_menu

REM ######################################## :install_option_data
:install_option_data
cls
echo.L2 Open Setup
echo.
echo.### Install_option_data ###
echo.
echo.(1) Install TeleToGH SQL patch
echo.(m) Main menu
echo.(q) Quit
echo.
set button=x
set /p button=Your choice ?:
if /i %button%==1 goto Install_TeleToGH
if /i %button%==m goto main_menu
if /i %button%==q goto end
goto Install_TeleToGH

REM ######################################## :Install_TeleToGH
:Install_TeleToGH
echo.Installing TeleToGH SQL patch !!!
echo.
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < optional/teletogh/patch.sql
echo.
echo.TeleToGH SQL patch Installed !!!
echo.
pause
goto main_menu

REM ######################################## :Install_Login_Server
:Install_Login_Server
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (
set ctime=0%ctime:~1,1%
)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a full backup into %DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe %Ignore% --add-drop-table -h %DBHost% -u %user% --password=%pass% %DBname% > backup/%DATE%-%ctime%_backup_full.sql
echo.
echo.Installing Login Server !!!
echo.
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < login/accounts.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < login/banned_ips.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < login/loginserv_log.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < login/lock.sql
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < login/referrals.sql
echo.
echo.Login Server Installed !!!
echo.
pause
goto main_menu

REM ######################################## :Install_Game_Server
:Install_Game_Server
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (
set ctime=0%ctime:~1,1%
)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a full backup into %DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe %Ignore% --add-drop-table -h %DBHost% -u %user% --password=%pass% %DBname% > backup/%DATE%-%ctime%_backup_full.sql
echo.
echo.Installing general tables !!!
echo.
for /r install %%f in (*.sql) do (
echo Loading %%~nf ... 
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < %%f
)
goto upgrade_db

REM ######################################## :upgrade_db
:upgrade_db
echo.
echo Upgrading tables !!!
echo.
for /r upgrade %%f in (*.sql) do (
echo Loading %%~nf ... 
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < %%f
)
echo.
echo.Complete !!!
echo.
if /I %fastend%==yes goto :EOF
pause
goto main_menu

REM ######################################## :full_backup
:full_backup
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (
set ctime=0%ctime:~1,1%
)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a full backup into %DATE%-%ctime%_backup_full.sql
echo.
mysqldump.exe %Ignore% --add-drop-table -h %DBHost% -u %user% --password=%pass% %DBname% > backup/%DATE%-%ctime%_backup_full.sql
goto end

REM ######################################## :general_backup
:general_backup
set ctime=%TIME:~0,2%
if "%ctime:~0,1%" == " " (
set ctime=0%ctime:~1,1%
)
set ctime=%ctime%'%TIME:~3,2%'%TIME:~6,2%
echo.
echo Making a general tables backup into %DATE%-%ctime%_backup_general.sql
echo.
mysqldump.exe %Ignore% --add-drop-table -h %DBHost% -u %user% --password=%pass% %DBname% %Generaltables% > backup/%DATE%-%ctime%_backup_general.sql
goto end

REM ######################################## :restore_DB
:restore_DB
if not exist backup/%filename% (
echo.
echo.File not found !
echo.
pause
goto restore_menu
)
cls
echo.
echo.Restore from file %filename% !
echo.
pause
mysql.exe -h %DBHost% -u %user% --password=%pass% -D %DBname% < backup/%filename%
goto end

REM ######################################## :not_working_now
:not_working_now
echo.
echo Not working NOW !!!
echo.
pause
goto main_menu

REM ######################################## :end
:end
