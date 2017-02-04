<%@ page import="org.apoiasuas.cidadao.Marcador" %>
<asset:javascript src="especificos/marcadores.js"/>

<g:javascript>
/**
 * Customizacao da janela a ser aberta
 */
$(document).ready(function() {
    var divPrincipal = newInterfaceDivNovoMarcador(${idPrincipal});

    divPrincipal.initDialog();

//Inicialização dos eventos (onclick, onkeyup, etc)
    $(divPrincipal).find("#btnConfirmar").click( function() {
        divPrincipal.confirmarNovoMarcadorDialog('${hiddenNovosMarcadores}',
                ${fieldsetMarcadores}, '${classeMaracadores}');
    } );

    $(divPrincipal).find("#btnCancelar").click( function() {
        divPrincipal.janelaNovoMarcador(false);
    } );

    $(divPrincipal).find("#inputDescricaoMarcador").keyup( function() {
        divPrincipal.marcadoresSimilaresComDelay(this)
    } );
} );
</g:javascript>

<div id='${idPrincipal}' title='${tituloJanela}' style="height: auto">
    <g:form onsubmit="return false;">
        <div class="fieldcontain">
            <label for="inputDescricaoMarcador">Descrição</label>
            <input type="text" id="inputDescricaoMarcador" size="40" maxlength="100">
        </div>

        <div id="marcadoresDisponiveisDialog" style="display: none">
            <g:each in="${marcadoresDisponiveis}" var="marcadordisp">
                <span class="marcadores-similares">
                    <% Marcador marcadorDisponivel = marcadordisp; %>
                    ${marcadorDisponivel.descricao}
                </span>
            </g:each>
        </div>
        <fieldset id="fieldsetMarcadoresFiltradosDialog" class="fieldsetMarcadores">
            <legend>Descriçõs similares já no sistema</legend>
        </fieldset>

        <input type="button" id="btnConfirmar" value="Confirmar" class="create" style="margin-top: 20px;" />
        &nbsp;&nbsp;<input type="button" id="btnCancelar" value="Cancelar" class="cancel" style="margin-top: 20px;" />
    </g:form>
</div>