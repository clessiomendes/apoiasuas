<%
	List<org.apoiasuas.pedidocertidao.PedidoCertidao> pedidosListDTO = pedidosList
%>

<%@ page import="org.apoiasuas.util.StringUtils; org.apoiasuas.pedidocertidao.PedidoCertidao" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Pedidos de Certidão</title>
	</head>
	<body>
		<a href="#list-link" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<h1>Pedidos de Certidão - procurar</h1>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create">Nova Demanda</g:link></li>
			</ul>
		</div>
		<div id="list-link" class="content scaffold-list" role="main">
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			<g:form class="pesquisar">
				<g:render template="filtrosProcurar"/>

%{--
				<g:link onclick="linkProcurarCidadao(this, '${createLink(action: 'list')}');">
					<input id="btnProcurarCidadao" type="button" class="search" value="Procurar"/>
				</g:link>
--}%

    	        <g:submitButton formaction="list" name="list" id="search" class="search" value="Procurar"/>
				<div class="expandir"><asset:image src="slidedown.png" onclick="expandirFiltros(this);"/></div>
			</g:form>

			<table class="tabelaListagem">
			<thead>
					<tr>
                        %{--<th>Situação atual</th>--}%
                        <th></th>
                        <th>Data</th>
                        <th class="hide-on-mobile" >Responsável</th>
                        <th>Nome</th>
                        <th class="hide-on-mobile" >Local Cartório</th>
                        <th class="hide-on-mobile" >Cad</th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${pedidosListDTO}" status="i" var="pedidoInstance">
					<% PedidoCertidao pedido = pedidoInstance %>
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						%{--<td>${pedido.situacao?.descricao}</td>--}%
						<td style="padding-bottom: 0"><asset:image style="width: 24px; height: 24px" title="${pedido.situacao.descricao}" src="${pedido.situacao.icone}"/></td>
						<td>${pedido.dateCreated?.format("dd/MM/yyyy")}</td>
						<td class="hide-on-mobile" >${pedido.operadorResponsavel?.username}</td>
						<td><g:link action="edit" id="${pedidoInstance.id}">${pedido.nomeRegistro}</g:link></td>
						<td class="hide-on-mobile" >${StringUtils.concatena(", 	",pedido.bairroCartorio, pedido.municipioCartorio, pedido.ufCartorio)}</td>
						<td class="hide-on-mobile" >${pedido.familia?.cad}</td>
					</tr>
				</g:each>
				</tbody>
			</table>

			<g:if test="${pedidosCount == 0}">
				<div style="margin: 10px">Nenhum resultado encontrado.</div>
			</g:if>

			<div class="pagination">
				<g:paginate params="${pageScope.filtro}" total="${pedidosCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
