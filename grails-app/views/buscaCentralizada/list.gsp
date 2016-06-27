<%@ page import="org.apoiasuas.redeSocioAssistencial.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="buscaCentralizado.titulo" /></title>
	</head>
	<body>
		%{--<a href="#list-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>--}%

%{--
    <fieldset class="buttons">
        <g:link class="create" action="create">Incluir novo serviço</g:link>
        <g:actionSubmit class="list" action="list" value="Procurar" />
    </fieldset>
--}%

    <div id="list-busca" class="content scaffold-list" role="main">
		<h1><g:message code="buscaCentralizado.titulo" /></h1>

        <div class="nav" role="navigation">
            <g:form action="list">
                <ul>
                    <li><g:textField name="palavraChave" size="40" autofocus="" value="${filtro?.palavraChave}"/></li>
                    <li><g:submitButton name="list" class="list" value="Procurar"/></li>
                </ul>
            </g:form>
        </div>

        <g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
		</g:if>
		<table>
			<tbody>
			<g:each in="${resultadoDTO?.objetosEncontrados}" status="i" var="objetoEncontrado">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td>
                        ${raw(objetoEncontrado.url + " ("+objetoEncontrado.tipo+")")}<br>
                        ${raw(objetoEncontrado.detalhes?.toString() ?: "")}
					</td>
				</tr>
			</g:each>
			</tbody>
		</table>
        <g:if test="${resultadoDTO?.total > resultadoDTO?.objetosEncontrados?.size()}">
            <div class="errors" role="status">Mostrando apenas os primeiros ${resultadoDTO.objetosEncontrados.size()} registros de um total de ${resultadoDTO.total} encontrados.</div>
        </g:if>
		</div>
	</body>
</html>
