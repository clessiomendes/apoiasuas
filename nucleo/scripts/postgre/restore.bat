@echo off

if "%3"=="" (goto error)

setlocal

if "%1"=="prod" @call prod.env
if "%1"=="valid" @call valid.env
if "%1"=="demo" @call demo.properties.cmd
@set PGPASSWORD=%2

@echo on
psql --set ON_ERROR_STOP=off -h %host% -U %user% -d %database% < %3 > %temp%\output.log 2>&1
@echo off
goto eof

:error
@echo(
echo uso: restore ambiente[prod/valid/demo] senha_bd arquivo-backup
@echo(

:eof