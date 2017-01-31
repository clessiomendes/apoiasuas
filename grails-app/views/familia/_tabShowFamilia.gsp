<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>

<ol class="property-list servico" style="padding: 0; margin: 0;">

%{--Exibe PROGRAMAS, AÇÕES E VULNERABILIADES--}%
<g:if test="${localDtoFamilia?.programas || localDtoFamilia?.acoes}">
    <fieldset id="fieldsetDadosEncaminhamento" class="embedded">
        <legend>
            Programas<g:helpTooltip chave="help.marcador.programas"/>,
            vulnerabilidades<g:helpTooltip chave="help.marcador.vulnerabilidades"/> e
            ações previstas<g:helpTooltip chave="help.marcador.acoes"/>
        </legend>
        <g:each in="${localDtoFamilia.programas}" var="programaFamilia">
            <span class="marcadores-programa">${programaFamilia?.programa?.descricao}</span>
        </g:each>
        <g:each in="${localDtoFamilia.vulnerabilidades}" var="vulnerabilidadeFamilia">
            <span class="marcadores-vulnerabilidade">${vulnerabilidadeFamilia?.vulnerabilidade?.descricao}</span>
        </g:each>
        <g:each in="${localDtoFamilia.acoes}" var="acaoFamilia">
            <span class="marcadores-acao">${acaoFamilia?.acao?.descricao}</span>
        </g:each>
    </fieldset>
</g:if>

<g:if test="${localDtoFamilia?.codigoLegado}">
    <li class="fieldcontain">
        <span id="codigoLegado-label" class="property-label"><g:message code="familia.codigoLegado.label" default="Codigo Legado" /></span>
        <span class="property-value" aria-labelledby="codigoLegado-label"><g:fieldValue bean="${localDtoFamilia}" field="codigoLegado"/></span>
    </li>
</g:if>

<g:if test="${localDtoFamilia?.tecnicoReferencia}">
    <li class="fieldcontain">
        <span id="tecnicoReferencia-label" class="property-label"><g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" /></span>
        <span style="color: red" class="property-value" aria-labelledby="tecnicoReferencia-label"><g:fieldValue bean="${localDtoFamilia}" field="tecnicoReferencia"/></span>
    </li>
</g:if>

<g:if test="${localDtoFamilia?.dateCreated}">
    <li class="fieldcontain">
        <span id="dateCreated-label" class="property-label"><g:message code="familia.dateCreated.label" default="Data Cadastro" /></span>
        <span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${localDtoFamilia?.dateCreated}" /></span>
    </li>
</g:if>

<g:if test="${localDtoFamilia?.endereco}">
    <li class="fieldcontain">
        <span id="endereco-label" class="property-label"><g:message code="familia.endereco.label" default="Endereço" /></span>
        <span class="property-value" aria-labelledby="endereco-label"> ${localDtoFamilia.endereco} </span>
        <span class="property-value" aria-labelledby="endereco-label"> ${localDtoFamilia.endereco.CEP ? "CEP "+localDtoFamilia.endereco.CEP +", " : ""}
        ${localDtoFamilia.endereco.municipio ? localDtoFamilia.endereco.municipio +", " : ""}
        ${localDtoFamilia.endereco.UF ? localDtoFamilia.endereco.UF : ""}
        </span>
    </li>
</g:if>

<g:if test="${localDtoFamilia?.membros}">
    <li class="fieldcontain">
        <span id="membros-label" class="property-label"><g:message code="familia.membros.label" default="Membros" /></span>
        <g:each in="${localDtoFamilia.membros}" var="m">
            <span class="property-value" aria-labelledby="membros-label">
                <g:link controller="cidadao" action="show" id="${m.id}">${m?.nomeCompleto }</g:link>
                ${m.parentescoReferencia ? ", "+m.parentescoReferencia : ""}
                ${m.idade ? ", "+m.idade + " anos" : ""}
            </span>
        </g:each>
    </li>
</g:if>
<g:if test="${localDtoFamilia?.telefones}">
    <li class="fieldcontain">
        <span id="telefones-label" class="property-label"><g:message code="familia.telefones.label" default="Telefones" /></span>

        <g:each in="${localDtoFamilia.telefones}" var="t">
            <span class="property-value" aria-labelledby="telefones-label">${t?.encodeAsHTML()}</span>
        </g:each>

    </li>
</g:if>

</ol>