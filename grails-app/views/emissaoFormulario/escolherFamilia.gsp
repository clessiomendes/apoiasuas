<%@ page import="org.apoiasuas.AncestralController; org.apoiasuas.formulario.EmissaoFormularioController" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="importar.cadastro.de.familias"/></title>
    <asset:javascript src="jqery.js"/>
</head>

<g:javascript>
    function limparFamilia() {
        document.getElementById("familiaParaSelecao").innerHTML='';
        document.getElementById("codigoLegado").value='';
    }

    /**
    * Sempre que carregar a página, submete a chamada ajax identica ao click do botao de procurar familia
    */
    $(document).ready(function() {
        if (document.getElementById("codigoLegado").value != '') {
            ${remoteFunction(action: 'familiaParaSelecao', method: "post", update: "familiaParaSelecao",
                params: [codigoLegado: session.getAttribute(org.apoiasuas.AncestralController.ULTIMA_FAMILIA)?.codigoLegado])}
            document.getElementById("codigoLegado").select();
        }
    });

    /**
    * Recarregar a pagina quando chegar a ela pelo botao de voltar do browser
    */
    $(window).onpageshow = function(evt) {
    // If persisted then it is in the page cache, force a reload of the page.
    if (evt.persisted) {
        document.body.style.display = "none";
        location.reload();
    }
};
</g:javascript>

<body>
<div id="edit-fool" class="content scaffold-edit" role="main">
    <h1><g:message code="???" default="Emissão de Formulários"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:formRemote name="fool" method="post" update="familiaParaSelecao" url="[action: 'familiaParaSelecao']"
                  onSuccess="document.getElementById('membroSelecionado').focus();">
        <fieldset class="form">
            <div class="fieldcontain">
                <label for="codigoLegado">
                    <g:radio name="familiaCadastrada" value="true" checked="true"/> Família cadastrada:
                </label>
                <g:textField autofocus="true" name="codigoLegado" id="codigoLegado" size="2" value="${session.getAttribute(org.apoiasuas.AncestralController.ULTIMA_FAMILIA)?.codigoLegado}"/>
                <g:actionSubmit class="save" action="familiaParaSelecao" id="btnSelecionarFamilia" value="ok"/>
            </div>
            <div class="fieldcontain">
                <label>
                    <g:radio name="familiaCadastrada" value="false" onclick="limparFamilia();"/> Família sem cadastro
                </label>
            </div>
        </fieldset>
    </g:formRemote>

    <g:form>
        <g:hiddenField name="idFormulario"/>
        <div id="familiaParaSelecao" style="display:inline"></div>
        <g:each in="${formulariosDisponiveis}" var="tipo">
            <fieldset class="embedded"><legend class="collapsable" style="cursor:pointer;">${tipo.key}</legend>
            <g:each in="${tipo.value}" var="formulario">
                <g:actionSubmitOpcaoFomulario formulario="${formulario}"/>
            </g:each>
            </fieldset>
            %{--<g:actionSubmit value="${it.nome}" action="preencherFormulario" onclick="this.form.idFormulario.value = '${it.id}'; return true"/>--}%
        </g:each>
    </g:form>

</div>

</body>
</html>
