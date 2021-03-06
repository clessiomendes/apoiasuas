<%@ page import="org.apoiasuas.LinkController; org.apoiasuas.Link" %>

<%
    Link linkDTO = linkInstance
%>

%{--Javascript para a selecao do tipo de link--}%
<g:javascript>
    //Inicialização da página
    jQuery(document).ready(function () {
        $("input[name='tipo']").change(eventoSelecaoTipo).change();
        $("#checkCompartilhar").change(eventoCompartilhar).change();
    });

    function eventoSelecaoTipo() {
        var radioValue = $("input[name='tipo']:checked").val();
        if (radioValue == "${Link.Tipo.FILE.toString()}") {
            $('#tipoFile').removeClass('hidden');
            $('#tipoUrl').addClass('hidden');
        } else if (radioValue == "${Link.Tipo.URL.toString()}") {
            $('#tipoFile').addClass('hidden');
            $('#tipoUrl').removeClass('hidden');
        }
    }

    function eventoCompartilhar() {
        var checked = $(this).prop('checked')
        console.log(checked);
        if (checked == true) {
            $('#compartilhadoCom').removeClass('hidden');
        } else {
            $('#compartilhadoCom').addClass('hidden');
            //$('#compartilhadoCom').hide();
        }
    }
</g:javascript>

<div class="fieldcontain">
	<label for="tipo">
		<g:message code="link.tipo.label"/>
	</label>
    <g:radioGroup name="tipo" values="${Link.Tipo.values()}" labels="${Link.Tipo.values()}" value="${linkDTO.tipo.toString()}" >
        ${it.radio} <g:message code="link.${it.label}.label"/>
    </g:radioGroup>
</div>

<br>

<div id="tipoUrl" class="fieldcontain ${linkDTO.tipo?.isUrl() ? '' : 'hidden'} ${hasErrors(bean: linkDTO, field: 'url', 'error')} ">
    <label for="url">
        <g:message code="link.URL.label" default="Url" />
    </label>
    <g:textField maxlength="250" size="60" name="url" value="${linkDTO?.url}"/>
</div>

<div id="tipoFile" class="fieldcontain ${linkDTO.tipo?.isFile() ? '' : 'hidden'} ${hasErrors(bean: linkDTO, field: 'file', 'error')} ">
    <label>Arquivo</label>
    <g:render template="/fileStorage" model="[fileName: linkDTO.fileName]"/>
</div>

<br>

<div class="fieldcontain ${hasErrors(bean: linkDTO, field: 'descricao', 'error')} ">
	<label for="descricao">
		<g:message code="link.descricao.label" />
	</label>
	<g:textField maxlength="80" size="60" name="descricao" value="${linkDTO?.descricao}"/>
</div>

<div class="tamanho-memo fieldcontain ${hasErrors(bean: linkDTO, field: 'instrucoes', 'error')} ">
    <label for="instrucoes">
        <g:message code="link.instrucoes.label"/>
    </label>
    <g:textArea name="instrucoes" rows="3" value="${linkDTO?.instrucoes}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: linkDTO, field: 'compartihar', 'error')} ">
    <label>Compartilhar com outros serviços?</label>
    <g:checkBox id="checkCompartilhar" name="${LinkController.CHECKBOX_COMPARTILHAR}" value="${linkDTO.compartilhar}"/> sim
</div>

<br>

<div id="compartilhadoCom" class="fieldcontain ${linkDTO.compartilhar ? '' : 'hidden'} class="fieldcontain ${hasErrors(bean: linkDTO, field: 'compartilhadoCom', 'error')}">
    <span id="uf-label" class="property-label"><g:message code="link.compartilhadoCom.label"/></span>
    <span class="property-value" style="margin-left:25%" aria-labelledby="uf-label">
        <g:render template="/abrangenciaTerritorial"/>
    </span>
</div>
