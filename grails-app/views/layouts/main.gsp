<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.CidadaoController; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia; org.apoiasuas.AncestralController" %>
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
        max-width: 400px;
        /*color: #83955c;*/
        /*box-shadow: 1px 1px 0.5em #9cb26e;*/
        font-size: 0.8em;
        line-height: 1.5;
        /*margin: 0.5em 0.5em;*/
        padding: 0.25em 1em;
        float: right;
    }
    #textoBanner, #textoBanner a:hover, #textoBanner a:visited, #textoBanner a:link, #textoBanner a:active {
        text-align: justify;
        text-indent: 0;
        font-family: "Georgia", serif;
        font-weight: bold;
        color: white;
        line-height: 0.9em;
        margin-top: 10px;
        text-decoration: none !important;
        border:0!important;
    }
    #cabecalho tr:hover {
        background: none;
    }
    #imgLogo {
        float: left;
        margin: 0 10px 0 0;
        width: 50px;
        height: 50px;
    }

    </style>

</head>
	<body>

    <g:render template="/layouts/notificacoes"></g:render>

    %{--   Muda a cor do banner de acordo com o ambiente:   --}%
    <div role="banner" id= ${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "grailsLogoProd" : org.apoiasuas.util.AmbienteExecucao.isValidacao() ? "grailsLogoValid" : "grailsLogoProd"}>
        <table id="cabecalho" class="vertical-align: middle"><tr>
            %{--<td><a href="${createLink(controller: "inicio", action: "menu")}"><asset:image src="apoiasuas_logo.png" alt="Grails"/></a> <span style="font-size:30px">${application.configuracao ? application.configuracao.nome : "indefinido" }</span> </td>--}%
            <td>
                <a href="${createLink(controller: "inicio", action: "menu")}"><asset:image id="imgLogo" src="suas-2.png" alt="Apoia CRAS"/></a>
                <p id="textoBanner">
                    <span style="font-size: 30px;">APOIA CRAS</span><br/>
                    <span style="font-size: 15px;"><sec:loggedInUserInfo style="font-size:20px;" field="servicoSistemaSessaoCorrente.nome"/></span>
                </p>
            </td><td> %{--  Exibe a última família / cidadão selecionado, se houver:   --}%
                <%
                    Familia ultimaFamilia = org.apoiasuas.cidadao.FamiliaController.getUltimaFamilia(session)
                    Cidadao ultimoCidadao = org.apoiasuas.cidadao.CidadaoController.getUltimoCidadao(session)
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
