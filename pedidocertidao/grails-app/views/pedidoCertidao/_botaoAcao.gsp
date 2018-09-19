<%@ page import="org.apoiasuas.pedidocertidao.PedidoCertidao" %>
<%
    org.apoiasuas.pedidocertidao.PedidoCertidao pedidoDTO = pedidoInstance
%>
<g:if test="${pedidoDTO.situacao.acoesPossiveis.contains(acao)}">
       <g:submitToRemote class="${classeCss ?: 'image-button-acoes'}"
              style="background-image: url(${assetPath(src: acao.icone)})"
              before="${before}" title="${title}" update="tabHistorico"
              url="${[action: 'acaoPedido', id:pedidoDTO.id, params:[acao: acao.name()]]}"
              value="${acao.acao}" />
</g:if>
