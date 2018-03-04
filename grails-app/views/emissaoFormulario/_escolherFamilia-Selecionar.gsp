<%@ page import="org.apoiasuas.cidadao.CidadaoController; org.apoiasuas.AncestralController" %>
<g:if test="${dtoFamiliaSelecionada}">
    <g:hiddenField name="familiaSelecionada" id="familiaSelecionada" value="${dtoFamiliaSelecionada.id}"/>
    %{--<div class="fieldcontain" style="margin-top: 0.7em">--}%
    <div class="fieldcontain">
        <label>Referência</label>
        <span class="property-value">${fieldValue(bean: dtoFamiliaSelecionada, field: "referencia")}</span>
    </div>

    <div class="fieldcontain">
        <label for="membroSelecionado">Membro</label>
        <g:select name="membroSelecionado" id="membroSelecionado" style="max-width: 20em"
                  from="${dtoFamiliaSelecionada.membrosOrdemAlfabetica*.nomeCompleto}"
                  keys="${dtoFamiliaSelecionada.membrosOrdemAlfabetica*.id}"
                  value="${org.apoiasuas.cidadao.CidadaoController.getUltimoCidadao(session)?.id}"
                  noSelection="['-1': 'sem cadastro']"/>
    </div>
</g:if>
