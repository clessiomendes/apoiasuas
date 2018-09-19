<%
	org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance
%>

<div id="create-monitoramento" class="content scaffold-create" role="main">

	<g:render template="/mensagensPosGravacao" model="[bean: localDtoMonitoramento]"/>

	<g:formRemote url="[action:'saveMonitoramento']" name="saveMonitoramento"
				  onFailure="onError(XMLHttpRequest);"
                  onSuccess="janelaModalMonitoramentos.confirmada();">
		<fieldset class="form">
            <g:render template="monitoramento/formMonitoramento"/>
		</fieldset>
		<fieldset class="buttons">
			<g:submitButton name="create" class="save" value="Gravar" />
			<input type="button" class="cancel" onclick="janelaModalMonitoramentos.cancelada();" value="Cancelar" />
		</fieldset>
	</g:formRemote>
</div>
