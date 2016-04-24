<%@ page import="org.apoiasuas.Configuracao" %>
<%
    Configuracao localDtoConfiguracao = configuracaoInstance
%>

<fieldset id="fieldsetEquipamento" class="embedded">
    <legend>
        Equipamento
    </legend>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.nome', 'error')} required">
        <label for="equipamento.nome">
            <g:message code="configuracao.equipamento.nome.label" default="Nome" />
            <span class="required-indicator">*</span>
        </label>
        <g:textField name="equipamento.nome" size="60" maxlength="80" required="" value="${localDtoConfiguracao?.equipamento?.nome}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.telefone', 'error')}">
        <label for="equipamento.telefone">
            <g:message code="configuracao.equipamento.telefone.label" default="Telefone(s)" />
        </label>
        <g:textField name="equipamento.telefone" size="30" maxlength="30" value="${localDtoConfiguracao?.equipamento?.telefone}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.site', 'error')}">
        <label for="equipamento.site">
            <g:message code="configuracao.equipamento.site.label" default="Site na internet" />
        </label>
        <g:textField name="equipamento.site" size="60" maxlength="80" value="${localDtoConfiguracao?.equipamento?.site}"/>
    </div>

<fieldset id="fieldsetEndereco" class="embedded">
    <legend>
        Endereço
    </legend>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.tipoLogradouro', 'error')} ">
        <label for="equipamento.endereco.tipoLogradouro">
            <g:message code="configuracao.equipamento.endereco.tipoLogradouro.label" default="Tipo Logradouro" />
        </label>
        <g:textField size="10" name="equipamento.endereco.tipoLogradouro" value="${localDtoConfiguracao?.equipamento?.endereco?.tipoLogradouro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.nomeLogradouro', 'error')} ">
        <label for="equipamento.endereco.nomeLogradouro">
            <g:message code="configuracao.equipamento.endereco.nomeLogradouro.label" default="Nome Logradouro" />

        </label>
        <g:textField size="60" name="equipamento.endereco.nomeLogradouro" value="${localDtoConfiguracao?.equipamento?.endereco?.nomeLogradouro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.numero', 'error')} ">
        <label for="equipamento.endereco.numero">
            <g:message code="configuracao.equipamento.endereco.numero.label" default="Numero" />

        </label>
        <g:textField size="10" name="equipamento.endereco.numero" value="${localDtoConfiguracao?.equipamento?.endereco?.numero}"/>

    </div>


    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.complemento', 'error')} ">
        <label for="equipamento.endereco.complemento">
            <g:message code="configuracao.equipamento.endereco.complemento.label" default="Complemento" />

        </label>
        <g:textField size="10" name="equipamento.endereco.complemento" value="${localDtoConfiguracao?.equipamento?.endereco?.complemento}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.bairro', 'error')} ">
        <label for="equipamento.endereco.bairro">
            <g:message code="configuracao.equipamento.endereco.bairro.label" default="Bairro" />

        </label>
        <g:textField size="30" name="equipamento.endereco.bairro" value="${localDtoConfiguracao?.equipamento?.endereco?.bairro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.municipio', 'error')} ">
        <label for="equipamento.endereco.municipio">
            <g:message code="configuracao.equipamento.endereco.municipio.label" default="Municipio" />

        </label>
        <g:textField size="60" name="equipamento.endereco.municipio" value="${localDtoConfiguracao?.equipamento?.endereco?.municipio}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.UF', 'error')} ">
        <label for="equipamento.endereco.UF">
            <g:message code="configuracao.equipamento.endereco.UF.label" default="UF" />
        </label>
        <g:textField size="2" name="equipamento.endereco.UF" value="${localDtoConfiguracao?.equipamento?.endereco?.UF}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoConfiguracao, field: 'equipamento.endereco.CEP', 'error')} ">
        <label for="equipamento.endereco.CEP">
            <g:message code="configuracao.equipamento.endereco.CEP.label" default="CEP" />
        </label>
        <g:textField size="10" name="equipamento.endereco.CEP" value="${localDtoConfiguracao?.equipamento?.endereco?.CEP}"/>

    </div>
</fieldset>
</fieldset>
