@echo off
setlocal

set host=bbvqaqnqnk6vvm2-postgresql.services.clever-cloud.com
set database=bbvqaqnqnk6vvm2
set user=uqks5egoiuaypimpuccw
@set PGPASSWORD=IwUJjNO5IP7qJeRprEsP

psql -h %host% -U %user% -d %database% < %1
