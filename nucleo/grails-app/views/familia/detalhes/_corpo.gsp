<%@ page import="org.apoiasuas.cidadao.FamiliaDetalhadoController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>

<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>

<g:form>
    <fieldset id="formPrincipal" class="form">
        %{-- Montagem do formulário de dados da família --}%
        <div id="divFamilia" class="forms-detalhados">
            <g:if test="${formFamilia}">
                <g:render template="${formFamilia}"/>
            </g:if>
            <g:else>
                <g:render template="/familia/detalhes/familia/formFamilia"/>
            </g:else>
        </div>

    %{-- Montagem dos formulários para cada membro JA EXISTENTE --}%
        <g:each in="${localDtoFamilia.getMembrosOrdemPadrao(null)}" var="cidadao" status="i">
            <div id="divMembros[${i}]" class="forms-detalhados forms-cidadao hidden">
            <g:if test="${formCidadao}">
                <g:render template="${formCidadao}"
                          model="[cidadaoInstance: cidadao, ordForm: i]"/>
            </g:if>
            <g:else>
                <g:render template="/familia/detalhes/cidadao/formCidadao"
                          model="[cidadaoInstance: cidadao, ordForm: i]"/>
            </g:else>
            </div>
        </g:each>

%{--
        <div id="divMarcadores" class="hidden forms-detalhados">
            <h1>to do... Marcadores</h1>
        </div>
--}%

    </fieldset>

    <fieldset class="buttons sticky-footer">
        <g:actionSubmit id="btnCreate" class="save hidden" action="saveNew" value="Gravar" title="Permite gravar e continuar alterando o cadastro"
            onclick="this.form.action='${createLink(action:'saveNew')}'; submitCriacao(this); return false;"/>

        <g:submitToRemote elementId="btnGravar" name="gravar" class="hidden save" value="Gravar"
                          title="Permite gravar e continuar alterando o cadastro"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          before="iniciaOverlayAjax(this); /*somente o botão receberá o efeito de bolinha rodando*/"
                          onSuccess="sucessoSave(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"
                          onComplete="terminaOverlayAjax(jQuery('#btnGravar'));"
                          />

        %{--O botão chama a action de gravacao e, EM PARALELO, abre uma nova aba para o novo membro --}%
                          %{--before="noOverlay = true;"--}%
        <g:submitToRemote elementId="btnAdicionarCidadao" class="hidden btn-adicionar-cidadao" value="Novo membro"
                          title="Adicionar mais um membro ao cadastro"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          before="iniciaOverlayAjax(this); /*somente o botão receberá o efeito de bolinha rodando*/"
                          after="novoMembro();"
                          onComplete="terminaOverlayAjax(jQuery('#btnAdicionarCidadao'));"
                          onSuccess="sucessoSave(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"/>

        %{--O botão imprimir, na verdade chama a action de gravacao e, em caso de sucesso, faz o download do formulario em seguida --}%
        <g:submitToRemote elementId="btnImprimir" name="imprimir" class="hidden print" value="Imprimir"
                          title="Gera formulário de cadastro para impressão (e grava eventuais alterações)"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          before="iniciaOverlayAjax(this); /*somente o botão receberá o efeito de bolinha rodando*/"
                          onComplete="terminaOverlayAjax(jQuery('#btnImprimir'));"
                          onSuccess="sucessoImprimir(data);"
                          onFailure="erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);"/>

        %{--O botão concluir, na verdade chama a action de gravacao e, em caso de sucesso, redireciona para a pagina de emissao de formularios --}%
                          %{--before="iniciaOverlayAjax(this); /*somente o botão receberá o efeito de bolinha rodando*/"--}%
                          %{--onComplete="terminaOverlayAjax(jQuery('#btnConcluir'));"--}%
        <g:submitToRemote elementId="btnConcluir" class="hidden btn-concluir" value="Concluir"
                          title="Conclui as alteraçoes no cadastro (e grava eventuais alterações)"
                          url="[action: 'save', id: localDtoFamilia.id]"
                          onSuccess='sucessoConcluir(data);'
                          onFailure='erroSave(XMLHttpRequest.status, XMLHttpRequest.responseText);'/>

    </fieldset>
</g:form>

%{--Formulário base (a ser clonado) para novos membros. FORA DO FORM PRA NAO ENTRAR NO SUBMIT--}%
<g:custom elemento="div" showif="${modoContinuarCriacao || modoEdicao}"
          id="divNovoMembro" class="forms-detalhados forms-cidadao hidden">
    <g:render template="/familia/detalhes/cidadao/formCidadao"
              model="[cidadaoInstance: new Cidadao(), ordForm: 'new']"/>
</g:custom>
