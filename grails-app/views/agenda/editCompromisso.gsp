<%@ page import="org.apoiasuas.agenda.Compromisso" %>
<%
	org.apoiasuas.agenda.Compromisso localDtoCompromisso = compromissoInstance
%>
<style>
#btnApagarCompromisso {
	display: none;
}
</style>

<script>
    function apagarCompromisso() {
        if (confirm("Confirma remoção deste compromisso?"))
            btnApagarCompromisso.click();
    }
</script>

<div id="edit-compromisso" class="content scaffold-create" role="main">
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

	<g:form>
		<g:hiddenField name="version" value="${localDtoCompromisso?.version}" />
		<fieldset class="form">
            <g:render template="formCompromisso"/>
		</fieldset>
		<fieldset class="buttons">
	%{--o parametro oculto "data" contem o retorno do post (no nosso caso, um array json contendo o compromisso recem gravado--}%
		<g:submitToRemote url="[action:'saveCompromisso',id:localDtoCompromisso?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="save" value="Gravar"/>

		%{--O botão btnApagarCompromisso, do tipo submitToRemote, está invisível para ser substituido na tela pelo botão abaixo, que chama uma função javascript
		 antes de fazer o submit do form--}%
        <input type="button" class="delete" value="Apagar" onclick="apagarCompromisso();"/>
		<g:submitToRemote url="[action:'deleteCompromisso',id:localDtoCompromisso?.id]" onSuccess="sucessoDelete(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  id="btnApagarCompromisso"/>

		<input type="button" class="close" onclick="janelaModal.cancelada();" value="Fechar" />
	</g:form>
</div>
