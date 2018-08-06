<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>

<div class="pesquisar" style="margin-top: -0.2em;">
    <g:render template="filtrosProcurar"/>

    <input id="btnProcurarCidadao" type="button" class="search" value="Procurar"
            onclick="linkProcurarCidadaoPopup(janelaModalProcurarCidadao, '${createLink(action: actionButtonProcurarPopup, controller: controllerButtonProcurarPopup)}');"/>

    <div class="expandir"><asset:image src="slidedown.png" onclick="expandirFiltros(this);"/></div>

</div>

<div id="list-cidadao" class="content scaffold-list" role="main" style="overflow-y: scroll; overflow-x: hidden; max-height: 400px;">
    <g:if test="${cidadaoInstanceCount}">
        <g:render template="/cidadao/tabelaListagem" model="[popup: true]" />
        <g:custom elemento="spam" showif="${cidadaoInstanceCount > cidadaoInstanceList.size()}">
            obs: exibindo apenas os ${cidadaoInstanceList.size()} primeiros registros de um total de ${cidadaoInstanceCount}. Refine sua busca.
        </g:custom>
    </g:if>
    <g:else>
        <div style="margin: 10px">Nenhum resultado encontrado.</div>
    </g:else>
</div>
