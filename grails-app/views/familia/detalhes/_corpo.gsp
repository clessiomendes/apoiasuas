<%@ page import="org.apoiasuas.cidadao.FamiliaDetalhadoController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>

<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>

<g:form action="edit" id="${localDtoFamilia.id}">
    <fieldset id="formPrincipal" class="form">
        %{-- Montagem do formulário de dados da família --}%
        <div id="divFamilia" class="forms-detalhados">
            <g:render template="/familia/detalhes/familia/formFamilia"/>
        </div>

    %{-- Montagem dos formulários para cada membro JA EXISTENTE --}%
        <g:each in="${localDtoFamilia.getMembrosOrdemPadrao(null)}" var="cidadao" status="i">
            <div id="divMembros[${i}]" class="forms-detalhados forms-cidadao hidden">
                <g:render template="/familia/detalhes/cidadao/formCidadao"
                          model="[cidadaoInstance: cidadao, ordForm: i]"/>
            </div>
        </g:each>

%{--
        <div id="divMarcadores" class="hidden forms-detalhados">
            <h1>to do... Marcadores</h1>
        </div>
--}%

    </fieldset>

    <fieldset class="buttons sticky-footer">
        <g:actionSubmit id="btnCreate" class="save hidden" action="saveNew" value="Gravar" title="Permite gravar e continuar alterando o cadastro" />

        <g:submitToRemote id="btnGravar" name="gravar" class="hidden save" value="Gravar"
                          title="Permite gravar e continuar alterando o cadastro"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          onSuccess="sucessoSave(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"/>

        %{--O botão chama a action de gravacao e, EM PARALELO, abre uma nova aba para o novo membro --}%
        <g:submitToRemote id="btnAdicionarCidadao" class="hidden btn-adicionar-cidadao" value="Novo membro"
                          title="Adicionar mais um membro ao cadastro"
                          before="noOverlay = true;"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          after="novoMembro();"
                          onSuccess="sucessoSave(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"/>

        %{--O botão imprimir, na verdade chama a action de gravacao e, em caso de sucesso, faz o download do formulario em seguida --}%
        <g:submitToRemote id="btnImprimir" name="imprimir" class="hidden print" value="Imprimir"
                          title="Gera formulário de cadastro para impressão (e grava eventuais alterações)"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          onSuccess="sucessoImprimir(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"/>

        %{--O botão concluir, na verdade chama a action de gravacao e, em caso de sucesso, redireciona para a pagina de emissao de formularios --}%
        <g:submitToRemote id="btnConcluir" class="hidden btn-concluir" value="Concluir"
                          title="Conclui as alteraçoes no cadastro (e grava eventuais alterações)"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          onSuccess="sucessoConcluir(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"/>

    </fieldset>
</g:form>

<g:javascript>
    function sucessoImprimir(data) {
        sucessoSave(data, "Família gravada com sucesso. Preparando download...", 6000);
        var destino = "${createLink(controller: 'familiaDetalhado', action: 'download', id: localDtoFamilia?.id)}";
        window.location = destino;

        //Desabilita o botão por 20 segundos, para evitar excesso de envios para o servidor
        var $btnImprimir = $('#btnImprimir');
        $btnImprimir.prop('disabled', true);
        setTimeout(function() { $btnImprimir.prop('disabled', false); }, 20000);
    }

    function sucessoConcluir(data) {
        sucessoSave(data);
        var destino = "${createLink(controller: 'emissaoFormulario', action: 'escolherFamilia')}";
        window.location = destino;
    }
</g:javascript>

%{--Formulário base (a ser clonado) para novos membros. FORA DO FORM PRA NAO ENTRAR NO SUBMIT--}%
<g:custom elemento="div" showif="${modoContinuarCriacao || modoEdicao}"
          id="divNovoMembro" class="forms-detalhados forms-cidadao hidden">
    <g:render template="/familia/detalhes/cidadao/formCidadao"
              model="[cidadaoInstance: new Cidadao(), ordForm: 'new']"/>
</g:custom>
