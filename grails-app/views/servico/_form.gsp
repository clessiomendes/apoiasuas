<%@ page import="org.apoiasuas.servico.Servico" %>



<div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'apelido', 'error')} required">
	<label for="apelido">
		<g:message code="servico.apelido.label" default="Apelido" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="apelido" size="20" required="" value="${servicoInstance?.apelido}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'nomeFormal', 'error')} required">
	<label for="nomeFormal">
		<g:message code="servico.nomeFormal.label" default="Nome Formal" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="nomeFormal" size="60" required="" value="${servicoInstance?.nomeFormal}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'encaminhamentoPadrao', 'error')} ">
	<label for="encaminhamentoPadrao">
		<g:message code="servico.encaminhamentoPadrao.label" default="Encaminhamento Padrao" />
		
	</label>
	<g:textArea name="encaminhamentoPadrao" rows="8" cols="60" value="${servicoInstance?.encaminhamentoPadrao}"/>

</div>

<fieldset class="embedded"><legend><g:message code="servico.endereco.label" default="Endereco" /></legend>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.tipoLogradouro', 'error')} ">
        <label for="endereco.tipoLogradouro">
            <g:message code="servico.endereco.tipoLogradouro.label" default="Tipo Logradouro" />
        </label>
        <g:textField size="10" name="endereco.tipoLogradouro" value="${servicoInstance?.endereco?.tipoLogradouro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.nomeLogradouro', 'error')} ">
        <label for="endereco.nomeLogradouro">
            <g:message code="servico.endereco.nomeLogradouro.label" default="Nome Logradouro" />

        </label>
        <g:textField size="60" name="endereco.nomeLogradouro" value="${servicoInstance?.endereco?.nomeLogradouro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.numero', 'error')} ">
        <label for="endereco.numero">
            <g:message code="servico.endereco.numero.label" default="Numero" />

        </label>
        <g:textField size="10" name="endereco.numero" value="${servicoInstance?.endereco?.numero}"/>

    </div>


    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.complemento', 'error')} ">
        <label for="endereco.complemento">
            <g:message code="servico.endereco.complemento.label" default="Complemento" />

        </label>
        <g:textField size="10" name="endereco.complemento" value="${servicoInstance?.endereco?.complemento}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.bairro', 'error')} ">
        <label for="endereco.bairro">
            <g:message code="servico.endereco.bairro.label" default="Bairro" />

        </label>
        <g:textField size="30" name="endereco.bairro" value="${servicoInstance?.endereco?.bairro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.municipio', 'error')} ">
        <label for="endereco.municipio">
            <g:message code="servico.endereco.municipio.label" default="Municipio" />

        </label>
        <g:textField size="60" name="endereco.municipio" value="${servicoInstance?.endereco?.municipio}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.UF', 'error')} ">
        <label for="endereco.UF">
            <g:message code="servico.endereco.UF.label" default="UF" />

        </label>
        <g:textField size="2" name="endereco.UF" value="${servicoInstance?.endereco?.UF}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoInstance, field: 'endereco.CEP', 'error')} ">
        <label for="endereco.CEP">
            <g:message code="servico.endereco.CEP.label" default="CEP" />

        </label>
        <g:textField size="10" name="endereco.CEP" value="${servicoInstance?.endereco?.CEP}"/>

    </div>


</fieldset>
