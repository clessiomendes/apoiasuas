<%@ page import="org.apoiasuas.util.SimNao; org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
%>

%{--Eixbe os erros de validação--}%
<div class="erroValidacao">
    <g:if test="${localDtoFamilia.hasErrors() || erroReferencia}">
        <ul class="errors" role="alert">
            <g:eachError bean="${localDtoFamilia}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
            <g:if test="${erroReferencia}">
                <li data-field-id="nomeReferencia">O campo [Nome Completo da RF] não pode ser vazio</li>
            </g:if>
        </ul>
    </g:if>
</div>

<g:render template="/familia/detalhes/familia/basico" model="${[localDtoFamilia: localDtoFamilia]}"/>
<g:render template="/familia/detalhes/familia/publicoPrioritario" model="${[localDtoFamilia: localDtoFamilia]}"/>
<g:render template="/familia/detalhes/familia/despesas" model="${[localDtoFamilia: localDtoFamilia]}"/>
<g:render template="/familia/detalhes/familia/infraestrutura" model="${[localDtoFamilia: localDtoFamilia]}"/>
<g:render template="/familia/detalhes/familia/outros" model="${[localDtoFamilia: localDtoFamilia]}"/>
