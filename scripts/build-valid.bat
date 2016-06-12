cd ..
call grails prod war --stacktrace -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRES_VALID
copy .\target\apoiasuas-0.1.war ..\deploy-cc\valid\apoiasuas.war
cd ..\deploy-cc\valid\
git add .
git commit -m "deploy validacao"
git push clever master
rem cd c:\workspaces\apoiaSUAS\scripts