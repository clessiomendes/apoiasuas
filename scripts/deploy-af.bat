@ECHO ON
@echo ============================ I N I C I A N D O ===============================
setlocal

::Ajuda
@if not "%2"=="" goto pula1
	echo "deploy-af [tag ou branch do controle de versao (current para o codigo corrente)] [prod/valid]"
	@echo off
	echo Branches:>_tmp
	git branch>>_tmp
	echo Tags:>>_tmp
	git tag>>_tmp
	type _tmp
	del _tmp
	goto eof
:pula1

::Configuracoes
set DIR_ATUAL=%cd%
set TEMP_DIR=c:\temp\deploy-af
set TAG=%1
set DESTINO=%2

if /i not "%DESTINO%"=="prod" (
	if /i not "%DESTINO%"=="valid" (
		echo Opcoes validas para o destino do deploy: prod ou valid
		goto erro
	)
)
if /i "%DESTINO%"=="valid" (
	set permSize=200m
	set heapSize=750m
	set timeZone=America/Sao_Paulo
	set appname=validacao
	set dataSource=appfog_postgres_valid
)
if /i "%DESTINO%"=="prod" (
	set permSize=200m
	set heapSize=750m
	set timeZone=America/Sao_Paulo
	set appname=prod-espelho
	set dataSource=appfog_postgres_prod
)
set JAVA_OPTS=-XX:MaxPermSize=%permSize% -XX:PermSize=%permSize% -Xms%heapSize% -Xmx%heapSize% -Duser.timezone=%timeZone% -Dorg.apoiasuas.datasource=%dataSource% -Dorg.apoiasuas.sabotagem=false
echo %JAVA_OPTS%
@echo on

::Obtendo versao do repositorio
echo Instalando de %TAG% em %DESTINO%
if /i not "%TAG%"=="current" goto pula2
	if /i "%DESTINO%"=="prod" (
		echo Nao eh permitido deploy em producao sem a criacao previa de uma branch ou tag
		goto erro
	)
	cd ..
	rmdir .\target\work\stage /s /q > nul
	goto pula3
:pula2

if /i "%TAG%"=="current" goto pula3
	rmdir %TEMP_DIR% /s /q
	mkdir %TEMP_DIR%
	git --work-tree=%TEMP_DIR% checkout %TAG% -- .
	if not "%errorlevel%"=="0" goto erro
	cd %TEMP_DIR%
	echo %TAG%>.\grails-app\views\inicio\_versao.gsp
	echo %cd%
:pula3

::Criando o pacote de deploy
if /i not "%DESTINO%"=="prod" goto pula4
	call grails prod war --stacktrace -Dorg.apoiasuas.datasource=%dataSource%
	echo on
:pula4
if /i not "%DESTINO%"=="valid" goto pula5
	call grails dev war --stacktrace -Dorg.apoiasuas.datasource=%dataSource%
	echo on
:pula5
if not "%errorlevel%"=="0" goto erro
cd .\target\work\stage
echo %cd%

::Efetuando o deploy no servidor AppFog
if /i not "%DESTINO%"=="prod" goto pula6
	::Producao? Vamo faze um backup antes
	set backup=%appname% %date%
	set backup=%backup:/=_%
	set backup="C:\Dropbox\develop\apoiasuas\bak-app-prod\%backup%.zip"
	if not exist %backup% call af download %appname% %backup%
:pula6
call af stop %appname%
if not "%errorlevel%"=="0" goto erro
echo on
call af env-del %appname% JAVA_OPTS
if not "%errorlevel%"=="0" goto erro
echo on
call af env-add %appname% JAVA_OPTS="%JAVA_OPTS%"
if not "%errorlevel%"=="0" goto erro
echo on
call af update %appname% --no-start
if not "%errorlevel%"=="0" goto erro
echo on
call af start %appname%
if not "%errorlevel%"=="0" goto erro
echo on
call af stats %appname%
if not "%errorlevel%"=="0" goto erro
echo on
cd %DIR_ATUAL%

goto eof
:erro
echo !!!!!!! E R R O !!!!!!

:eof
pause