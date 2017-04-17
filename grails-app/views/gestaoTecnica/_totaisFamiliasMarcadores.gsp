<%@ page import="org.apoiasuas.marcador.Marcador" %>

<g:each in="${familiasMarcadores}" var="entryMarcador">
    <div class="conteudo-gestao-tecnica">
        %{--obs: o id para os links abaixo é útil para identificar o link unicamente no DOM html (por exemplo, para ser acessado nos testes automatizados de tela)--}%
        <g:link elementId="${actionListagem+'-'+entryMarcador.key.id}" title="clique para ver a relação detalhada das famílias" action="${actionListagem}" params="[idMarcador: entryMarcador.key.id, idTecnico: idTecnico]">
            ${entryMarcador.key.descricao}: ${entryMarcador.value}
        </g:link>
    </div>
</g:each>
<br>
<div class="total-gestao-tecnica">
    <g:link elementId="${actionListagem+'-total'}" title="clique para ver a relação detalhada das famílias" action="${actionListagem}" params="[idTecnico: idTecnico]">
        <b>Total ${totalFamiliasMarcadores}</b>
    </g:link>
    <g:helpTooltip>${helpTotal}</g:helpTooltip>
</div>