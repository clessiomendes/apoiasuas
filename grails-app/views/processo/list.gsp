
<%@ page import="org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'task.label', default: 'Tarefas')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-task" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
        <g:form action="listaPendentes">
        %{--
                    <ul style="margin-bottom: 0.5em">
                        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                        <sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.STR_SUPER_USER}">
                        </sec:ifAnyGranted>
                    </ul>
        --}%
            <ul>
                <li>
                    Tipo <g:select name="definicicaoProcesso" noSelection="${['':'']}" from="${definicoesProcessoDisponiveis.collect{it.name}}" keys="${definicoesProcessoDisponiveis.collect{it.key}}"/>
                </li>
                <li>
                    Operador <g:select name="usuarioSistema" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}" keys="${ususariosDisponiveis.collect{it.id}}"/>
                </li>
                <li><g:submitButton name="list" class="list" value="Procurar"/></li>
            </ul>
        </g:form>
        </div>
		<div id="list-task" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="tabelaListagem">
			<thead>
					<tr>
						<g:sortableColumn property="usuarioSistema" title="Operador" />
                        <g:sortableColumn property="processo" title="Descrição" />  %{--Definicao do processo--}%
						<g:sortableColumn property="descricao" title="Situação atual" />  %{--Descricao da tarefa--}%
						<g:sortableColumn  property="criacao" title="Data" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${tarefas}" status="i" var="tarefa">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td>${tarefa.responsavel?.username}</td>
                        <td><g:link action="mostraProcesso" id="${tarefa.processo.id}">${tarefa.processo.descricao}</g:link></td>
                        <td>${tarefa.descricao}</td>
                        <td><g:formatDate date="${tarefa.inicio}"/></td>
					</tr>
				</g:each>
				</tbody>
			</table>
%{--
			<div class="pagination">
				<g:paginate total="${linkInstanceCount ?: 0}" />
			</div>
--}%
		</div>
	</body>
</html>
