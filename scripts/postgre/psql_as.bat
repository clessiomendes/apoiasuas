@echo off

if "%2"=="" (goto error)
if "%3"=="" (set saida=%temp%\bak-postgre.sql) else (set saida=%3)

setlocal

if "%1"=="prod" @call prod.properties.cmd
if "%1"=="valid" @call valid.properties.cmd
if "%1"=="demo" @call demo.properties.cmd
@set PGPASSWORD=%2

psql -h %host% -U %user% -d %database%

goto eof

:error
@echo(
echo uso: psql ambiente[prod/valid/demo] senha_bd
@echo(

:eof