<%@ page import="org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>
<asset:javascript src="especificos/apoiasuas-modal.js"/>

<g:javascript>
    var janelaModalMarcador = new JanelaModalAjax();

    /**
     * Navega para nova tela de edição de marcadores (inclusão, remoção, alteração)
     */
    function editMarcadores() {
        location.href = "${createLink(action: 'editMarcadoresApenas', id: localDtoFamilia.id)}";
    }

    /**
     * Navega para nova tela de edição de referencia e parentescos
     */
    function trocaReferencia() {
        location.href = "${createLink(controller: 'referenciaFamiliar', action: 'edit', id: localDtoFamilia.id)}";
    }

    /**
     * Navega para nova tela de inclusão de membros
     */
    function novoMembro() {
        location.href = "${createLink(controller: 'cidadao', action: 'create', params: [idFamilia: localDtoFamilia.id])}";
    }
</g:javascript>

<ol class="property-list servico" style="padding: 0; margin: 0;">

%{--Exibe PROGRAMAS, AÇÕES E VULNERABILIADES--}%
%{--<g:if test="${localDtoFamilia?.programasHabilitados || localDtoFamilia?.acoesHabilitadas  || localDtoFamilia?.vulnerabilidadesHabilitadas  || localDtoFamilia?.outrosMarcadoresHabilitados}">--}%
    <fieldset id="fieldsetDadosEncaminhamento" class="embedded">
        <legend>
            Programas<g:helpTooltip chave="help.marcador.programas"/>,
            vulnerabilidades<g:helpTooltip chave="help.marcador.vulnerabilidades"/>,
            ações previstas<g:helpTooltip chave="help.marcador.acoes"/>
            e outras sinalizações<g:helpTooltip chave="help.marcador.outros.marcadores"/>

            <input id="editarMarcadores" type="button" class="btn-editar-marcadores" style="transform: scale(0.8);"
                   title="Clique para alterar estas definições (incluir, remover, etc)" onclick="editMarcadores();">
        </legend>
        <g:each in="${localDtoFamilia.programasHabilitados}" var="marcadorFamilia">
            <span class="marcadores-programa">
                <a href="javascript:void(0)" onclick='janelaModalMarcador.abreJanela("detalhes...","${createLink(action:'showPrograma', id: marcadorFamilia.id)}");'>
                    ${marcadorFamilia?.programa?.descricao}
                </a>
            </span>
        </g:each>
        <g:each in="${localDtoFamilia.vulnerabilidadesHabilitadas}" var="marcadorFamilia">
            <span class="marcadores-vulnerabilidade">
                <a href="javascript:void(0)" onclick='janelaModalMarcador.abreJanela("detalhes...","${createLink(action:'showVulnerabilidade', id: marcadorFamilia.id)}");'>
                    ${marcadorFamilia?.vulnerabilidade?.descricao}
                </a>
            </span>
        </g:each>
        <g:each in="${localDtoFamilia.acoesHabilitadas}" var="marcadorFamilia">
            <span class="marcadores-acao">
                <a href="javascript:void(0)" onclick='janelaModalMarcador.abreJanela("detalhes...","${createLink(action:'showAcao', id: marcadorFamilia.id)}");'>
                    ${marcadorFamilia?.acao?.descricao}
                </a>
            </span>
        </g:each>
        <g:each in="${localDtoFamilia.outrosMarcadoresHabilitados}" var="marcadorFamilia">
            <span class="marcadores-outro-marcador">
                <a href="javascript:void(0)" onclick='janelaModalMarcador.abreJanela("detalhes...","${createLink(action:'showOutroMarcador', id: marcadorFamilia.id)}");'>
                    ${marcadorFamilia?.outroMarcador?.descricao}
                </a>
            </span>
        </g:each>
    </fieldset>
%{--</g:if>--}%

<li class="fieldcontain">
    <span id="cad-label" class="property-label"><g:message code="familia.codigoLegado.label" default="Cad" /></span>
    <span class="property-value" aria-labelledby="cad-label"><g:fieldValue bean="${localDtoFamilia}" field="cad"/></span>
</li>

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

    %{--Lista membros habilitados--}%
    <li class="fieldcontain">
        <span id="membros-label" class="property-label"><g:message code="familia.membros.label" default="Membros" /></span>
        <g:each in="${localDtoFamilia.getMembrosOrdemPadrao(true)}" var="m">
            <span class="property-value" aria-labelledby="membros-label">
                <g:link controller="cidadao" action="show" id="${m.id}">${m?.nomeCompleto }</g:link>
                ${m.parentescoReferencia ? ", "+m.parentescoReferencia : ""}
                ${m.idade ? ", "+m.idade + " anos" : ""}
            </span>
        </g:each>
        <span class="property-value" aria-labelledby="membros-label">
            <sec:access acessoServico="${RecursosServico.INCLUSAO_MEMBRO_FAMILIAR}">
                <input id="novoMembro" type="button" class="create" style="margin: 5px 5px 5px 0"
                       title="Incluir um novo cidadão como membro desta família" value="Novo membro" onclick="novoMembro();">
            </sec:access>
            <input id="trocarReferencia" type="button" class="edit" style="margin: 5px 5px 5px 0"
                   title="Alterar a referência familiar e o parentesco entre os membros" value="Trocar referência" onclick="trocaReferencia();">
        </span>
    </li>

    %{--Lista membros removidos do grupo familiar--}%
    <g:set var="membrosDesabilitados" value="${localDtoFamilia?.getMembrosOrdemPadrao(false)}"/>
    <g:if test="${membrosDesabilitados}">
        <li class="fieldcontain">
            <span id="membros-removidos-label" class="property-label">Membros removidos</span>
            <g:each in="${membrosDesabilitados}" var="m">
                <span class="property-value" aria-labelledby="membros-removidos-label">
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

<fieldset class="buttons">
    <g:link class="add" controller="emissaoFormulario" action="escolherFamilia">Emitir formulário</g:link>
    <g:link class="list" controller="emissaoFormulario" action="listarFormulariosEmitidosFamilia" params="[idFamilia: familiaInstance.id]" >Formulários emitidos</g:link>
    <g:link class="edit" action="edit" resource="${familiaInstance}">Alterar dados</g:link>
    <g:link class="acompanhamento" controller="familia" action="editAcompanhamentoFamilia" id="${familiaInstance.id}">Acompanhamento</g:link>
</fieldset>
