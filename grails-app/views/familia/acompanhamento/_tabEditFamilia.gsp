<%@ page import="org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
%>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'tecnicoReferencia', 'error')} ">
    <label for="tecnicoReferencia">
        <g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" />
    </label>
    <g:select id="tecnicoReferencia" name="tecnicoReferencia.id" from="${operadores}" optionKey="id" value="${localDtoFamilia?.tecnicoReferencia?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<br>

<g:if test="${localDtoFamilia?.referencia}">
    <div class="fieldcontain">
        <div class="property-label">Referência familiar</div>
        <div class="property-value">${localDtoFamilia.getReferencia().nomeCompleto}</div>
    </div>
</g:if>

<g:if test="${localDtoFamilia?.referencia}">
    <div class="fieldcontain">
        <div class="property-label">
            <g:message code="familia.codigoLegado.label" default="Codigo Legado" />
        </div>
        <div class="property-value">${localDtoFamilia.codigoLegado}</div>
    </div>
</g:if>

<br>

<g:if test="${localDtoFamilia?.endereco?.obtemEnderecoCompleto()}">
    <div class="fieldcontain">
        <div class="property-label">Endereço</div>
        <div class="property-value">${localDtoFamilia.endereco.obtemEnderecoCompleto()}</div>
    </div>
</g:if>

