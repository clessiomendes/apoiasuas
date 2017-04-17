<%@ page import="org.apoiasuas.cidadao.MonitoramentoService"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Monitoramentos</title>
    <asset:javascript src="especificos/apoiasuas-modal.js"/>
</head>

<body>

<g:javascript>
    var janelaModal = new JanelaModalAjax(updateList);

/*
    $(document).ready(function() {
		janelaModal = new JanelaModalAjax(updateList);
    } );
*/

    function updateList() {
        console.debug("recarregando");
        location.href = "${createLink(action: 'listarMonitoramentos', params: [situacao: situacao, idTecnico: idTecnico])}";
    };
</g:javascript>

<h1>${tituloListagem}</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<div id="list-monitoramento" class="content scaffold-list" role="main">

    <table class="tabelaListagem">
        <thead><tr>
            <th>Descrição</th>
            <th>Família</th>
        </tr></thead>

        <tbody>
        <g:each in="${monitoramentosInstanceList}" status="i" var="monitoramentoInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela("ver monitoramento","${createLink(controller: 'familia', action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                    ${monitoramentoInstance.memoCortado}
                </a></td>
                <td>
                    <g:link controller="familia" action="show" id="${monitoramentoInstance?.familia?.id}">
                        ${monitoramentoInstance?.familia?.montaDescricao().encodeAsHTML()}
                    </g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <g:if test="${monitoramentosInstanceList.size() >= MonitoramentoService.MAX_MONITORAMENTOS_LISTAGEM}">
        <ul class="errors" role="alert"> <li>
                - Atenção! Apenas os ${MonitoramentoService.MAX_MONITORAMENTOS_LISTAGEM} primeiros monitoramentos foram exibidos.
        </li> </ul>
    </g:if>
%{--
    <div class="pagination">
        <g:paginate params="${pageScope.filtro}" total="${cidadaoInstanceCount ?: 0}"/>
    </div>
--}%
</div>

</body>
</html>
