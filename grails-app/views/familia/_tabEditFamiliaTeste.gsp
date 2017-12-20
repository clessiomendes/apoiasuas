<%@ page import="org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
%>

<g:if test="${localDtoFamilia?.referencia}">
    <div class="fieldcontain">
        <label>Referência familiar</label>
        <span class="property-value">
            ${localDtoFamilia.getReferencia().nomeCompleto}
        </span>
    </div>
</g:if>
<br>
%{--
O código legado só fica disponível se o serviço tem acesso a este recurso, pois ele oculta o id na apresentação da
descrição da familia: Familita.getCad()
--}%
<sec:access acessoServico="${RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO}">
    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'codigoLegado', 'error')} ">
        <label for="codigoLegado">
            <g:message code="familia.codigoLegado.label" default="Codigo Legado" />
        </label>
        <g:textField name="codigoLegado" size="10" pattern="[0-9]{0,}" value="${localDtoFamilia?.codigoLegado}"/>
    </div>
</sec:access>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'tecnicoReferencia', 'error')} ">
    <label for="tecnicoReferencia">
        <g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" />
    </label>
    <g:select id="tecnicoReferencia" name="tecnicoReferencia.id" style="max-width: 200px;"
              from="${operadores}" optionKey="id" value="${localDtoFamilia?.tecnicoReferencia?.id}"
              class="many-to-one" noSelection="['': '']"/>
</div>

<fieldset class="embedded endereco"><legend><g:message code="familia.endereco.label" default="Endereço" /></legend>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.tipoLogradouro', 'error')} ">
        <label for="endereco.tipoLogradouro">
            <g:message code="familia.endereco.tipoLogradouro.label" default="Tipo Logradouro" />
        </label>
        <g:textField name="endereco.tipoLogradouro" maxlength="15" value="${enderecoInstance?.tipoLogradouro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.nomeLogradouro', 'error')} required">
        <label for="endereco.nomeLogradouro">
            <g:message code="familia.endereco.nomeLogradouro.label" default="Nome Logradouro" />
            <span class="required-indicator">*</span>
        </label>
        <g:textField name="endereco.nomeLogradouro" maxlength="60" value="${enderecoInstance?.nomeLogradouro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.numero', 'error')} ">
        <label for="endereco.numero">
            <g:message code="familia.endereco.numero.label" default="Numero" />
        </label>
        <g:textField name="endereco.numero" maxlength="7" value="${enderecoInstance?.numero}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.complemento', 'error')} ">
        <label for="endereco.complemento">
            <g:message code="familia.endereco.complemento.label" default="Complemento" />
        </label>
        <g:textField name="endereco.complemento" maxlength="30" value="${enderecoInstance?.complemento}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.bairro', 'error')} ">
        <label for="endereco.bairro">
            <g:message code="familia.endereco.bairro.label" default="Bairro" />
        </label>
        <g:textField name="endereco.bairro" maxlength="60" value="${enderecoInstance?.bairro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.municipio', 'error')} ">
        <label for="endereco.municipio">
            <g:message code="familia.endereco.municipio.label" default="Municipio" />
        </label>
        <g:textField name="endereco.municipio" maxlength="60" value="${enderecoInstance?.municipio}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.UF', 'error')} ">
        <label for="endereco.UF">
            <g:message code="familia.endereco.UF.label" default="UF" />
        </label>
        <g:textField name="endereco.UF" maxlength="2" value="${enderecoInstance?.UF}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.CEP', 'error')} ">
        <label for="endereco.CEP">
            <g:message code="familia.endereco.CEP.label" default="CEP" />
        </label>
        <g:textField name="endereco.CEP" maxlength="10" value="${enderecoInstance?.CEP}"/>
    </div>

</fieldset>

<fieldset class="embedded"><legend>Características do domicílio</legend>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Moradia</label>
        <g:textField name="XXX" size="15" maxlength="KKK" value="${''}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Nº cômodos</label>
        <g:textField name="XXX" size="2" maxlength="KKK" value="${''}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Nº quartos</label>
        <g:textField name="XXX" size="2" maxlength="KKK" value="${''}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Tipo de construção</label>
        <g:textField name="XXX" size="15" maxlength="KKK" value="${''}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Riscos</label>
        <span class="property-value"><g:checkBox name="XXX" maxlength="KKK" value="${''}"/> sim</span>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Barreira arquitetônica</label>
        <g:textField name="XXX" size="5" maxlength="KKK" value="${''}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>Energia elétrica</label>
        <g:textField name="XXX" size="5" maxlength="KKK" value="${''}"/>
    </div>

%{--
    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
        <label>
            YYY
        </label>
        <g:textField name="XXX" size="ZZZ" maxlength="KKK" value="${''}"/>
    </div>
--}%

</fieldset>

<div class="tamanho-memo fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'XXX', 'error')} ">
    <label>Informações complementares</label>
    <g:textArea name="XXX" rows="6" value="${''}"/>
</div>

<fieldset class="embedded"><legend>Telefones</legend>
    <g:render template="telefone/formTelefones" model="${[localDtoFamilia: localDtoFamilia]}"/>
</fieldset>

