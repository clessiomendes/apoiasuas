<%@ page import="org.apoiasuas.cidadao.Cidadao" %>



<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'referencia', 'error')} ">
	<label for="referencia">
		<g:message code="cidadao.referencia.label" default="Referencia" />
		
	</label>
	<g:checkBox name="referencia" value="${cidadaoInstance?.referencia}" />

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'criador', 'error')} required">
	<label for="criador">
		<g:message code="cidadao.criador.label" default="Criador" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="criador" name="criador.id" from="${org.apoiasuas.UsuarioSistema.list()}" optionKey="id" required="" value="${cidadaoInstance?.criador?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'ultimoAlterador', 'error')} required">
	<label for="ultimoAlterador">
		<g:message code="cidadao.ultimoAlterador.label" default="Ultimo Alterador" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="ultimoAlterador" name="ultimoAlterador.id" from="${org.apoiasuas.UsuarioSistema.list()}" optionKey="id" required="" value="${cidadaoInstance?.ultimoAlterador?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'familia', 'error')} ">
	<label for="familia">
		<g:message code="cidadao.familia.label" default="Familia" />
		
	</label>
	<g:select id="familia" name="familia.id" from="${org.apoiasuas.cidadao.Familia.list()}" optionKey="id" value="${cidadaoInstance?.familia?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'dataNascimento', 'error')} ">
	<label for="dataNascimento">
		<g:message code="cidadao.dataNascimento.label" default="Data Nascimento" />
		
	</label>
	<g:datePicker name="dataNascimento" precision="day"  value="${cidadaoInstance?.dataNascimento}" default="none" noSelection="['': '']" />

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'nis', 'error')} ">
	<label for="nis">
		<g:message code="cidadao.nis.label" default="Nis" />
		
	</label>
	<g:textField name="nis" value="${cidadaoInstance?.nis}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'nomeCompleto', 'error')} ">
	<label for="nomeCompleto">
		<g:message code="cidadao.nomeCompleto.label" default="Nome Completo" />
		
	</label>
	<g:textField name="nomeCompleto" value="${cidadaoInstance?.nomeCompleto}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'origemImportacaoAutomatica', 'error')} ">
	<label for="origemImportacaoAutomatica">
		<g:message code="cidadao.origemImportacaoAutomatica.label" default="Origem Importacao Automatica" />
		
	</label>
	<g:checkBox name="origemImportacaoAutomatica" value="${cidadaoInstance?.origemImportacaoAutomatica}" />

</div>

<div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'parentescoReferencia', 'error')} ">
	<label for="parentescoReferencia">
		<g:message code="cidadao.parentescoReferencia.label" default="Parentesco Referencia" />
		
	</label>
	<g:textField name="parentescoReferencia" value="${cidadaoInstance?.parentescoReferencia}"/>

</div>

