<%@ page import="org.apoiasuas.marcador.Marcador" %>
<script>
    $('#create-marcador').find("#inputDescricaoMarcador").keyup( function() {
        marcadoresSimilaresComDelay(this);
    } );

    /**
     * Copia todos as associacoes marcador-famailia (e eventuais alterações nas mesmas) para o form a ser submetido,
     * para que elas sejam persistidas antes do refresh da lista destes marcadores
     * (obs: não pode ser movido para um arquivo .js porque `$ {fieldsetMarcador}` precisa ser processada na gsp)
     */
    function copiaMarcadoresAlterados() {
        var $elementosClonados = jQuery('${fieldsetMarcador}').find(".clonarEmNovoMarcador").clone();
        $elementosClonados.hide();
        $("#saveMarcador").append($elementosClonados)
    }

    //# sourceURL=createMarcador
</script>

<div id="create-marcador" class="content scaffold-create" role="main">
	<g:if test="${flash.message}">
	    <div class="message" role="status">${flash.message}</div>
	</g:if>

    <g:hasErrors bean="${marcadorInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${marcadorInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
	</g:hasErrors>

%{--
    <g:formRemote url="[action:actionSaveMarcador]" name="saveMarcador"
                  update="[failure: 'janelaNovoMarcador']" onSuccess="fechaJanelaNovoMarcadorEAtualiza();">
        <g:hiddenField name="idFamilia" value="${familiaInstance.id}"/>
--}%
    <g:formRemote url="[action:actionSaveMarcador]" name="saveMarcador"
                  update="[failure: 'janelaNovoMarcador']" onSuccess="fechaJanelaNovoMarcadorEAtualiza();">
        <fieldset class="form">
            <g:hiddenField name="idFamilia" value="${familiaInstance.id}"/>
                <div class="fieldcontain ${hasErrors(bean: marcadorInstance, field: 'memo', 'error')} ">
                    <label for="descricao">
                        Descrição <span class="required-indicator">*</span>
                        <g:helpTooltip>Descrição sucinta para o(a) ${tipoMarcador} sendo criado, e que ficará <b>disponível para ser utilizado(a) em outras famílias</b></g:helpTooltip>
                    </label>
                    <g:textField autocomplete="off" size="40" name="descricao" id="inputDescricaoMarcador" value="${marcadorInstance?.descricao}" autofocus=""/>
                </div>

            %{--Marcadores disponíveis, a serem clonados em fieldsetMarcadoresFiltradosDialog à medida que forem filtrados--}%
            <div id="marcadoresDisponiveisDialog" style="display: none">
                <g:each in="${marcadoresDisponiveis}" var="marcadordisp">
                    <span class="marcadores-similares">
                        <% Marcador marcadorDisponivel = marcadordisp; %>
                        ${marcadorDisponivel.descricao}
                    </span>
                </g:each>
            </div>
        </fieldset>

        <fieldset id="fieldsetMarcadoresFiltradosDialog" class="fieldsetMarcadores" style="display: none">
            <legend>Antes de confirmar, verifique se este(a) ${tipoMarcador} já esta previsto(a) no sistema</legend>
        </fieldset>

		<fieldset class="buttons">
			<g:submitButton name="create" class="save" value="Criar novo" onclick="copiaMarcadoresAlterados();"/>
            <input type="button" id="btnCancelar" value="Cancelar" class="cancel" onclick="fechaJanelaNovoMarcador();"/>
		</fieldset>
	</g:formRemote>
</div>
