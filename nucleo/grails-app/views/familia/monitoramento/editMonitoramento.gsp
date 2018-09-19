<%
    org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance
%>


<div id="edit-monitoramento" class="content scaffold-edit" role="main">

	<g:render template="/mensagensPosGravacao" model="[bean: localDtoMonitoramento]"/>

    <g:formRemote url="[action:'saveMonitoramento', id: localDtoMonitoramento.id]" name="saveMonitoramento"
                  onFailure="janelaModalMonitoramentos.loadHTML(XMLHttpRequest.responseText);"
                  onSuccess="janelaModalMonitoramentos.confirmada();">
        <g:hiddenField name="version" value="${localDtoMonitoramento?.version}" />

        <fieldset class="form">
            <g:render template="monitoramento/formMonitoramento"/>
        </fieldset>

        <fieldset class="buttons">
            <g:submitButton name="update" class="save" value="Gravar" />
            <input type="button" class="cancel" onclick="janelaModalMonitoramentos.cancelada();" value="Cancelar" />
        </fieldset>

    </g:formRemote>
</div>

