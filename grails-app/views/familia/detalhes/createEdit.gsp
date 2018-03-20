<%@ page import="org.apoiasuas.cidadao.FamiliaDetalhadoController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>

<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>

<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title>Detalhes da Família</title>
	    <asset:stylesheet src="familia/detalhes/familia-detalhado.less"/>
	    <asset:stylesheet src="animate.css"/>
	    <asset:javascript src="familia/detalhes/familia-detalhado.js"/>
	    <asset:stylesheet src="jquery.clearField/jquery.clearField.css"/>
	    <asset:javascript src="jquery.clearField/jquery.clearField.js"/>
		<asset:javascript src="familia/telefone/formTelefones.js"/>
		<asset:stylesheet src="familia/telefone/formTelefones.less"/>
	</head>
	<body>

        <script>
            //variaveis globais para serem usadas pelo javascript familia-detalhado.js
            var imgVerMais = '${assetPath(src: 'down-w.png')}';
            var imgVerMenos = '${assetPath(src: 'up-w.png')}';
            var imgVerMaisTodos = 'url(${assetPath(src: 'double-down-w.png')})';
            var imgVerMenosTodos = 'url(${assetPath(src: 'double-up-w.png')})';
            var actionEscolherFormulario = "${createLink(controller: 'emissaoFormulario', action: 'escolherFormulario')}";
            var actionDowloadCadastro = "${createLink(controller: 'familiaDetalhado', action: 'download', id: localDtoFamilia?.id)}";

            $(document).ready(function(){
                //          Mostra controles aa partir do modo. obs: manter embargado na gsp por conta das tags gsp do grails
                <g:if test="${modoEdicao}" >
                  ModoInicializacao.edicao(${idCidadao ?: ''});
                </g:if>
                <g:elseif test="${modoCriacao}" >
                  ModoInicializacao.criacao();
                </g:elseif>
                <g:elseif test="${modoContinuarCriacao}" >
                  ModoInicializacao.continuarCriacao();
                </g:elseif>
            });
        </script>

        <g:render template="/familia/detalhes/cabecalho"/>

        <g:render template="/familia/detalhes/corpo"/>

	</body>
</html>
