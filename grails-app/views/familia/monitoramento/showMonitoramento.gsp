<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis" %>
<%
    org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance;
%>

<script>
    function editMonitoramento() {
        janelaModalMonitoramentos.abreJanela({titulo: "alterar monitoramento", refreshFunction: updateList,
            url: "${createLink(action: 'editMonitoramento', id: localDtoMonitoramento.id)}"});
    }

    function efetivaMonitoramento() {
        if (confirm("Confirma efetivação desta ação nesta data?"))
            ${remoteFunction(action: 'efetivaMonitoramento', id: localDtoMonitoramento.id,
                onFailure: 'janelaModalMonitoramentos.loadHTML(XMLHttpRequest.responseText);', onSuccess: 'janelaModalMonitoramentos.confirmada();')};
    }

    function deleteMonitoramento() {
        if (confirm("${message(code: 'default.button.delete.confirm.message')}"))
            ${remoteFunction(action: 'deleteMonitoramento', id: localDtoMonitoramento.id,
                onFailure: 'janelaModalMonitoramentos.loadHTML(XMLHttpRequest.responseText);', onSuccess: 'janelaModalMonitoramentos.confirmada();')};
    }

    function suspendeMonitoramento() {
        if (confirm("Confirma suspensão deste monitoramento (você pode retomá-lo futuramente se precisar)?"))
            ${remoteFunction(action: 'suspendeMonitoramento', id: localDtoMonitoramento.id,
                onFailure: 'janelaModalMonitoramentos.loadHTML(XMLHttpRequest.responseText);', onSuccess: 'janelaModalMonitoramentos.confirmada();')};
    }
    //# sourceURL=showMonitoramento
</script>

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<ol class="property-list monitoramento" style="padding: 0; margin: 0;">

    <g:if test="${localDtoMonitoramento?.memo}">
        <li class="fieldcontain">
            <span id="descricao-label" class="property-label">Descrição <g:helpTooltip chave="help.descricao.monitoramento" /></span>
            <span class="property-value" aria-labelledby="descricao-label"><g:fieldValue bean="${localDtoMonitoramento}" field="memo"/></span>
        </li>
    </g:if>

    <g:if test="${localDtoMonitoramento?.responsavel}">
        <li class="fieldcontain">
            <span id="tecnico-label" class="property-label">Técnico <nobr>responsável
                <g:helpTooltip chave="help.responsavel.monitoramento"/></nobr></span>
            %{--<span class="property-value" aria-labelledby="situacao-label">${localDtoMonitoramento?.situacao}</span>--}%
            <span class="property-value" aria-labelledby="tecnico-label">${localDtoMonitoramento.responsavel.username}</span>
        </li>
    </g:if>

    <g:if test="${localDtoMonitoramento?.dataCriacao}">
        <li class="fieldcontain">
            <span id="criacao-label" class="property-label">Data de <nobr>criação
                <g:helpTooltip chave="help.data.criacao.monitoramento" /></nobr>
            </span>
            <span class="property-value" aria-labelledby="criacao-label"><g:formatDate date="${localDtoMonitoramento?.dataCriacao}" format="dd/MM/yyyy" /></span>
        </li>
    </g:if>

    <g:if test="${localDtoMonitoramento?.prioritario == true}">
        <li class="fieldcontain">
            <span class="property-label"></span>
            <span class="property-value"><b>prioritário</b></span>
        </li>
    </g:if>

    <li class="fieldcontain">
        <span id="situacao-label" class="property-label">Situação</span>
        <span class="property-value" aria-labelledby="situacao-label">${localDtoMonitoramento?.situacao}</span>
    </li>

</ol>

<fieldset class="buttons">
    <g:if test="${! localDtoMonitoramento.efetivado}">
        <a href="javascript:void(0)" class="check" title="${message(code: 'help.efetivado.monitoramento')}" onclick="efetivaMonitoramento();">Ação executada</a>
    </g:if>
    <a href="javascript:void(0)" class="edit" onclick="editMonitoramento();">Alterar</a>
    <g:if test="${! localDtoMonitoramento.efetivado && ! localDtoMonitoramento.suspenso}">
        <a href="javascript:void(0)" class="cancel" title="${message(code: 'help.suspenso.monitoramento')}" onclick="suspendeMonitoramento();">Suspender</a>
    </g:if>
    <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
        <a href="javascript:void(0)" class="delete" title="Apaga definitvamente o monitoramento" onclick="deleteMonitoramento();">${message(code: 'default.button.delete.label', default: 'Remover')}</a>
    </sec:ifAnyGranted>
    <a href="javascript:void(0)" class="close" onclick="janelaModalMonitoramentos.cancelada();">Fechar</a>
</fieldset>
