<%
	org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance
%>

<div id="create-familia" class="content scaffold-create" role="main">
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
                  update="[failure: 'janelaMonitoramento']" onSuccess="fechaJanela();">
		<fieldset class="form">
            <g:render template="formMonitoramento"/>
		</fieldset>
		<fieldset class="buttons">
			<g:submitButton name="create" class="save" value="Gravar" />
		</fieldset>
	</g:formRemote>
</div>
