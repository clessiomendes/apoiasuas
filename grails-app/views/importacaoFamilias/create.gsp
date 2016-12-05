<%@ page import="org.apoiasuas.importacao.ImportacaoFamiliasController" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="importar.cadastro.de.familias"/></title>
</head>

<body>
<h1><g:message code="importar.cadastro.de.familias"/></h1>

<g:message code="linha.do.cabecalho"/> <g:textField name="inputLinhaDoCabecalho" size="2" value="${linhaDoCabecalho}"/>
<br>
<g:message code="aba.da.planilha"/> <g:textField name="inputAbaDaPlanilha" size="2" value="${abaDaPlanilha}"/>

%{--
Chamada via ajax para efetuar o upload. Passar:
action=upload -> a action faz parte da url base, por isso deve ser passada no argumento 'url', em um mapa
controler (desnecessario porque a chamada eh interna ao controler ImportarFamiliasController)
--}%

<uploader:uploader id="idUploadImportacao" sizeLimit="1000000000" debug="true" allowedExtensions="${"[\'xlsx\']"}"
                   multiple="true" url="${[action: 'upload']}">
    <uploader:onSubmit>

    %{--Aqui adicionamos o conteúdo do campo "Linha do Cabecalho" nos parametros enviados na querystring, junto com o arquivo--}%
        this.params.linhaDoCabecalho = document.getElementById("inputLinhaDoCabecalho").value;
    %{--Aqui adicionamos o conteúdo do campo "Aba da planilha" nos parametros enviados na querystring, junto com o arquivo--}%
        this.params.abaDaPlanilha = document.getElementById("inputAbaDaPlanilha").value;
    </uploader:onSubmit>
    <uploader:onProgress>
        jQuery.ajax({
            type:'POST',
            url:'./progressoUpload',
%{-- TODO: Se um erro retornar do servidor, cancelar o upload para que o usuário não fique esperando atoa.
            error:function(XMLHttpRequest,textStatus,errorThrown){
                $('a.qq-upload-cancel').click();
            },
            success:function(data,textStatus){
            },

--}%
            data: {loaded: loaded, total: total}
        });
    </uploader:onProgress>
    <uploader:onComplete>
        if (responseJSON.success) {
            window.location.assign('<g:createLink action="create2"/>');
    %{-- Usado com webflow
    window.location.assign('<g:createLink controller="fluxoImportacaoFamilias" action="fluxoImportacao"/>' + '?idImportacao='+responseJSON.idImportacao);
    --}%
        }
    </uploader:onComplete>
</uploader:uploader>

</body>
</html>
