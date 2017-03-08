<%@ page import="org.apoiasuas.cidadao.Cidadao" %>

<%
	org.apoiasuas.cidadao.Cidadao localDtoCidadao = cidadaoInstance;
    String prefixo = prefixoEntidade ?: "";
%>

<asset:javascript src="especificos/jquery-mask.js"/>
<g:javascript>
	$(function() {
		$(".dateMask").mask("99/99/9999");
	});

/*
	//evento onCheck para o checkbox "efetivado"
	$("#checkReferencia").change(function() {
		//var $dataEfetivada = $("#dataEfetivada");
		if (this.checked) {
			$("#divParentescoReferencia").hide()
		} else {
			$("#divParentescoReferencia").show();
		}
	});
	$("#checkReferencia").change();
*/

</g:javascript>

<f:with bean="${localDtoCidadao}">
	<f:field property="nomeCompleto" prefix="${prefixo}" label="Nome completo" widget-size="60" required="true"/>

%{--Só permite alterar se não for a referência--}%
	<div id="divParentescoReferencia" class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'parentescoReferencia', 'error')} ">
		<g:hiddenField name="${prefixo}referencia" value="${localDtoCidadao.referencia}"/>
		<g:if test="${localDtoCidadao.referencia}">
			<label></label>Referência Familiar
			<g:hiddenField name="${prefixo}parentescoReferencia" value="${localDtoCidadao.parentescoReferencia}"/>
		</g:if>
		<g:else>
			<label for="parentescoReferencia">
				<nobr>
					Parentesco <g:helpTooltip chave="cidadao.parentesco.referencia" args="[localDtoCidadao.familia?.referencia?.nomeCompleto]"/>
				</nobr>
			</label>
			<g:textField name="${prefixo}parentescoReferencia" value="${localDtoCidadao?.parentescoReferencia}"/>
		</g:else>
	</div>

<div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'dataNascimento', 'error')} ">
	<label for="dataNascimento">
		<g:message code="cidadao.dataNascimento.label" default="Data Nascimento" />
	</label>
	<g:textField class="dateMask" name="dataNascimento" size="10" maxlength="10" value="${localDtoCidadao?.dataNascimento?.format("dd/MM/yyyy")}"/>
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

</f:with>