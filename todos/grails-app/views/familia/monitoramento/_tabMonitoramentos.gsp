<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
%>

<g:javascript>
    var janelaModalMonitoramentos = new JanelaModalAjax();

/*
    $(document).ready(function() {
		//updateList();
		janelaModalMonitoramentos
    } );
*/

    function createMonitoramento() {
        janelaModalMonitoramentos.abreJanela({titulo: "Criar novo Monitoramento", refreshFunction: updateList,
                url: "${createLink(action:'createMonitoramento', params: [idFamilia: localDtoFamilia.id])}"});
    }

    function updateList() {
        $("#divListMonitoramento").html('<asset:image src="loading.gif"/> carregando...');
        ${remoteFunction(action:'listMonitoramento', id: localDtoFamilia.id,
                update: [success: 'divListMonitoramento', failure: 'divListMonitoramento'],
                onFailure: 'alert("Erro carregando lista de monitoramentos (via ajax)");'
        )};
    }

</g:javascript>

<input id="btnNovoMonitoramento" type="button" class="create" value="Novo monitoramento" style="margin: 5px" onclick="createMonitoramento();"/>

<div id="divListMonitoramento"/>
    <g:render template="monitoramento/listMonitoramento" model="[monitoramentoInstanceList: localDtoFamilia.monitoramentos?.sort()]"/>
</div>
