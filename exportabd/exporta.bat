@echo off
@setlocal

if "%1"=="" (
    @echo Uso: exporta.bat arquivo-de-configuracao [reenvia]
    GOTO :EOF
)

rem eol stops comments from being parsed
rem otherwise split lines at the = char into two tokens
for /F "eol=# delims== tokens=1,*" %%a in (%1) do (

    rem proper lines have both a and b set
    rem if okay, assign property to some kind of namespace
    rem so some.property becomes test.some.property in batch-land
    if NOT "%%a"=="" if NOT "%%b"=="" set %%a=%%b
)

IF DEFINED WINDOWS_VERSION (
    ECHO WINDOWS_VERSION: %WINDOWS_VERSION%
    IF "%WINDOWS_VERSION%" == "xp" (
        @set "CURLPATH=.\curlxp\curl.exe"
    ) else (
        IF "%WINDOWS_VERSION%" == "w7" (
            @set "CURLPATH=.\curlw7\curl.exe"
        ) else (
            ECHO Possiveis opções para WINDOWS_VERSION: xp ou w7. Opção fornecida: %WINDOWS_VERSION%
            GOTO :EOF
        )
    )
) ELSE (
    ECHO WINDOWS_VERSION não definido
    GOTO :EOF
)
IF DEFINED USUARIO_APOIACRAS (ECHO USUARIO_APOIACRAS: %USUARIO_APOIACRAS%) ELSE (
    ECHO USUARIO_APOIACRAS não definido
    GOTO :EOF
)
IF DEFINED SENHA_APOIACRAS (ECHO SENHA_APOIACRAS definida) ELSE (
    ECHO SENHA_APOIACRAS não definido
    GOTO :EOF
)
IF DEFINED URL_APOIACRAS (ECHO URL_APOIACRAS: %URL_APOIACRAS%) ELSE (
    ECHO URL_APOIACRAS não definido
    GOTO :EOF
)
IF DEFINED NOME_ARQUIVO_INTERMEDIARIO (ECHO NOME_ARQUIVO_INTERMEDIARIO: %NOME_ARQUIVO_INTERMEDIARIO%) ELSE (
    ECHO NOME_ARQUIVO_INTERMEDIARIO não definido
    GOTO :EOF
)
IF DEFINED SERVIDOR_PROXY (
    ECHO SERVIDOR_PROXY: %SERVIDOR_PROXY%
    IF DEFINED USUARIO_PROXY (
        ECHO USUARIO_PROXY: %USUARIO_PROXY%
        IF DEFINED SENHA_PROXY (ECHO SENHA_PROXY definida) ELSE (
            ECHO SENHA_PROXY não definido
            GOTO :EOF
        )
    )
)

if not "%USUARIO_PROXY%"=="" set "PROXY_OPTIONS=--proxy-user %USUARIO_PROXY%:%SENHA_PROXY%"
echo Proxy options: %PROXY_OPTIONS%

if not "%SERVIDOR_PROXY%"=="" set "PROXY=--proxy %SERVIDOR_PROXY% %PROXY_OPTIONS%"
echo Proxy: %PROXY%

@set "URL=%URL_APOIACRAS%?user=%USUARIO_APOIACRAS%^&pass=%SENHA_APOIACRAS%"
echo Url completa: %URL%

rem @set "LINHA_COMANDO_COMPLETO=""%CURLPATH%"" %PROXY%  -k ""%URL%"" -F ""qqfile=@%NOME_ARQUIVO_INTERMEDIARIO%x"" ^> resultado.html"
rem echo Linha de comando completa: %LINHA_COMANDO_COMPLETO%

echo Todos os parâmetros foram definidos corretamente

@echo chamando exporta.js
cscript /nologo exporta.js
if errorlevel 500 set ENVIA=true
@echo exporta.js returned %errorlevel%

rem call %LINHA_COMANDO_COMPLETO%
rem if errorlevel 500 call %LINHA_COMANDO_COMPLETO%
if "%2" == "reenvia" (
    echo Enviando mesmo que não haja mudanças no arquivo
    set ENVIA=true
)

if "%ENVIA%" == "true" call "%CURLPATH%" %PROXY%  -k "%URL%" -F "qqfile=@%NOME_ARQUIVO_INTERMEDIARIO%x" > resultado.html

@type resultado.html
pause