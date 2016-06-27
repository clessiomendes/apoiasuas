<%@ page import="org.apoiasuas.Link" %>

<%
    Link linkDTO = linkInstance
%>

<g:javascript>
    jQuery(document).ready(function ()
    {
        $("input[name='tipo']").change(radioValueChanged);
    })

    function radioValueChanged()
    {
        radioValue = $(this).val();

//        alert(radioValue);

        if (radioValue == "${Link.Tipo.FILE.toString()}") {
            $('#tipoFile').slideDown(500);
            $('#tipoUrl').hide();
        } else {
            $('#tipoFile').hide();
            $('#tipoUrl').slideDown(500);
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

<div id="tipoUrl" class="fieldcontain ${linkDTO.tipo?.isUrl() ? '' : 'hidden'} ${hasErrors(bean: linkDTO, field: 'url', 'error')} ">
    <label for="url">
        <g:message code="link.URL.label" default="Url" />
    </label>
    <g:textField maxlength="250" size="60" name="url" value="${linkDTO?.url}"/>
</div>

<div id="tipoFile" class="fieldcontain ${linkDTO.tipo?.isFile() ? '' : 'hidden'} ${hasErrors(bean: linkDTO, field: 'file', 'error')} ">
    <label for="file">
        Enviar novo arquivo
    </label>
    <input type="file" id="file" name="file"/>
</div>

<div class="fieldcontain ${hasErrors(bean: linkDTO, field: 'descricao', 'error')} ">
	<label for="descricao">
		<g:message code="link.descricao.label" />
	</label>
	<g:textField maxlength="80" size="60" name="descricao" value="${linkDTO?.descricao}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: linkInstance, field: 'instrucoes', 'error')} ">
	<label for="instrucoes">
		<g:message code="link.instrucoes.label"/>
	</label>
	<g:textArea name="instrucoes" rows="3" cols="60" value="${linkInstance?.instrucoes}"/>
</div>


