<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>

<div class="buttons">
    <nobr>Nome ou Cad: <g:textField name="nomeOuCad" id="inputNomeOuCad" size="23" autofocus=""
                              onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"
                              value="${defaultNomePesquisa}"/></nobr>
    <nobr>Endereço: <g:textField name="logradouro" id="inputLogradouro" size="23"
                           onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/></nobr>
    <nobr>Nº <g:textField name="numero" id="inputNumero" size="1"
                 onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/></nobr>
    <input id="btnProcurarCidadao" type="button" class="speed-button-procurar"
            onclick="linkProcurarCidadaoPopup(janelaModalProcurarCidadao, '${createLink(action: actionButtonProcurarPopup, controller: controllerButtonProcurarPopup)}');"/>
    <br>
    <nobr>Idade: <g:textField name="idade" id="inputIdade" size="2"
                        onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/></nobr>
    <nobr>NIS: <g:textField name="nis" id="inputNis" size="10"
                        onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/></nobr>
    <nobr>Programa: <g:select name="programa" id="inputPrograma" noSelection="${['':'']}" from="${programas.collect{it.descricao}}"
                              keys="${programas.collect{it.id}}"/></nobr>
    <nobr>Nome de outro membro na mesma familia: <g:textField name="outroMembro" id="inputOutroMembro" size="23"
                               onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/></nobr>
</div>

<div id="list-cidadao" class="content scaffold-list" role="main" style="overflow-y: scroll; overflow-x: hidden; max-height: 400px;">
    <g:if test="${cidadaoInstanceCount}">
        <g:render template="/cidadao/tabelaListagem" model="[popup: true]" />
        <g:custom elemento="spam" showif="${cidadaoInstanceCount > cidadaoInstanceList.size()}">
            obs: exibindo apenas os ${cidadaoInstanceList.size()} primeiros registros de um total de ${cidadaoInstanceCount}. Refine sua busca.
        </g:custom>
    </g:if>
    <g:else>
        <div style="margin: 10px">Nenhum resultado encontrado.</div>
    </g:else>
</div>
