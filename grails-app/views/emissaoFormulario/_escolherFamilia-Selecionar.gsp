<%@ page import="org.apoiasuas.AncestralController" %>
<g:if test="${dtoFamiliaSelecionada}">
    <g:hiddenField name="familiaSelecionada" id="familiaSelecionada" value="${dtoFamiliaSelecionada.id}"/>
        <div class="fieldcontain">
            <label>
                <g:message code="???" default="referência: "/>
            </label>
            ${fieldValue(bean: dtoFamiliaSelecionada, field: "referencia")}
        </div>

        <div class="fieldcontain">
            <label for="membroSelecionado">
                <g:message code="???" default="usuário: "/>
            </label>
            <g:select name="membroSelecionado" id="membroSelecionado"
                      from="${dtoFamiliaSelecionada.membrosOrdemAlfabetica*.nomeCompleto}"
                      keys="${dtoFamiliaSelecionada.membrosOrdemAlfabetica*.id}"
                      value="${session.getAttribute(org.apoiasuas.AncestralController.ULTIMO_CIDADAO)?.id}"
                      noSelection="['-1': 'sem cadastro']"/>
        </div>
</g:if>
<g:else>
    <ul class="errors" role="alert">
            <li>Familia não encontrada</li>
    </ul>
</g:else>
