echo on
cd ..
echo Inicio: %time%
setlocal
set sub_dir_deploy=prod

set modo=prod
set war=apoiasuas.war

::Compilação e geração da pasta war em uma pasta temporaria
rmdir /s /q c:\temp\deploy-cc\apoiasuas.war
mkdir c:\temp\deploy-cc
mkdir c:\temp\deploy-cc\apoiasuas.war
call grails prod war --non-interactive --stacktrace -Dorg.apoiasuas.modo=%modo% -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRE -Dgrails.project.work.dir=tmp_deploy
echo on

::Limpa e copia os arquivos da pasta temporaria para o a pasta de deploy do clever cloud
rmdir /s /q c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%
mkdir c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%
xcopy /e /q c:\temp\deploy-cc\apoiasuas.war c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%
copy .\ambientes\ambiente-%modo%.properties c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%\WEB-INF\classes\ambiente.properties

echo Fim do build %modo%

set modo=demo
set war=apoiasuas_demo.war

::Compilação e geração da pasta war em uma pasta temporaria
rmdir /s /q c:\temp\deploy-cc\apoiasuas.war
mkdir c:\temp\deploy-cc
mkdir c:\temp\deploy-cc\apoiasuas.war
call grails prod war --non-interactive --stacktrace -Dorg.apoiasuas.modo=%modo% -Dorg.apoiasuas.datasource=CLEVERCLOUD_POSTGRE -Dgrails.project.work.dir=tmp_deploy
echo on

::Limpa e copia os arquivos da pasta temporaria para o a pasta de deploy do clever cloud
rmdir /s /q c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%
mkdir c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%
xcopy /e /q c:\temp\deploy-cc\apoiasuas.war c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%
copy .\ambientes\ambiente-%modo%.properties c:\workspaces\deploy-cc\%sub_dir_deploy%\%war%\WEB-INF\classes\ambiente.properties

echo Fim do build %modo%

::Faz o commit/push para terminar o deploy da aplicacao (que reinicia automaticamente)
cd c:\workspaces\deploy-cc\%sub_dir_deploy%
git add .
git commit -m "deploy"
git push -f clever master
echo Fim do deploy completo (%time%)
