<%@ page import="org.apoiasuas.redeSocioAssistencial.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servico.label', default: 'Servico')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

%{--
    <fieldset class="buttons">
        <g:link class="create" action="create">Incluir novo serviço</g:link>
        <g:actionSubmit class="list" action="list" value="Procurar" />
    </fieldset>
--}%

    <div id="list-servico" class="content scaffold-list" role="main">
			<h1>Rede sócio-assistencial</h1>

            <g:form style="padding-left: 1em">
                Palavra chave:<g:textField name="palavraChave" size="20" autofocus="" value="${filtro?.nome}"/>
				<g:actionSubmit action="list" class="search" value="Procurar"/>
				<g:actionSubmit action="create" class="create" value="Incluir novo serviço"/>
                %{--<g:link class="create" action="create">Incluir novo serviço</g:link>--}%
            </g:form>

        <g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="tabelaListagem">
                <thead><tr>
                    <th>Nome popular</th>
                    <th>Descrição</th>
                </tr></thead>
				<tbody>
				<g:each in="${servicoInstanceList}" status="i" var="servicoInstance"> <% Servico servico = servicoInstance %>
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
                            <g:link action="show" id="${servico.id}">${raw(servico.apelido)}</g:link>
                            <span style="color:red">${servico.habilitado ? "" : " (desativado)"}</span>
                        </td>
						<td>${raw(servico.descricaoCortada)}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${servicoInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
