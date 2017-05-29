<%@ page import="org.apoiasuas.cidadao.Monitoramento" %>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<table class="tabelaListagem">
    <thead><tr>
        <th>Data</th>
        <th>Descrição</th>
        <th></th>
        %{--<th>Situação</th>--}%
    </tr></thead>
    <tbody>
    <g:each in="${monitoramentoInstanceList}" status="i" var="monitoramentoInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela("ver monitoramento","${createLink(action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                <g:formatDate date="${monitoramentoInstance.dataCriacao}"/>
            </a></td>
            <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela("ver monitoramento","${createLink(action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                ${monitoramentoInstance.memoCortado}
            </a></td>
            <td><input type="button" class="${monitoramentoInstance.iconeSituacao}" title="${monitoramentoInstance.situacao}"/></td>
%{--
            <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela("ver monitoramento","${createLink(action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                ${monitoramentoInstance.situacao}
            </a></td>
--}%
        </tr>
    </g:each>
    </tbody>
</table>
