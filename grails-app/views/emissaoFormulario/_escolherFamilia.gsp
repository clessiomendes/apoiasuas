<%@ page import="org.apoiasuas.cidadao.FamiliaController" %>
<g:javascript>
    function limparFamilia() {
        document.getElementById("familiaParaSelecao").innerHTML='';
        document.getElementById("cad").value='';
    }

    /**
    * Sempre que carregar a página, submete a chamada ajax identica ao click do botao de procurar familia
    */
    $(document).ready(function() {
        %{--if (document.getElementById("cad").value != '') {--}%
            %{--${remoteFunction(controller: 'emissaoFormulario', action: 'familiaParaSelecao', method: "post", update: "familiaParaSelecao",--}%
                    %{--params: [cad: org.apoiasuas.cidadao.FamiliaController.getUltimaFamilia(session)?.cad])};--}%
            $("#cad").select();
        //}
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

    function ajaxSuccess(data) {
        $('#membroSelecionado').focus();
        //document.getElementById('membroSelecionado').focus();
    }
</g:javascript>

    <g:formRemote name="fool" method="post" update="familiaParaSelecao" url="[controller: 'emissaoFormulario', action: 'familiaParaSelecao']"
                  onSuccess="ajaxSuccess();">
        <g:radio name="familiaCadastrada" value="true" checked="true"/> Família cadastrada:
        <g:textField autofocus="autofocus" name="cad" id="cad" size="3" value="${FamiliaController.getUltimaFamilia(session)?.cad}"/>
        <g:actionSubmit action="familiaParaSelecao" id="btnSelecionarFamilia" class="speed-button-procurar" value="ok"
                        onclick="noOverlay = true; return true;" />
        <g:radio style="margin-left: 20px" name="familiaCadastrada" value="false" onclick="limparFamilia();"/> Família sem cadastro
    </g:formRemote>

    <br>

%{--
    Este form precisa ser "submetido" por uma chamada externa da pagina que inclui este template, por um javascript como:
    document.getElementById('preencherFormulario').submit();
--}%
    <g:form id="preencherFormulario" name="preencherFormulario" controller="emissaoFormulario" action="preencherFormulario" >
        <g:hiddenField name="idFormulario"/>
        <g:hiddenField name="idServico"/>
        <div id="familiaParaSelecao" style="display:inline">
            <g:render template="/emissaoFormulario/escolherFamilia-Selecionar" model="${[dtoFamiliaSelecionada: FamiliaController.getUltimaFamiliaAtualizaMembros(session)]}"/>
        </div>
    </g:form>
