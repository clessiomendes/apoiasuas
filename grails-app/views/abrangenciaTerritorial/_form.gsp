<%@ page import="org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial" %>

<%
    AbrangenciaTerritorial abrangenciaTerritorial = abrangenciaTerritorialInstance;
%>

<g:javascript>
    $(document).ready(function() {
        $('#div_territoriosAtuacao').jstree({
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
function submeteTerritoriosAtuacao(calee) {
    var selectedElmsIds = [];
    var selectedElms = $('#div_territoriosAtuacao').jstree("get_selected", true);
    $.each(selectedElms, function() {
        selectedElmsIds.push(this.id);
    });
    document.getElementById("territoriosAtuacao").value = selectedElmsIds.join(",");
    return true
}

</g:javascript>

<g:hiddenField name="territoriosAtuacao"/>

<div class="fieldcontain ${hasErrors(bean: abrangenciaTerritorial, field: 'nome', 'error')} ">
	<label for="nome">
		<g:message code="abrangenciaTerritorial.nome.label" default="Nome" />
        <span class="required-indicator">*</span>
	</label>
	<g:textField size="60" name="nome" required="" value="${abrangenciaTerritorial?.nome}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: abrangenciaTerritorial, field: 'habilitado', 'error')} ">
    <label for="habilitado">
        <g:message code="abrangenciaTerritorial.habilitado.label" default="Habilitado" />
    </label>
    <g:checkBox name="habilitado" value="${abrangenciaTerritorial?.habilitado}" />
</div>

<div class="fieldcontain">
    <span id="uf-label" class="property-label"><g:message code="abrangenciaTerritorial.pai.label" default="Subordinado a" /></span>
    <span class="property-value" style="margin-left:25%" aria-labelledby="uf-label">
        <div id="div_territoriosAtuacao"></div>
    </span>
</div>
