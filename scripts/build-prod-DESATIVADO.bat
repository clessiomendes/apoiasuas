cd ..
echo Inicio: %time%
call grails clean-all --non-interactive --refresh-dependencies
rmdir /s /q .\target
call grails prod war --non-interactive --stacktrace -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRES_PROD -Dgrails.project.work.dir=tmp_deploy
copy .\target\apoiasuas-0.1.war c:\workspaces\deploy-cc\prod\apoiasuas.war
cd c:\workspaces\deploy-cc\prod\
git add .
git commit -m "deploy producao"
git push -f clever master
echo Fim: %time%
