<%@ page import="org.apoiasuas.cidadao.CidadaoController; org.apoiasuas.AncestralController" %>
<g:if test="${dtoFamiliaSelecionada}">
    <g:hiddenField name="familiaSelecionada" id="familiaSelecionada" value="${dtoFamiliaSelecionada.id}"/>
        <div class="fieldcontain" style="margin-top: 0.7em">
            <label>Referência:</label>
            ${fieldValue(bean: dtoFamiliaSelecionada, field: "referencia")}
        </div>

        <div class="fieldcontain">
            <label for="membroSelecionado">membro:</label>
            <g:select name="membroSelecionado" id="membroSelecionado"
                      from="${dtoFamiliaSelecionada.membrosOrdemAlfabetica*.nomeCompleto}"
                      keys="${dtoFamiliaSelecionada.membrosOrdemAlfabetica*.id}"
                      value="${org.apoiasuas.cidadao.CidadaoController.getUltimoCidadao(session)?.id}"
                      noSelection="['-1': 'sem cadastro']"/>
        </div>
</g:if>
<g:else>
    <ul class="errors" role="alert">
            <li>Familia não encontrada</li>
    </ul>
</g:else>
