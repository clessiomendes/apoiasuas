@echo(
@echo(
@echo                     A T E N C A O ! ! ! !
@echo(
@echo Acessando banco de dados de producao
@echo(
@echo(

@echo off
setlocal

set host=bemstxtxm1dayhm-postgresql.services.clever-cloud.com
set database=bemstxtxm1dayhm
set user=uqupfxoijo5ecvvkw3cj
@set PGPASSWORD=adp0EGEZCmO9taiK6g3x

@echo on
::pg_dump -U user_name -h remote_host -p remote_port name_of_database > name_of_backup_file
pg_dump -O -h %host% -U %user% %database% > bak-prod-postgre