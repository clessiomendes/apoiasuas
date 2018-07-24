cd ..
echo Inicio: %time%
setlocal
set ambiente=demo
set data_source=CLEVERCLOUD_POSTGRES_DEMO

::Compilação e geração da pasta war em uma pasta temporaria
::call grails clean-all --non-interactive --refresh-dependencies
::rmdir /s /q .\target
rmdir /s /q c:\temp\deploy-cc\apoiasuas.war
mkdir c:\temp\deploy-cc
mkdir c:\temp\deploy-cc\apoiasuas.war
call grails prod war --non-interactive --stacktrace -Dorg.apoiasuas.datasource=%data_source% -Dgrails.project.work.dir=tmp_deploy

::Limpa e copia os arquivos da pasta temporaria para o a pasta de deploy do clever cloud
rmdir /s /q c:\workspaces\deploy-cc\%ambiente%\apoiasuas.war
mkdir c:\workspaces\deploy-cc\%ambiente%\apoiasuas.war
xcopy /e c:\temp\deploy-cc\apoiasuas.war c:\workspaces\deploy-cc\%ambiente%\apoiasuas.war
::copy .\target\apoiasuas-0.1.war c:\workspaces\deploy-cc\%ambiente%\apoiasuas.war

::Faz o commit/push para terminar o deploy da aplicacao (que reinicia automaticamente)
cd c:\workspaces\deploy-cc\%ambiente%\
git add .
git commit -m "deploy"
git push -f clever master
echo Fim: %time%
