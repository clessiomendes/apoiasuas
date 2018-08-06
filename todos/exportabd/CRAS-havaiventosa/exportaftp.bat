@setlocal

@set "servidor=bucket-8babfda0-0ea6-4ba6-a0fb-ca1d2e583b4f-fsbucket.services.clever-cloud.com"
@set "usuario=user8babfda00ea64ba6a0fbca1d2e583b4f"
@set "senha=JHgQIlZKYTFqPaFY"
@set "arquivo=intermediario.xlsx"
@set "servico=cras-havai-ventosa-id-1"
@set "destino=%servidor%/apoiasuas-repositorio/importacao/%servico%.enviando"

@rem roda script para geracao do arquivo a ser enviado
@cscript /nologo exporta.js

@rem envia arquivo por ftp
@echo exporta.js returned %errorlevel%
@if errorlevel 500 (
    call wput -B --tries=5 --force-tls %arquivo% ftp://%usuario%:%senha%@%destino%
    @rem renomeia arquivo no destino sinalizando o fim do upload
    @echo del apoiasuas-repositorio/importacao/%servico%.xlsx>ftpcmd.txt
    @echo ren apoiasuas-repositorio/importacao/%servico%.enviando apoiasuas-repositorio/importacao/%servico%.xlsx>>ftpcmd.txt
    psftp -b ftpcmd.txt -be -batch -l %usuario% -pw %senha% %servidor%
    del ftpcmd.txt /f
)

pause