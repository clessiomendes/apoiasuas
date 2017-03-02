<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
%>

<div class="fieldcontain">
    <label>Referência familiar</label>
    ${localDtoFamilia?.getReferencia()?.nomeCompleto}
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'codigoLegado', 'error')} ">
    <label for="codigoLegado">
        <g:message code="familia.codigoLegado.label" default="Codigo Legado" />
    </label>
    <g:textField name="codigoLegado" value="${localDtoFamilia?.codigoLegado}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'tecnicoReferencia', 'error')} ">
    <label for="tecnicoReferencia">
        <g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" />
    </label>
    <g:select id="tecnicoReferencia" name="tecnicoReferencia.id" from="${operadores}" optionKey="id" value="${localDtoFamilia?.tecnicoReferencia?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<fieldset class="embedded"><legend><g:message code="familia.endereco.label" default="Endereço" /></legend>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.tipoLogradouro', 'error')} ">
        <label for="endereco.tipoLogradouro">
            <g:message code="familia.endereco.tipoLogradouro.label" default="Tipo Logradouro" />
        </label>
        <g:textField name="endereco.tipoLogradouro" size="15" maxlength="15" value="${enderecoInstance?.tipoLogradouro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.nomeLogradouro', 'error')} ">
        <label for="endereco.nomeLogradouro">
            <g:message code="familia.endereco.nomeLogradouro.label" default="Nome Logradouro" />
        </label>
        <g:textField name="endereco.nomeLogradouro" size="60" maxlength="60" value="${enderecoInstance?.nomeLogradouro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.numero', 'error')} ">
        <label for="endereco.numero">
            <g:message code="familia.endereco.numero.label" default="Numero" />
        </label>
        <g:textField name="endereco.numero" size="5" maxlength="5" value="${enderecoInstance?.numero}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.complemento', 'error')} ">
        <label for="endereco.complemento">
            <g:message code="familia.endereco.complemento.label" default="Complemento" />
        </label>
        <g:textField name="endereco.complemento" size="30" maxlength="30" value="${enderecoInstance?.complemento}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.bairro', 'error')} ">
        <label for="endereco.bairro">
            <g:message code="familia.endereco.bairro.label" default="Bairro" />
        </label>
        <g:textField name="endereco.bairro" size="30" maxlength="30" value="${enderecoInstance?.bairro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.CEP', 'error')} ">
        <label for="endereco.CEP">
            <g:message code="familia.endereco.CEP.label" default="CEP" />
        </label>
        <g:textField name="endereco.CEP" size="7" maxlength="10" value="${enderecoInstance?.CEP}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.municipio', 'error')} ">
        <label for="endereco.municipio">
            <g:message code="familia.endereco.municipio.label" default="Municipio" />
        </label>
        <g:textField name="endereco.municipio" size="30" maxlength="60" value="${enderecoInstance?.municipio}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.UF', 'error')} ">
        <label for="endereco.UF">
            <g:message code="familia.endereco.UF.label" default="UF" />
        </label>
        <g:textField name="endereco.UF" size="2" maxlength="2" value="${enderecoInstance?.UF}"/>
    </div>
</fieldset>
