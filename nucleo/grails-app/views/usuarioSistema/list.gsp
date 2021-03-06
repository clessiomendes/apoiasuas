<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.seguranca.UsuarioSistema" %>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usuarioSistema.label', default: 'Operador do sistema')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-usuarioSistema" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<h1>Procurar - Operadores que utilizam o sistema</h1>

		<div class="nav" role="navigation">
			<ul>
				<sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.STR_SUPER_USER}">
					<li><g:link class="create" action="create">Incluir novo</g:link></li>
				</sec:ifAnyGranted>
			</ul>
		</div>

		<g:form class="pesquisar" action="list">
			<span class="campo">
				<span class="titulo">Nome/login</span>
				<g:textField name="nome" size="20" autofocus=""/>
			</span>

			<span class="campo">
				<span class="titulo">Serviço</span>
				<g:select name="servicoSistema" noSelection="${['':'']}" from="${servicosDisponiveis.collect{it.nome}}" keys="${servicosDisponiveis.collect{it.id}}"/>
			</span>

			<g:submitButton name="list" class="search" value="Procurar"/>
		</g:form>

    </div>

		<div id="list-usuarioSistema" class="content scaffold-list" role="main">

            <g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			<table class="tabelaListagem">
    			<thead>
					<tr>
						<g:sortableColumn property="nomeCompleto" title="${message(code: 'usuarioSistema.nomeCompleto.label', default: 'Nome Completo')}" />
						<g:sortableColumn property="username" title="${message(code: 'usuarioSistema.username.label', default: 'Nome Simplificado')}" />
                        <g:sortableColumn property="servicoSistemaSeguranca.nome" title="${message(code: 'usuarioSistema.servico.label')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${usuarioSistemaInstanceList}" status="i" var="usuarioSistemaInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${usuarioSistemaInstance.id}">${fieldValue(bean: usuarioSistemaInstance, field: "nomeCompleto")}</g:link></td>
						<td>${fieldValue(bean: usuarioSistemaInstance, field: "username")}</td>
                        <td>${fieldValue(bean: usuarioSistemaInstance, field: "servicoSistemaSeguranca.nome")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
            <div class="pagination">
                <g:paginate params="${pageScope.filtro}" total="${usuarioSistemaInstanceCount ?: 0}"/>
            </div>
		</div>
	</body>
</html>
