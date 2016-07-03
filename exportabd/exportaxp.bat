@setlocal

@rem Windows 7: troque o caminho abaixo de curlxp para curlw7
@set "curlpath=y:\clessio\apoiasuas\curlxp\curl.exe"

@set "usuario=web_service"
@set "senha=videAU12"
@set "url=https://apoiacras.cleverapps.io/importacaoFamilias/restUpload?user=%usuario%&pass=%senha%"

cscript /nologo exporta.js
@echo exporta.js returned %errorlevel%
if errorlevel 500 call "%curlpath%" --proxy ssl://cache01.pbh:3128 --proxy-user cras.havaiventosa:videAU12 -k "%url%" -F "qqfile=@intermediario.xlsx" > resultado.html
@type resultado.html
pause