<%@ page import="org.apoiasuas.cidadao.Cidadao" %>

<%
	org.apoiasuas.cidadao.Cidadao localDtoCidadao = cidadaoInstance
%>

%{--
<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'referencia', 'error')} ">
	<label for="referencia">
		<g:message code="cidadao.referencia.label" default="Referencia" />
	</label>
	<g:checkBox name="referencia" value="${localDtoCidadao?.referencia}" />
</div>
--}%

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'nomeCompleto', 'error')} ">
	<label for="nomeCompleto">
		<g:message code="cidadao.nomeCompleto.label" default="Nome Completo" />
	</label>
	<g:textField name="nomeCompleto" size="60" maxlength="60" value="${localDtoCidadao?.nomeCompleto}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'dataNascimento', 'error')} ">
	<label for="dataNascimento">
		<g:message code="cidadao.dataNascimento.label" default="Data Nascimento" />
	</label>
	<g:textField name="dataNascimento" size="10" maxlength="10" value="${localDtoCidadao?.dataNascimento?.format("dd/MM/yyyy")}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'nis', 'error')} ">
	<label for="nis">
		<g:message code="cidadao.nis.label" default="NIS" />
	</label>
	<g:textField name="nis" size="15" maxlength="20" value="${localDtoCidadao?.nis}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'identidade', 'error')} ">
	<label for="identidade">
		<g:message code="cidadao.identidade.label" default="Identidade" />
	</label>
	<g:textField name="identidade" size="15" maxlength="20" value="${localDtoCidadao?.identidade}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'cpf', 'error')} ">
	<label for="cpf">
		<g:message code="cidadao.cpf.label" default="CPF" />
	</label>
	<g:textField name="cpf" size="15" maxlength="20" value="${localDtoCidadao?.cpf}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'naturalidade', 'error')} ">
	<label for="naturalidade">
		<g:message code="cidadao.naturalidade.label" default="Naturalidade" />
	</label>
	<g:textField name="naturalidade" size="30" maxlength="60" value="${localDtoCidadao?.naturalidade}"/>
	<g:textField name="UFNaturalidade" size="2" maxlength="2" value="${localDtoCidadao?.UFNaturalidade}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'estadoCivil', 'error')} ">
	<label for="naturalidade">
		<g:message code="cidadao.estadoCivil.label" default="Estado Civil" />
	</label>
	<g:textField name="estadoCivil" size="10" maxlength="20" value="${localDtoCidadao?.estadoCivil}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'nomeMae', 'error')} ">
	<label for="nomeMae">
		<g:message code="cidadao.nomeMae.label" default="Nome da mãe" />
	</label>
	<g:textField name="nomeMae" size="60" maxlength="60" value="${localDtoCidadao?.nomeMae}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'nomePai', 'error')} ">
	<label for="nomePai">
		<g:message code="cidadao.nomePai.label" default="Nome do pai" />
	</label>
	<g:textField name="nomePai" size="60" maxlength="60" value="${localDtoCidadao?.nomePai}"/>
</div>

%{--
<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'parentescoReferencia', 'error')} ">
	<label for="parentescoReferencia">
		<g:message code="cidadao.parentescoReferencia.label" default="Parentesco Referencia" />
	</label>
	<g:textField name="parentescoReferencia" value="${localDtoCidadao?.parentescoReferencia}"/>
</div>
--}%

