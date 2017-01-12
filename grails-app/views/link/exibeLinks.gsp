<%
    List<Link> linkInstanceListDTO = linkInstanceList
%>

<%@ page import="org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'link.label', default: 'Link')}" />
		<title>Atalhos Externos</title>
        <asset:stylesheet src="especificos/exibeLinks.less"/>
	</head>
	<body>

    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="list" action="list">Configurar</g:link></li>
        </ul>
    </div>

    <div id="controller-list" class="content" role="navigation">
		<a href="#list-link" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <h1>Atalhos</h1>
		<div id="list-link" class="content scaffold-list" role="main">
            <ul>
				<g:each in="${linkInstanceListDTO}" status="i" var="linkInstance">
                    <g:if test="${linkInstance?.tipo.isUrl()}">
                        <li><g:link target="new" title="${linkInstance.instrucoes}" url="${linkInstance.urlCompleta}" id="${linkInstance.id}">${fieldValue(bean: linkInstance, field: "descricao")}</g:link></li>
                    </g:if>
                    <g:if test="${linkInstance?.tipo.isFile()}">
                        <li><g:link action="downloadFile" title="${linkInstance.instrucoes}" id="${linkInstance.id}"><g:fieldValue bean="${linkInstance}" field="descricao"/></g:link></li>
                    </g:if>
				</g:each>
            </ul>
		</div>
    </div>
    %{--</div>--}%
	</body>
</html>
