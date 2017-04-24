cd ..
::echo Inicio: %time%
::call grails clean-all --non-interactive --refresh-dependencies
::rmdir /s /q .\target
::call grails prod war --stacktrace -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRES_VALID -Dgrails.project.work.dir=tmp_deploy
copy .\target\apoiasuas-0.1.war c:\workspaces\deploy-cc\valid\apoiasuas.war
cd ..\deploy-cc\valid\
git add .
git commit -m "deploy validacao"
git push clever master
echo Fim: %time%
