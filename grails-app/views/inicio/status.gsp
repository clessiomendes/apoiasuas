<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 04/06/2015
  Time: 02:26
--%>

<%@ page import="org.apoiasuas.util.ApplicationContextHolder; org.apoiasuas.seguranca.DefinicaoPapeis" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Status</title>
    <meta name="layout" content="main" />
    <asset:stylesheet src="especificos/status.less"/>
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
                %{--<li>Uso de banco de dados: ${ocupacaoBD}</li>--}%
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
        %{--<li>Fornecedor de banco de dados: ${fornecedorVersaoBancoDeDados}</li>--}%
        <li>Ambiente de hospedagem: ${org.apoiasuas.util.AmbienteExecucao.ambienteHospedagem}</li>
        <li>Container: ${ApplicationContextHolder.getServletContext().getServerInfo()}</li>
        <li>Ambiente de execução: ${org.apoiasuas.util.AmbienteExecucao.ambienteExecucao}</li>
        <li>Repositorio de arquivos: ${configuracoesRepostiorio}</li>
        <li>Versão: <g:render template="versao"/></li>
        <li>Rodando desde: <g:formatDate format="dd/MM/yyyy HH:mm:ss" date="${org.apoiasuas.util.AmbienteExecucao.inicioAplicacao}"/></li>
        <li>Estado do SO: ${org.apoiasuas.util.SystemUtils.systemStatistics()}</li>
%{--
        <g:if test="${System.getenv().VCAP_SERVICES}">
            <li>Serviços AppFog: ${System.getenv().VCAP_SERVICES}</li>
        </g:if>
--}%
    </ul>

    <sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.STR_SUPER_USER}">
        <div id="logList" class="dialog">
            <h2>Logs:</h2>
        <g:form action="changeLog">
            <g:each var="appender" in="${logAppenders}">
                <li class="controller">
                    ${appender.key} <g:select name="${appender.key}" from="${logLevels}" value="${appender.value}"> </g:select>
                    %{--<g:select autofocus="" name="servicoSistema" noSelection="${['':'']}" from="${servicosDisponiveis.collect{it.nome}}" keys="${servicosDisponiveis.collect{it.id}}" required="" />--}%
                </li>
            </g:each>
            <g:submitButton name="chage" value="Alterar log"/>
        </g:form>
        </div>

    <div id="controllerList" class="dialog">
%{--
        <h2>Atualizações de Banco de Dados Pendentes:</h2>
        <ul>
            <g:each var="c" in="${atualizacoesPendentesBD}">
                <li class="controller">${c}</li>
            </g:each>
        </ul>
--}%
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