cd ..
call grails prod war --stacktrace -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRES_PROD -Dgrails.project.work.dir=tmp_deploy
copy .\target\apoiasuas-0.1.war ..\deploy-cc\prod\apoiasuas.war
cd ..\deploy-cc\prod\
git add .
git commit -m "deploy producao"
git push clever master
cd c:\workspaces\apoiaSUAS\scripts