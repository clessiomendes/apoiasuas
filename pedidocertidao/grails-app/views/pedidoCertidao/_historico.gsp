<%@ page import="org.apoiasuas.util.Modulos; org.apoiasuas.pedidocertidao.PedidoCertidao" %>
<%
    PedidoCertidao pedidoDTO = pedidoInstance
%>

<g:render template="/mensagensPosGravacao" model="[bean: pedidoDTO]" plugin="${Modulos.NUCLEO}"/>

<div class="situacao-atual">
       <asset:image src="${pedidoDTO.situacao.icone}"/>
       <span title="Situação atual do pedido de certidão" style="margin-right: 30px">${pedidoDTO.situacao.descricao}</span>
       <tmpl:botaoAcao acao="${PedidoCertidao.Situacao.DESFAZER}"
                       classeCss="speed-button-desfazer"
                       title="Retornar à situação anterior do pedido"/>
</div>
<br>

<table class="tabelaListagem">
    <col width = "10px"/>
    <col width = "150px"/>
    <thead><tr>
        <th>Data</th>
        <th>Operador</th>
        <th></th>
    </tr></thead>

    <tbody>
        <g:each in="${pedidoDTO.historico.sort { it?.id }}" var="historico" status="i">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><g:formatDate date="${historico.dataHora}"/></td>
                <td>${historico.operador?.username}</td>
                <td>
                    <g:if test="${historico.acao}">
                        <asset:image style="width: 16px; height: 16px" src="${historico.acao?.icone}"/>
                    </g:if>
                    ${historico.descricao}
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

<%
//    pedidoDTO.situacao.acoesPossiveis;
//    System.out.println(pedidoDTO.situacao);
//    System.out.println(pedidoDTO.situacao.descricao);
//    System.out.println(pedidoDTO.situacao.acoesPossiveis*.acao.join(","));
//    System.out.println(pedidoDTO.situacao.acoesPossiveis.contains(PedidoCertidao.Situacao.PEDIDO_ENVIADO));
%>

<input type="button" style="background-image: url(${assetPath(src: 'usecases/comentario.png')})"
      class="image-button-acoes" action="inserirComentario" title="Adicionar uma informação no histórico do pedido"
      value="Inserir Comentário" onclick="habilitarComentario();" />

<span id="spanComentario" style="display: none">
    <form style="display: inline-block">
        <g:textField id="txtComentario" name="descricao" size="60" onkeydown="return txtComentarioKeyDown(event);"/>
        <g:submitToRemote elementId="btnEnviarComentario" class="speed-button-check" action="inserirComentario" id="${pedidoDTO.id}"
                          title="Registrar comentário" update="tabHistorico" before='if (! preEnviarComentario()) return false;'/>
        <input id="btnCancelarComentario" type="button" class="speed-button-undo" title="Cancelar" onclick="habilitarComentario();" />
    </form>
    <br>
</span>

<tmpl:botaoAcao acao="${PedidoCertidao.Situacao.PEDIDO_ENVIADO}" />
<tmpl:botaoAcao acao="${PedidoCertidao.Situacao.CERTIDAO_RECEBIDA}" />
<tmpl:botaoAcao acao="${PedidoCertidao.Situacao.CERTIDAO_ENTREGUE}" />
<tmpl:botaoAcao acao="${PedidoCertidao.Situacao.PEDIDO_CANCELADO}" before="if (! confirm('Confirma cancelamento?')) return false;"/>

