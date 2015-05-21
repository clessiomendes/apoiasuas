<%@ page import="org.apoiasuas.formulario.Formulario" %>

<%
	org.apoiasuas.formulario.Formulario localDtoFormulario = formularioInstance
%>

<div class="fieldcontain ${hasErrors(bean: localDtoFormulario, field: 'nome', 'error')} required">
	<label for="nome">
		<g:message code="formulario.nome.label" default="Nome" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="nome" size="60" required="" value="${localDtoFormulario?.nome}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFormulario, field: 'descricao', 'error')} ">
	<label for="descricao">
		<g:message code="formulario.descricao.label" default="Descricao" />
		
	</label>
	<g:textField name="descricao" size="60" value="${localDtoFormulario?.descricao}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFormulario, field: 'template', 'error')} required">
	<label for="template">
		<g:message code="formulario.template.label" default="Formulário padrão" />
	</label>
	<input type="file" id="template" name="template" />
	<g:if test="${localDtoFormulario?.template}">
		<g:link action="downloadTemplate" id="${localDtoFormulario.id}">(baixar atual)</g:link>
	</g:if>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFormulario, field: 'campos', 'error')} ">
	<label for="campos">
		<g:message code="formulario.campos.label" default="Campos" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${localDtoFormulario?.camposOrdenados?}" var="c">
    %{--<li><g:link controller="campoFormulario" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>--}%
    <li>${c?.encodeAsHTML()}</li>
</g:each>
%{--
<li class="add">
<g:link controller="campoFormulario" action="create" params="['formulario.id': localDtoFormulario?.id]">${message(code: 'default.add.label', args: [message(code: 'campoFormulario.label', default: 'CampoFormulario')])}</g:link>
</li>
--}%
</ul>


</div>

