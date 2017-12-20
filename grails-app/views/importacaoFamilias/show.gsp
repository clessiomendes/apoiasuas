<%@ page import="org.apoiasuas.importacao.TentativaImportacao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'tentativaImportacao.label', default: 'TentativaImportacao')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-tentativaImportacao" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                          default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-tentativaImportacao" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list tentativaImportacao">

        <g:if test="${dtoTentatviaImportacao?.status}">
            <li class="fieldcontain">
                <span id="status-label" class="property-label"><g:message code='tentativaImportacao.statusDetalhado.label'/></span>

                <span class="property-value" aria-labelledby="status-label">
                    <g:fieldValue bean="${dtoTentatviaImportacao}" field="statusDetalhado"/>
                    <g:if test="${dtoTentatviaImportacao?.emAndamento}">
                        <g:link action="show" id="${dtoTentatviaImportacao?.id}">Atualizar</g:link>
                    </g:if>
                </span>

            </li>
        </g:if>

        <g:if test="${dtoTentatviaImportacao?.dateCreated}">
            <li class="fieldcontain">
                <span id="dateCreated-label" class="property-label">
                    <g:message code="tentativaImportacao.dateCreated.label" default="Início"/>
                </span>

                <span class="property-value" aria-labelledby="dateCreated-label">
                    <g:formatDate format="${org.apoiasuas.util.ApoiaSuasDateUtils.FORMATO_DATA_HORA}" date="${dtoTentatviaImportacao?.dateCreated}"/>
                </span>

            </li>
        </g:if>

        <g:if test="${dtoTentatviaImportacao?.lastUpdated}">
            <li class="fieldcontain">
                <span id="lastUpdated-label" class="property-label">
                    <g:message code="tentativaImportacao.lastUpdated.label"/>
                </span>

                <span class="property-value" aria-labelledby="lastUpdated-label">
                    <g:formatDate format="${org.apoiasuas.util.ApoiaSuasDateUtils.FORMATO_DATA_HORA}" date="${dtoTentatviaImportacao?.lastUpdated}"/>
                </span>

            </li>
        </g:if>

        <g:if test="${dtoTentatviaImportacao?.linhasPreProcessadas}">
            <li class="fieldcontain">
                <span id="linhasPreProcessadas-label" class="property-label">
                    <g:message code="tentativaImportacao.linhasPreProcessadas.label"/>
                </span>

                <span class="property-value" aria-labelledby="linhasPreProcessadas-label">
                    <g:fieldValue bean="${dtoTentatviaImportacao}" field="linhasPreProcessadas"/>
                </span>

            </li>
        </g:if>

        <g:if test="${dtoTentatviaImportacao?.linhasProcessadasConclusao}">
            <li class="fieldcontain">
                <span id="linhasProcessadasConclusao-label" class="property-label">
                    <g:message code="tentativaImportacao.linhasProcessadasConclusao.label"/>
                </span>

                <span class="property-value" aria-labelledby="linhasProcessadasConclusao-label">
                    <g:fieldValue bean="${dtoTentatviaImportacao}" field="linhasProcessadasConclusao"/>
                </span>

            </li>
        </g:if>

        <g:if test="${resumoImportacaoDTO}">
            <li class="fieldcontain">
                <span id="informacoesDoProcessamento-label" class="property-label">
                    <g:message code="tentativaImportacao.informacoesDoProcessamento.label"/>
                </span>

                <span class="property-value" aria-labelledby="informacoesDoProcessamento-label">
                    Novas famílias: ${resumoImportacaoDTO.novasFamilias} <br>
                    Famílias atualizadas: ${resumoImportacaoDTO.familiasAtualizadas} <br>
                    Famílias ignoradas (alteradas após importação): ${resumoImportacaoDTO.familiasIgnoradas} <br>
                    Novos cidadãos: ${resumoImportacaoDTO.novosCidadaos} <br>
                    Cidadãos atualizados: ${resumoImportacaoDTO.cidadaosAtualizados} <br>
                    Cidadãos ignorados (alterados após importação): ${resumoImportacaoDTO.cidadaosIgnorados} <br>
                    <g:if test="${resumoImportacaoDTO.familiasComErros || resumoImportacaoDTO.cidadaosComErros}">
                        <div style="color:red">
                            Famílias com erros: ${resumoImportacaoDTO.familiasComErros} <br>
                            Cidadãos com erros: ${resumoImportacaoDTO.cidadaosComErros}
                        </div>
                    </g:if>
                </span>
            </li>
        </g:if>
        <g:elseif test="${dtoTentatviaImportacao?.informacoesDoProcessamento}">
            <li class="fieldcontain">
                <span id="informacoesDoProcessamento-label" class="property-label">
                    <g:message code="tentativaImportacao.informacoesDoProcessamento.label" default="Informacoes Do Processamento"/>
                </span>
                <span class="property-value" aria-labelledby="informacoesDoProcessamento-label">
                    ${dtoTentatviaImportacao?.informacoesDoProcessamento}/>
                </span>
            </li>
        </g:elseif>

    </ol>
</div>

<g:if test="${resumoImportacaoDTO?.inconsistencias}">
    <g:render template="showInconsistencias"/>
</g:if>

</body>
</html>
