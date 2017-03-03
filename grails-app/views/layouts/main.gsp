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
    <asset:javascript src="especificos/notify.js"/>
    <asset:stylesheet src="especificos/notificacoes.less"/>
    <asset:stylesheet src="especificos/header.less"/>
    <g:if env="development">
        <asset:stylesheet src="globais/development-utils.less"/>
    </g:if>

    <g:layoutHead/>
    <r:layoutResources/>

</head>
<body>

    <g:render template="/layouts/notificacoes"></g:render>

    %{--   Muda a cor do banner de acordo com o ambiente:   --}%
    <div role="banner" id= "grailsLogoProd"}>
    %{--<div role="banner" id= ${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "grailsLogoProd" : org.apoiasuas.util.AmbienteExecucao.isValidacao() ? "grailsLogoValid" : "grailsLogoLocal"}>--}%
        <table id="cabecalho"><tr>
            %{--<td><a href="${createLink(controller: "inicio", action: "menu")}"><asset:image src="apoiasuas_logo.png" alt="Grails"/></a> <span style="font-size:30px">${application.configuracao ? application.configuracao.nome : "indefinido" }</span> </td>--}%
            <td>
                <a id="textoBanner" href="${createLink(controller: "inicio", action: "menu")}"><asset:image id="imgLogo" src="suas.png" alt="Apoia CRAS"/>
                        <h1>APOIA CRAS</h1>
                        <h2><sec:loggedInUserInfo field="servicoSistemaSessaoCorrente.nome"/></h2>
                </a>
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

    <div class="footerProd">
    %{--<div class=${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "footerProd" : org.apoiasuas.util.AmbienteExecucao.isValidacao() ? "footerValid" : "footerLocal"} role="contentinfo">--}%
        <form name="logout" method="POST" action="${createLink(controller: 'logout')}">
            <sec:ifLoggedIn>
                <asset:image src="operador.png" alt="Operador" height="20" width="20"/> <sec:loggedInUserInfo field="username"/>
                <g:link controller="logout" style="color: black">(sair)</g:link>
            </sec:ifLoggedIn>
            <span style="color: black; float: right" >
                Créditos:
                <a style="color: black" target="_blank" href="http://icons8.com">Icons8</a>
                <a style="color: black" target="_blank" href="http://grails.org">Grails</a>
            </span>
        </form>
    </div>
    %{--<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>--}%
    <r:layoutResources/>
</body>
</html>
