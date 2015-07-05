<%@ page import="org.apoiasuas.Link" %>

<div class="fieldcontain ${hasErrors(bean: linkInstance, field: 'url', 'error')} ">
    <label for="url">
        <g:message code="link.url.label" default="Url" />

    </label>
    <g:textField maxlength="250" size="80" name="url" value="${linkInstance?.url}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: linkInstance, field: 'descricao', 'error')} ">
	<label for="descricao">
		<g:message code="link.descricao.label" default="Descrição" />
		
	</label>
	<g:textField maxlength="80" size="80" name="descricao" value="${linkInstance?.descricao}"/>

</div>

%{--
<div class="fieldcontain ${hasErrors(bean: linkInstance, field: 'instrucoes', 'error')} ">
	<label for="instrucoes">
		<g:message code="link.instrucoes.label" default="Instruções" />
		
	</label>
	<g:textArea name="instrucoes" rows="60" cols="3" value="${linkInstance?.instrucoes}"/>

</div>
--}%


