<%@ page import="org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia; org.apoiasuas.AncestralController" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
  		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		<g:layoutHead/>
		<r:layoutResources/>

    <style type="text/css" media="screen">
    #caixa-familia {
        background: none repeat scroll 0% 0% #E1F2B6;
        border: 1px solid #83955c;
        /*color: #83955c;*/
        /*box-shadow: 1px 1px 0.5em #9cb26e;*/
        font-size: 0.8em;
        line-height: 1.5;
        /*margin: 0.5em 0.5em;*/
        padding: 0.25em 1em;
        float: right;
    }
    </style>

</head>
	<body>
    %{--   Muda a cor do banner de acordo com o ambiente:   --}%
    <div role="banner" id= ${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "grailsLogoProd" : org.apoiasuas.util.AmbienteExecucao.isValidacao() ? "grailsLogoValid" : "grailsLogoLocal"}>
        <table class="vertical-align: middle"><tr>
%{--
            <td> Test ${servletContext.getInitParameter("nomeEquipamento")} e <a href="${createLink(controller: "inicio", action: "menu")}"><asset:image src="apoiasuas_logo.png" alt="Grails"/></a>  </td>
--}%
            <td><a href="${createLink(controller: "inicio", action: "menu")}"><asset:image src="apoiasuas_logo.png" alt="Grails"/></a> <span style="font-size:40px">${application.configuracao ? application.configuracao.equipamento?.nome : "indefinido" }</span> </td>
            <td> %{--  Exibe a última família / cidadão selecionado, se houver:   --}%
                <%
                    Familia ultimaFamilia = session[org.apoiasuas.AncestralController.ULTIMA_FAMILIA]
                    Cidadao ultimoCidadao = session[org.apoiasuas.AncestralController.ULTIMO_CIDADAO]
                %>
                <g:if test="${ultimaFamilia != null}">
                    <div id="caixa-familia">
                        <g:link controller="familia" action="show" id="${ultimaFamilia.id}">Cad ${ultimaFamilia.codigoLegado}</g:link>
                        <br>
                        <g:if test="${ultimoCidadao != null}">
                            <g:link controller="cidadao" action="show" id="${ultimoCidadao.id}">${ultimoCidadao.nomeCompleto}</g:link>
                        </g:if>
                        &nbsp;
                    </div>
                </g:if>
            </td>
        </tr></table>
    </div>


    <g:layoutBody/>
		<div class=${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "footerProd" : org.apoiasuas.util.AmbienteExecucao.isValidacao() ? "footerValid" : "footerLocal"} role="contentinfo">
        <form name="logout" method="POST" action="${createLink(controller: 'logout')}">
            <sec:ifLoggedIn>
                <asset:image src="operador.png" alt="Operador" height="20" width="20"/> <sec:loggedInUserInfo field="username"/>
                <g:link controller="logout" style="color: black">(sair)</g:link>
            </sec:ifLoggedIn>
        </form>
        </div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		<r:layoutResources/>
	</body>
</html>
