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

	<g:render template="/mensagensPosGravacao" model="[bean: localDtoCompromisso]"/>

	<g:formRemote url="[action:'saveCompromisso']" name="saveCompromisso"
				  onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                  onSuccess="sucesso(data)"> %{--o parametro oculto "data" contem o retorno do post (no nosso caso, um array
                  json contendo o compromisso recem criado--}%
		<fieldset class="form">
            <g:render template="formCompromisso"/>
		</fieldset>
		<fieldset class="buttons">
			<g:submitButton name="create" class="save" value="Gravar"  id="btnGravar"/>
			<input type="button" class="cancel" onclick="janelaModal.cancelada();" value="Cancelar"  id="btnCancelar"/>
		</fieldset>
	</g:formRemote>
</div>
