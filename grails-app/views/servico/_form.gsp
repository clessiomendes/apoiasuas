<%@ page import="org.apoiasuas.redeSocioAssistencial.Servico" %>
<%
    Servico localDtoServico = servicoInstance
%>

<g:javascript>
    $(document).ready(function() {
        $("#tabs").tabs();
    });
</g:javascript>

<div id="tabs">
    <ul>
        <li><a href="#tabInicial">informações</a> </li>
        <li><a href="#tabEncaminhamento">encaminhamentos</a> </li>
    </ul>

    <div id="tabInicial">
        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'apelido', 'error')} required">
            <label for="apelido">
                <g:message code="servico.apelido.label" default="Nome popular" />
                <span class="required-indicator">*</span>
            </label>
            <g:textField name="apelido" size="60" maxlength="60" required="" value="${localDtoServico?.apelido}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'enabled', 'error')}">
            <label></label>
            <g:checkBox name="habilitado" value="${localDtoServico?.habilitado}"/>
            <g:message code="servico.habilitado.label"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'descricao', 'error')} ">
            <label for="descricao">
                <g:message code="servico.descricao.label" default="Descrição detalhada" />
            </label>
            <div class="property-value">
                Forneça detalhes do serviço, como critérios e instruções para acesso, documentos necessários, o que é oferecido, etc.
                <g:textArea name="descricao" rows="8" cols="60" value="${localDtoServico?.descricao}"/>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'telefones', 'error')}">
            <label for="telefones">
                <g:message code="servico.telefones.label" default="Telefone(s)" />
            </label>
            <g:textField name="telefones" size="60" maxlength="255" value="${localDtoServico?.telefones}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'site', 'error')}">
            <label for="site">
                <g:message code="servico.site.label" default="Site na internet" />
            </label>
            <g:textField name="site" size="60" maxlength="255" value="${localDtoServico?.site}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'abrangenciaTerritorial', 'error')} required">
            <span id="uf-label" class="property-label"><g:message code="servico.abrangenciaTerritorial.label" default="Atende *" /></span>
            <span class="property-value" style="margin-left:25%" aria-labelledby="uf-label">
                <g:render template="/abrangenciaTerritorial"/>
            </span>
        </div>

    </div>
    <div id="tabEncaminhamento">
        %{--
        <fieldset id="fieldsetEncaminhamento" class="embedded" ${localDtoServico.podeEncaminhar ? "" : "disabled"}>
            <legend>
        --}%
        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'podeEncaminhar', 'error')}">
            <label/>
            <g:checkBox name="podeEncaminhar" value="${localDtoServico.podeEncaminhar}" onclick="document.getElementById('fieldsetEncaminhamento').disabled = ! this.checked; return true"/>
            <g:message code="servico.podeEncaminhar" default="Permitir encaminhamento" />
        </div>
        %{--
            </legend>
        --}%

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'nomeFormal', 'error')}">
            <label for="nomeFormal">
                <g:message code="servico.nomeFormal.label" default="Nome Formal" />
            </label>
            <g:textField name="nomeFormal" size="60" value="${localDtoServico?.nomeFormal}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'encaminhamentoPadrao', 'error')} ">
            <label for="encaminhamentoPadrao">
                <g:message code="servico.encaminhamentoPadrao.label" default="Encaminhamento Padrao" />
            </label>
            <g:textArea name="encaminhamentoPadrao" rows="8" cols="60" value="${localDtoServico?.encaminhamentoPadrao}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.tipoLogradouro', 'error')} ">
            <label for="endereco.tipoLogradouro">
                <g:message code="endereco.tipoLogradouro.label" default="Tipo Logradouro" />
            </label>
            <g:textField size="10" name="endereco.tipoLogradouro" value="${localDtoServico?.endereco?.tipoLogradouro}"/>

        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.nomeLogradouro', 'error')} ">
            <label for="endereco.nomeLogradouro">
                <g:message code="endereco.nomeLogradouro.label" default="Nome Logradouro" />

            </label>
            <g:textField size="60" name="endereco.nomeLogradouro" value="${localDtoServico?.endereco?.nomeLogradouro}"/>

        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.numero', 'error')} ">
            <label for="endereco.numero">
                <g:message code="endereco.numero.label" default="Numero" />

            </label>
            <g:textField size="10" name="endereco.numero" value="${localDtoServico?.endereco?.numero}"/>

        </div>


        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.complemento', 'error')} ">
            <label for="endereco.complemento">
                <g:message code="endereco.complemento.label" default="Complemento" />

            </label>
            <g:textField size="10" name="endereco.complemento" value="${localDtoServico?.endereco?.complemento}"/>

        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.bairro', 'error')} ">
            <label for="endereco.bairro">
                <g:message code="endereco.bairro.label" default="Bairro" />

            </label>
            <g:textField size="30" name="endereco.bairro" value="${localDtoServico?.endereco?.bairro}"/>

        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.municipio', 'error')} ">
            <label for="endereco.municipio">
                <g:message code="endereco.municipio.label" default="Municipio" />

            </label>
            <g:textField size="60" name="endereco.municipio" value="${localDtoServico?.endereco?.municipio}"/>

        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.UF', 'error')} ">
            <label for="endereco.UF">
                <g:message code="endereco.UF.label" default="UF" />
            </label>
            <g:textField size="2" name="endereco.UF" value="${localDtoServico?.endereco?.UF}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoServico, field: 'endereco.CEP', 'error')} ">
            <label for="endereco.CEP">
                <g:message code="endereco.CEP.label" default="CEP" />
            </label>
            <g:textField size="10" name="endereco.CEP" value="${localDtoServico?.endereco?.CEP}"/>

        </div>
        %{--
        </fieldset>
        --}%
    </div>
</div>
