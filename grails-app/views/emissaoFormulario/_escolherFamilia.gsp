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
        if (document.getElementById("cad").value != '') {
            ${remoteFunction(controller: 'emissaoFormulario', action: 'familiaParaSelecao', method: "post", update: "familiaParaSelecao", params: [cad: org.apoiasuas.cidadao.FamiliaController.getUltimaFamilia(session)?.cad])}
            document.getElementById("cad").select();
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
        <g:radio name="familiaCadastrada" value="false" onclick="limparFamilia();"/> Família sem cadastro
        <br>
        <g:radio name="familiaCadastrada" value="true" checked="true"/> Família cadastrada:
        <g:textField autofocus="true" name="cad" id="cad" size="2" value="${FamiliaController.getUltimaFamilia(session)?.cad}"/>
%{--TODO: Formatar botão ok--}%
        <g:actionSubmit action="familiaParaSelecao" id="btnSelecionarFamilia" value="ok"/>
    </g:formRemote>

    <br>

%{--
    Este form precisa ser "submetido" por uma chamada externa da pagina que inclui este template, por um javascript como:
    document.getElementById('preencherFormulario').submit();
--}%
    <g:form id="preencherFormulario" name="preencherFormulario" controller="emissaoFormulario" action="preencherFormulario" >
        <g:hiddenField name="idFormulario"/>
        <g:hiddenField name="idServico"/>
        <div id="familiaParaSelecao" style="display:inline"></div>
    </g:form>
