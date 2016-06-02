<%@ page import="org.apoiasuas.cidadao.FamiliaController" %>
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
            ${remoteFunction(controller: 'emissaoFormulario', action: 'familiaParaSelecao', method: "post", update: "familiaParaSelecao", params: [codigoLegado: org.apoiasuas.cidadao.FamiliaController.getUltimaFamilia(session)?.codigoLegado])}
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

    <g:formRemote name="fool" method="post" update="familiaParaSelecao" url="[controller: 'emissaoFormulario', action: 'familiaParaSelecao']"
                  onSuccess="document.getElementById('membroSelecionado').focus();">
        <div class="fieldcontain">
            <label>
                <g:radio name="familiaCadastrada" value="false" onclick="limparFamilia();"/> Família sem cadastro
            </label>
        </div>
        <div class="fieldcontain">
            <label for="codigoLegado">
                <g:radio name="familiaCadastrada" value="true" checked="true"/> Família cadastrada:
            </label>
            <g:textField autofocus="true" name="codigoLegado" id="codigoLegado" size="2" value="${FamiliaController.getUltimaFamilia(session)?.codigoLegado}"/>
            <g:actionSubmit class="save" action="familiaParaSelecao" id="btnSelecionarFamilia" value="ok"/>
        </div>
    </g:formRemote>

%{--
    Este form precisa ser "submetido" por uma chamada externa da pagina que inclui este template, por um javascript como:
    document.getElementById('preencherFormulario').submit();
--}%
    <g:form id="preencherFormulario" name="preencherFormulario" controller="emissaoFormulario" action="preencherFormulario" >
        <g:hiddenField name="idFormulario"/>
        <g:hiddenField name="idServico"/>
        <div id="familiaParaSelecao" style="display:inline"></div>
    </g:form>
