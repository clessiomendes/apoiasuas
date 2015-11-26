cd ..
rem grails prod war --stacktrace -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRES_PROD
copy .\target\apoiasuas-0.1.war ..\teste1\target\apoiasuas.war
cd ..\teste1
deploy-clevercloud.bat
cd c:\workspaces\apoiaSUAS\scripts