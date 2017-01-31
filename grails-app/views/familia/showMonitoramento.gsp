<%
    org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance;
%>

<script>
    function editMonitoramento() {
        abreJanela("alterar monitoramento","${createLink(action: 'editMonitoramento', id: monitoramentoInstance.id)}");
    }

    function deleteMonitoramento() {
        if (confirm("${message(code: 'default.button.delete.confirm.message')}"))
            ${remoteFunction(action: 'deleteMonitoramento', id: monitoramentoInstance.id,
                update: [failure: 'janelaMonitoramento'], onSuccess: 'fechaJanela();')};
    }
</script>

%{--
<div class="nav" role="navigation">
    <ul>
        <li><g:link class="list" controller="cidadao" action="procurarCidadao"><g:message message="Procurar"/></g:link></li>
    </ul>
</div>
--}%

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<ol class="property-list monitoramento" style="padding: 0; margin: 0;">

    <g:if test="${localDtoMonitoramento?.memo}">
        <li class="fieldcontain">
            <span id="codigoLegado-label" class="property-label">Descrição <g:helpTooltip chave="help.descricao.monitoramento" /></span>
            <span class="property-value" aria-labelledby="codigoLegado-label"><g:fieldValue bean="${localDtoMonitoramento}" field="memo"/></span>
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

    <li class="fieldcontain">
        <span id="situacao-label" class="property-label">Situação</span>
        <span class="property-value" aria-labelledby="situacao-label">${localDtoMonitoramento?.situacao}</span>
    </li>

</ol>

<fieldset class="buttons" style="margin-top: 10px">
    <a href="javascript:void(0)" class="edit" onclick="editMonitoramento();">Alterar dados</a>
%{--
    <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
--}%
    <a href="javascript:void(0)" class="delete" onclick="deleteMonitoramento();">${message(code: 'default.button.delete.label', default: 'Delete')}</a>
</fieldset>
