<%@ page import="org.apoiasuas.agenda.Compromisso" %>
<%
	org.apoiasuas.agenda.Compromisso localDtoCompromisso = compromissoInstance
%>

<script>
	/*
	 * Reflete o compromisso recem criado no componente de calendario em calendario.gsp, para ser exibido ao usuario
	 */
	function sucesso(data) {
		janelaModal.confirmada(); //fecha a janela modal
		refreshEvento(data);
//		$('#calendar').fullCalendar('renderEvent', data);
	}
//# sourceURL=createCompromisso
</script>

<div id="create-compromisso" class="content scaffold-create" role="main">
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>
	<g:hasErrors bean="${localDtoCompromisso}">
        <ul class="errors" role="alert">
            <g:eachError bean="${localDtoCompromisso}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
	</g:hasErrors>

	<g:formRemote url="[action:'saveCompromisso']" name="saveCompromisso"
				  onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                  onSuccess="sucesso(data)"> %{--o parametro oculto "data" contem o retorno do post (no nosso caso, um array
                  json contendo o compromisso recem criado--}%
		<fieldset class="form">
            <g:render template="formCompromisso"/>
		</fieldset>
		<fieldset class="buttons">
			<g:submitButton name="create" class="save" value="Gravar" />
			<input type="button" class="cancel" onclick="janelaModal.cancelada();" value="Cancelar" />
		</fieldset>
	</g:formRemote>
</div>
