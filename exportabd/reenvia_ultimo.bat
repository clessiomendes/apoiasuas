@setlocal

@rem Windows 7: troque o caminho abaixo de curlxp para curlw7
@set "curlpath=.\curlw7\curl.exe"

@set "usuario=web_service_vistaalegre"
@set "senha=cras2016"
@set "url=https://apoiacras.cleverapps.io/importacaoFamilias/restUpload?user=%usuario%&pass=%senha%"

call "%curlpath%" -k "%url%" -F "qqfile=@intermediario.xlsx" > resultado.html
@type resultado.html
pause