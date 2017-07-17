<%@ page import="org.apoiasuas.cidadao.MonitoramentoService"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Monitoramentos</title>
</head>

<body>

<g:javascript>
    var janelaModal = new JanelaModalAjax(updateList);

    $(document).ready(function() {
		$("#atrasado").change(changePendentes);
		$("#semPrazo").change(changePendentes);
		$("#dentroPrazo").change(changePendentes);
		$('input[name="situacao"]').click(changeSituacaoPendente);
		//changeSituacaoPendente();
    } );

    function changeSituacaoPendente() {
        if ($("#situacaoPendente").prop("checked"))
            $("#divPendente").show(300)
        else
             $("#divPendente").hide(300)
       //var checked = $(this).prop('checked')
		//$("#atrasado").prop('checked', false)
		//$("#semPrazo").prop('checked', false)
		//$("#dentroPrazo").prop('checked', false)
		//$(this).prop('checked', checked)
    };

    function changePendentes() {
        var checked = $(this).prop('checked')
		$("#atrasado").prop('checked', false)
		$("#semPrazo").prop('checked', false)
		$("#dentroPrazo").prop('checked', false)
		$(this).prop('checked', checked)
    };

    function updateList() {
        console.debug("recarregando");
        document.formMonitoramentos.submit();
        %{--location.href = "${createLink(action: 'listarMonitoramentos', params: [filtroPadrao: filtroPadrao, idTecnico: idTecnico])}";--}%
    };
</g:javascript>

<h1>${tituloListagem}</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<g:form name="formMonitoramentos" action="listarMonitoramentos">
    <table class="parametrosPesquisa">
        <tr>
            <td>
                <div>
                    <fieldset style="display:inline-block; padding: 0 10px; border: 1px solid #CCCCCC; vertical-align:top">
                        <g:radio name="situacao" id="situacaoPendente"  value="pendente" checked="${situacao == 'pendente'}"/>Pendente
                        <g:radio name="situacao" value="efetivado" checked="${situacao == 'efetivado'}"/>Efetivado
                        <g:radio name="situacao" value="suspenso" checked="${situacao == 'suspenso'}"/>Suspenso
                        <g:radio name="situacao" value="todos"  checked="${situacao == 'todos' || situacao == null}"/>Todos
                        <div id="divPendente" style="display: ${situacao == 'pendente' ? 'block' : 'none'}">
                            <g:checkBox name="atrasado" checked="${atrasado != null}"/>atrasado
                            <g:checkBox name="dentroPrazo" checked="${dentroPrazo != null}"/>dentro do prazo
                            <g:checkBox name="semPrazo"  checked="${semPrazo != null}"/>sem prazo
                        </div>
                    </fieldset>
                    <nobr style="margin: 0 10px"><g:checkBox name="prioritario" checked="${prioritario != null}"/>Prioritário</nobr>
                    <nobr>
                        Técnico <g:select name="idTecnico" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}"
                                            keys="${ususariosDisponiveis.collect{it.id}}" value="${idTecnico}"/>
                    </nobr>
                </div>
            </td>
            <td>
                <div>
                    <g:submitButton formaction="listarMonitoramentos" name="listarMonitoramentos" id="search" class="search" value="Procurar"/>
                </div>
            </td>
        </tr>
    </table>
</g:form>

<div id="list-monitoramento" class="content scaffold-list" role="main">

    <table class="tabelaListagem">
        <thead><tr>
            <th>Data</th>
            <th/>
            <th>Descrição</th>
            <th>Família</th>
        </tr></thead>

        <tbody>
        <g:each in="${monitoramentosInstanceList}" status="i" var="monitoramentoInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela({titulo: "ver monitoramento", refreshFunction: updateList,
                    url: "${createLink(controller: 'familia', action:'showMonitoramento', id: monitoramentoInstance.id)}"});'>
                    <g:formatDate date="${monitoramentoInstance.dataCriacao}"/>
                </a></td>
                <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela({titulo: "ver monitoramento", refreshFunction: updateList,
                    url: "${createLink(controller: 'familia', action:'showMonitoramento', id: monitoramentoInstance.id)}"});'>
                    <input type="button" class="${monitoramentoInstance.iconeSituacao}" title="${monitoramentoInstance.situacao}"/>
                </a></td>
                <td><a href="javascript:void(0)" onclick='janelaModal.abreJanela({titulo: "ver monitoramento", refreshFunction: updateList,
                    url: "${createLink(controller: 'familia', action:'showMonitoramento', id: monitoramentoInstance.id)}"});'>
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
