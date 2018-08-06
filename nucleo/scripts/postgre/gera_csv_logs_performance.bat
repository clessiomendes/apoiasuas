@echo(
@echo(
@echo Uso: 
@echo gera_csv_logs_performance [inicio] [fim]
@echo inicio e fim sao opcionais e devem ser fornecidos no formato DD/MM/YYYY-HH24:MI:SS
@echo(
@echo(

@echo off
setlocal
if "%1"=="" (goto error)
@set PGPASSWORD=%1

if "%2"=="" (SET inicio=01/01/1900-00:00:00) ELSE (SET inicio=%2)
if "%3"=="" (SET fim=31/12/2900-00:00:00) ELSE (SET fim=%3)

set host=bcxpj29zvmrjzfl-postgresql.services.clever-cloud.com
set database=bcxpj29zvmrjzfl
set user=uwbhk7dyxjsqyljjw0qj
set "saida=logs_bd.csv"
set "sql=select * from log where inicio >= to_timestamp('%inicio%', 'DD/MM/YYYY-HH24:MI:SS') and inicio <= to_timestamp('%fim%', 'DD/MM/YYYY-HH24:MI:SS') order by id"

@echo(
@echo "%sql%"

psql -h %host% -U %user% -d %database% -c "\COPY (%sql%) TO '%saida%' DELIMITER ';' CSV HEADER;"

IF %ERRORLEVEL% EQU 0 (
	@echo(
	@echo resultado gravado em %saida%
	@echo(
)
goto eof

:error
@echo(
echo uso: gera_csv_logs senha_bd_logs [inicio|"01/01/1900-00:00:00"] [fim|"31/12/2900-00:00:00"]
@echo(

:eof

