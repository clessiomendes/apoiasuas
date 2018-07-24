@echo off

if "%2"=="" (goto error)
if "%3"=="" (set saida=%temp%\bak-postgre.sql) else (set saida=%3)


setlocal

if "%1"=="prod" @call prod.properties.cmd
if "%1"=="valid" @call valid.properties.cmd
if "%1"=="demo" @call demo.properties.cmd
@set PGPASSWORD=%2

@echo on
::pg_dump -U user_name -h remote_host -p remote_port name_of_database > name_of_backup_file
::https://www.postgresql.org/docs/8.4/static/app-pgdump.html
pg_dump -O -c -v -x -h %host% -U %user% %database% > %saida%
@echo backup gerado em %saida%
@echo off
goto eof

:error
@echo(
echo uso: backup ambiente[prod/valid/demo] senha_bd arquivo-backup
@echo(

:eof