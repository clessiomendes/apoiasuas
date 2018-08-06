<%@ page import="org.apoiasuas.marcador.Marcador" %>
<h3 class="header-gestao-tecnica"><g:message code="titulo.monitoramentos"/></h3>
<g:each in="${monitoramentos}" var="entryMonitoramento">
    <div class="conteudo-gestao-tecnica">
        <g:if test="${entryMonitoramento.value > 0}">
        <g:link elementId="moitoramento-${entryMonitoramento.key}" title="clique para ver a relação detalhada de monitoramentos" action="listarMonitoramentos" params="[filtroPadrao: entryMonitoramento.key, idTecnico: idTecnico]">
            ${entryMonitoramento.key.label}: ${entryMonitoramento.value}
        </g:link>
        </g:if>
        <g:else>
            ${entryMonitoramento.key.label}: ${entryMonitoramento.value}
        </g:else>
    </div>
</g:each>
<br>
<div class="total-gestao-tecnica">
    <g:link title="clique para ver a relação detalhada de monitoramentos" action="listarMonitoramentos" params="[idTecnico: idTecnico]">
        <b>Total ${totalMonitoramentos}</b>
    </g:link>
</div>
