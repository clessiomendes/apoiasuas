<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 04/06/2015
  Time: 02:26
--%>

<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Status</title>
    <meta name="layout" content="main" />
    <style type="text/css" media="screen">

    #nav {
        margin-top:20px;
        margin-left:30px;
        width:228px;
        float:left;

    }
    .homePagePanel * {
        margin:0px;
    }
    .homePagePanel .panelBody ul {
        list-style-type:none;
        margin-bottom:10px;
    }
    .homePagePanel .panelBody h1 {
        text-transform:uppercase;
        font-size:1.1em;
        margin-bottom:10px;
    }
    h2 {
        margin-top:15px;
        margin-bottom:15px;
        font-size:1.2em;
    }
    #pageBody {
        margin-left:280px;
        margin-right:20px;
    }
    </style>
</head>

<body>
<div id="nav">
    <div class="homePagePanel">
        <div class="panelTop">

        </div>
        <div class="panelBody">
            <h1>Application Status</h1>
            <ul>
                <li>App version: <g:meta name="app.version"></g:meta></li>
                <li>Grails version: <g:meta name="app.grails.version"></g:meta></li>
                <li>JVM version: ${System.getProperty('java.version')}</li>
                <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
                <li>Domains: ${grailsApplication.domainClasses.size()}</li>
                <li>Services: ${grailsApplication.serviceClasses.size()}</li>
                <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
            </ul>
            <h1>Installed Plugins</h1>
            <ul>
                <g:set var="pluginManager"
                       value="${applicationContext.getBean('pluginManager')}"></g:set>

                <g:each var="plugin" in="${pluginManager.allPlugins}">
                    <li>${plugin.name} - ${plugin.version}</li>
                </g:each>

            </ul>
        </div>
        <div class="panelBtm">
        </div>
    </div>


</div>
<div id="pageBody">
    <h2>Parâmetros de instalação:</h2>
    <ul>
        <li>Fornecedor de banco de dados: ${org.apoiasuas.util.AmbienteExecucao.forncedorBancoDados}</li>
        <li>Ambiente de hospedagem: ${org.apoiasuas.util.AmbienteExecucao.ambienteHospedagem}</li>
        <li>Ambiente de execução: ${org.apoiasuas.util.AmbienteExecucao.ambienteExecucao}</li>
        <li>Versão: <g:render template="versao"/></li>
        <li>Rodando desde: <g:formatDate format="dd/MM/yyyy HH:mm:ss" date="${org.apoiasuas.util.AmbienteExecucao.inicioAplicacao}"/></li>
    </ul>

    <sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.SUPER_USER}">
    <div id="controllerList" class="dialog">
        <h2>Atualizações de Banco de Dados Pendentes:</h2>
        <ul>
            <g:each var="c" in="${atualizacoesPendentesBD}">
                <li class="controller">${c}</li>
            </g:each>
        </ul>
        <h2>Controllers disponíveis:</h2>
        <ul>
            <g:each var="c" in="${grailsApplication.controllerClasses}">
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
            </g:each>
        </ul>
        <h2>Beans disponíveis:</h2>
        <ul>
            <g:each var="c" in="${grailsApplication.mainContext.beanDefinitionNames}">
                <li class="controller">${c}</li>
            </g:each>
        </ul>
    </div>
    </sec:ifAnyGranted>
</div>
</body>
</html>