<%@ page import="org.apoiasuas.redeSocioAssistencial.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Rede intersetorial e socioassistencial</title>
        <asset:stylesheet src="servico/servico.less"/>
	</head>
	<body>
		<a href="#list-servico" class="skip" tabindex="-1">Skip to content&hellip;</a>
        <div id="list-servico" class="content scaffold-list" role="main">

		<h1>Procurar - Rede intersetorial e socioassistencial<span class="hide-on-mobile"> (serviços, benefícios e programas)</span></h1>

		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create">Incluir novo</g:link></li>
			</ul>
		</div>

<g:form class="pesquisar">

    <span class="campo">
        <span class="titulo">Palavra chave</span>
        <g:textField name="palavraChave" size="20" autofocus="" value="${filtro?.nome}"/>
    </span>

	<g:actionSubmit action="list" class="search" value="Procurar" onclick="this.form.action='${createLink(action:'list')}';"/>

</g:form>

            <g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="tabelaListagem">
                <thead><tr>
                    <th></th>
                    <th>Nome popular</th>
                    <th>Descrição</th>
                </tr></thead>
				<tbody>
				<g:each in="${servicoInstanceList}" status="i" var="servicoInstance"> <% Servico servico = servicoInstance %>
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>
							<g:if test="${servico?.imagemFileStorage}">
                            <g:link action="show" id="${servico.id}">
								<img class="avatar-servico" src="${g.createLink(action: 'imagem', params: [imagemFileStorage: servico?.imagemFileStorage])}"/>
							</g:link>
							</g:if>
						</td>
						%{--<td><asset:image src="config.png" width="40" height="40"/></td>--}%
						<td>
                            <g:link action="show" id="${servico.id}">${raw(servico.apelido)}</g:link>
                            <span class="desativado">${servico.habilitado ? "" : " (desativado)"}</span>
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
