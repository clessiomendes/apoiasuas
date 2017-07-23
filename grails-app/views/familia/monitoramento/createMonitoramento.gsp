<%
	org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance
%>

%{--
<script>
	function onError(XMLHttpRequest) {
//		console.log(XMLHttpRequest);
		if (XMLHttpRequest.status == 401)
			alert('Não autenticado')
		else
			janelaModal.loadHTML(XMLHttpRequest.responseText);
	}
//# sourceURL=createMonitoramento
</script>
--}%

<div id="create-monitoramento" class="content scaffold-create" role="main">
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>
	<g:hasErrors bean="${localDtoMonitoramento}">
        <ul class="errors" role="alert">
            <g:eachError bean="${localDtoMonitoramento}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
	</g:hasErrors>

	<g:formRemote url="[action:'saveMonitoramento']" name="saveMonitoramento"
				  onFailure="onError(XMLHttpRequest);"
                  onSuccess="janelaModal.confirmada();">
		<fieldset class="form">
            <g:render template="monitoramento/formMonitoramento"/>
		</fieldset>
		<fieldset class="buttons">
			<g:submitButton name="create" class="save" value="Gravar" />
			<input type="button" class="cancel" onclick="janelaModal.cancelada();" value="Cancelar" />
		</fieldset>
	</g:formRemote>
</div>
