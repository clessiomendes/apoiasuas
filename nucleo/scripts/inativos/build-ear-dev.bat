cd ..
call grails dev ear --stacktrace -Dorg.apoiasuas.datasource=LOCAL_POSTGRES
rem copy .\target\apoiasuas-0.1.war ..\deploy-cc\prod\apoiasuas.war
rem cd ..\deploy-cc\prod\
rem git add .
rem git commit -m "deploy producao"
rem git push clever master
cd c:\workspaces\apoiaSUAS\scripts