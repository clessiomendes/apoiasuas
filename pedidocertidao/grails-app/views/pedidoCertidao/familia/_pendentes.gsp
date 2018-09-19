%{--Exibe pedidos de certidão pendentes (VERSÃO 2.0 do módulo de pedidos de certidão) --}%

<%@ page import="org.apoiasuas.pedidocertidao.PedidoCertidao" %>
<g:if test="${pedidos}">
	<ul class="errors" role="alert">
	<g:each in="${pedidos}" var="pedido">
		<% PedidoCertidao pedidoDTO = pedido %>
		<li><g:link controller="pedidoCertidao" action="edit" id="${pedidoDTO.id}">Pedido de certidão em <g:formatDate date="${pedidoDTO.dateCreated}" />, situação: ${pedidoDTO.situacao.descricao}</g:link></li>
	</g:each>
	</ul>
</g:if>
