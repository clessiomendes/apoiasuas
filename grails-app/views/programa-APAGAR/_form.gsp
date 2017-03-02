<%@ page import="org.apoiasuas.marcador.Programa" %>



<div class="fieldcontain ${hasErrors(bean: programaInstance, field: 'nome', 'error')} required">
	<label for="nome">
		<g:message code="programa.nome.label" default="Nome" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="nome" cols="40" rows="5" maxlength="255" required="" value="${programaInstance?.nome}"/>

</div>

