<%@ page import="org.apoiasuas.marcador.AssociacaoMarcador; org.apoiasuas.marcador.Marcador" %>
<%
    org.apoiasuas.marcador.AssociacaoMarcador localDtoMarcador = marcador;
%>

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<ol class="property-list monitoramento" style="padding: 0; margin: 0;">

    <li class="fieldcontain">
        <span class="property-value">
            Definido ${localDtoMarcador.data ? "em "+localDtoMarcador.data.format("dd/MM/yyyy") : ""}
            ${localDtoMarcador.tecnico ? "por "+localDtoMarcador.tecnico.username : ""}
        </span>
    </li>

    <g:if test="${localDtoMarcador?.observacao}">
        <li class="fieldcontain">
            <span id="obs-label" class="property-label">Observações</span>
            <span class="property-value" aria-labelledby="obs-label">${localDtoMarcador.observacao}</span>
        </li>
    </g:if>

</ol>

<input type="button" id="btnCancelar" onclick="janelaModalMarcadores.cancelada();" value="Fechar" class="cancel" style="margin-top: 20px;"/>
