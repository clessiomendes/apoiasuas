
<%@ page import="org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'link.label', default: 'Link')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>

	<body>
		<a href="#show-link" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-link" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list link">
			
				<g:if test="${linkInstance?.url}">
					<li class="fieldcontain">
						<span id="url-label" class="property-label"><g:message code="link.URL.label" /></span>
						<span class="property-value" aria-labelledby="url-label"><g:fieldValue bean="${linkInstance}" field="url"/></span>
					</li>
				</g:if>

				<g:if test="${linkInstance?.fileName}">
					<li class="fieldcontain">
						<span id="fileName-label" class="property-label"><g:message code="link.FILE.label" /></span>
						<span class="property-value" aria-labelledby="fileName-label">
                            <g:link action="downloadFile" id="${linkInstance.id}">
                                <g:fieldValue bean="${linkInstance}" field="fileName"/>
                            </g:link>
						</span>
					</li>
				</g:if>

                <g:if test="${linkInstance?.descricao}">
                    <li class="fieldcontain">
                        <span id="descricao-label" class="property-label"><g:message code="link.descricao.label" default="Descricao" /></span>
                        <span class="property-value" aria-labelledby="descricao-label"><g:fieldValue bean="${linkInstance}" field="descricao"/></span>
                    </li>
                </g:if>

                <g:if test="${linkInstance?.instrucoes}">
                    <li class="fieldcontain">
                        <span id="instrucoes-label" class="property-label"><g:message code="link.instrucoes.label"/></span>
                        <span class="property-value" aria-labelledby="instrucoes-label">
                            ${raw(org.apoiasuas.util.StringUtils.toHtml(linkInstance?.instrucoes))}
                            %{--<g:fieldValue bean="${linkInstance}" field="instrucoes"/>--}%
                        </span>
                    </li>
                </g:if>

				<g:if test="${linkInstance?.compartilhar}">
				<li class="fieldcontain">
					<span id="abrangenciaTerritorial-label" class="property-label"><g:message code="link.compartilhadoCom.label" default="Compartilhado com" /></span>
					<span class="property-value" aria-labelledby="abrangenciaTerritorial-label">
						<g:render template="/abrangenciaTerritorial"/>
					</span>
				</li>
				</g:if>

            </ol>
			<g:form url="[resource:linkInstance, action:'delete']">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${linkInstance}"><g:message code="default.button.edit.label" default="Editar" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Apagar')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Confirma remoção?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
