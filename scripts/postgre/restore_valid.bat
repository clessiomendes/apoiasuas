@echo off

if "%1"=="" (goto error) ELSE (SET inicio=%1)

setlocal
set host=bbvqaqnqnk6vvm2-postgresql.services.clever-cloud.com
set database=bbvqaqnqnk6vvm2
set user=uqks5egoiuaypimpuccw
@set PGPASSWORD=IwUJjNO5IP7qJeRprEsP

@echo on
::psql --set ON_ERROR_STOP=on -h %host% -U %user% -d %database% < %1
psql --set ON_ERROR_STOP=off -h %host% -U %user% -d %database% < %1
@echo off
goto eof

:error
@echo(
echo uso: restore_valid arquivo-backup
@echo(

:eof