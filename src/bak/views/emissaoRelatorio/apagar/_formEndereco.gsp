<%@ page import="org.apoiasuas.cidadao.Familia" %>

<fieldset class="embedded"><legend><g:message code="familia.endereco.label" default="Endereco" /></legend>


    <div class="fieldcontain ${hasErrors(bean: familiaInstance, field: 'endereco.nomeLogradouro', 'error')} ">
        <label for="endereco.nomeLogradouro">
            <g:message code="familia.endereco.nomeLogradouro.label" default="Logradouro" />
        </label>
        <g:textField name="endereco.tipoLogradouro" size = "5" value="${familiaInstance?.endereco?.tipoLogradouro}"/>
        <g:textField name="endereco.nomeLogradouro" size = "40" value="${familiaInstance?.endereco?.nomeLogradouro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: familiaInstance, field: 'endereco.numero', 'error')} ">
        <label for="endereco.numero">
            <g:message code="familia.endereco.numero.label" default="Numero" />
        </label>
        <g:textField name="endereco.numero" size="5" value="${familiaInstance?.endereco?.numero}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: familiaInstance, field: 'endereco.complemento', 'error')} ">
        <label for="endereco.complemento">
            <g:message code="familia.endereco.complemento.label" default="Complemento" />
        </label>
        <g:textField name="endereco.complemento" value="${familiaInstance?.endereco?.complemento}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: familiaInstance, field: 'endereco.bairro', 'error')} ">
        <label for="endereco.bairro">
            <g:message code="familia.endereco.bairro.label" default="Bairro" />
        </label>
        <g:textField name="endereco.bairro" value="${familiaInstance?.endereco?.bairro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: familiaInstance, field: 'endereco.CEP', 'error')} ">
        <label for="endereco.CEP">
            <g:message code="familia.endereco.CEP.label" default="CEP" />
        </label>
        <g:textField name="endereco.CEP" value="${familiaInstance?.endereco?.CEP}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: familiaInstance, field: 'telefones', 'error')} ">
        <label>
            <g:message code="familia.telefones.label" default="Telefone" />
        </label>


    %{--TODO: Alinhar radiobuttons dos telefones à direita do label "Telefone"--}%
        <div>
        <g:if test="${familiaInstance?.telefones}">
            <g:each var="telefone" in="${familiaInstance.telefones}" status="i">
                <g:radio name="telefoneSelecionado" value="${telefone}" checked="${(i==0) ? 'checked' : ''}" /> ${telefone} <br>
            </g:each>
        </g:if>
        <g:radio name="telefoneSelecionado" value="novo" onclick="document.getElementById('novoTelefone').focus();"/> novo: <g:textField name="novoTelefoneDDD" size="2" value="${DDDpadrao}"/> <g:textField name="novoTelefone"/><br>
        </div>

    </div>


</fieldset>
