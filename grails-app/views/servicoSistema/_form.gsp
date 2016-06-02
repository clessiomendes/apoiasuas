<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<%
    ServicoSistema servicoSistema = servicoSistemaInstance
%>

<g:javascript>
    $(document).ready(function() {
        $('#div_abrangenciaTerritorial').jstree({
            //'plugins' : ['checkbox'],
            'core' : {
                'data' : ${raw(territoriosDisponiveis)}
    },
    "rules":{
        'multiple' : false
    },
    "ui" : {
        "select_limit" : 1  //only allow one node to be selected at a time
    }//ui
});//jstree
});//function

/**
* Transfere os checkbox'es marcados na treeview de Areas de Atuacao para um parametro hidden a ser submetido no post do formulario
*/
function submeteAbrangenciaTerritorial(calee) {
    var selectedElmsIds = [];
    var selectedElms = $('#div_abrangenciaTerritorial').jstree("get_selected", true);
    $.each(selectedElms, function() {
        selectedElmsIds.push(this.id);
    });
    document.getElementById("territorioAtuacao").value = selectedElmsIds.join(",");
    return true
}
</g:javascript>

    <g:hiddenField name="territorioAtuacao"/>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'nome', 'error')} required">
        <label for="nome">
            <g:message code="servico.nome.label" default="Nome" />
            <span class="required-indicator">*</span>
        </label>
        <g:textField name="nome" size="60" maxlength="80" required="" value="${servicoSistema?.nome}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'enabled', 'error')}">
        <label></label>
        <g:checkBox name="habilitado" value="${servicoSistema?.habilitado}"/>
        <g:message code="servicoSistema.habilitado.label"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'telefone', 'error')}">
        <label for="telefone">
            <g:message code="servico.telefone.label" default="Telefone(s)" />
        </label>
        <g:textField name="telefone" size="30" maxlength="30" value="${servicoSistema?.telefone}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'site', 'error')}">
        <label for="site">
            <g:message code="servico.site.label" default="Site na internet" />
        </label>
        <g:textField name="site" size="60" maxlength="80" value="${servicoSistema?.site}"/>
    </div>

    <div class="fieldcontain">
        <span id="uf-label" class="property-label"><g:message code="servico.abrangenciaTerritorial.label" default="Território atendido" /></span>
        <span class="property-value" style="margin-left:25%" aria-labelledby="uf-label">
            <div id="div_abrangenciaTerritorial"></div>
        </span>
    </div>

<fieldset id="fieldsetEndereco" class="embedded">
    <legend>
        Endereço
    </legend>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.tipoLogradouro', 'error')} ">
        <label for="endereco.tipoLogradouro">
            <g:message code="endereco.tipoLogradouro.label" default="Tipo Logradouro" />
        </label>
        <g:textField size="10" name="endereco.tipoLogradouro" value="${servicoSistema?.endereco?.tipoLogradouro}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.nomeLogradouro', 'error')} ">
        <label for="endereco.nomeLogradouro">
            <g:message code="endereco.nomeLogradouro.label" default="Nome Logradouro" />
        </label>
        <g:textField size="60" name="endereco.nomeLogradouro" value="${servicoSistema?.endereco?.nomeLogradouro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.numero', 'error')} ">
        <label for="endereco.numero">
            <g:message code="endereco.numero.label" default="Numero" />
        </label>
        <g:textField size="10" name="endereco.numero" value="${servicoSistema?.endereco?.numero}"/>
    </div>


    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.complemento', 'error')} ">
        <label for="endereco.complemento">
            <g:message code="endereco.complemento.label" default="Complemento" />
        </label>
        <g:textField size="10" name="endereco.complemento" value="${servicoSistema?.endereco?.complemento}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.bairro', 'error')} ">
        <label for="endereco.bairro">
            <g:message code="endereco.bairro.label" default="Bairro" />
        </label>
        <g:textField size="30" name="endereco.bairro" value="${servicoSistema?.endereco?.bairro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.municipio', 'error')} ">
        <label for="endereco.municipio">
            <g:message code="endereco.municipio.label" default="Municipio" />

        </label>
        <g:textField size="60" name="endereco.municipio" value="${servicoSistema?.endereco?.municipio}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.UF', 'error')} ">
        <label for="endereco.UF">
            <g:message code="endereco.UF.label" default="UF" />
        </label>
        <g:textField size="2" name="endereco.UF" value="${servicoSistema?.endereco?.UF}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.CEP', 'error')} ">
        <label for="endereco.CEP">
            <g:message code="endereco.CEP.label" default="CEP" />
        </label>
        <g:textField size="10" name="endereco.CEP" value="${servicoSistema?.endereco?.CEP}"/>
    </div>
</fieldset>
