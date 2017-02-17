<%@ page import="org.apoiasuas.marcador.Marcador" %>
<asset:javascript src="especificos/marcadores.js"/>

<g:javascript>
/**
 * Customizacao da janela a ser aberta
 */
$(document).ready(function() {
    var divPrincipal = newInterfaceDivEditMarcador(${idPrincipal});

    divPrincipal.initDialog();

//Inicialização dos eventos (onclick, onkeyup, etc)

    $(divPrincipal).find("#btnConfirmar").click( function() {
        divPrincipal.confirmarEditMarcadorDialog();
    } );

    $(divPrincipal).find("#btnCancelar").click( function() {
        divPrincipal.cancelarEditMarcadorDialog();
    } );

%{--
    $(divPrincipal).find("#btnConfirmar").click( function() {
        divPrincipal.confirmarNovoMarcadorDialog('${hiddenNovosMarcadores}',
                ${fieldsetMarcadores}, '${classeMaracadores}');
    } );


    $(divPrincipal).find("#inputDescricaoMarcador").keyup( function() {
        divPrincipal.marcadoresSimilaresComDelay(this)
    } );
--}%

    } );
</g:javascript>

<div id='${idPrincipal}' title='${tituloJanela}' style="height: auto">
    <g:form onsubmit="return false;">
        <span id="descricaoMarcador"></span>

        <div class="fieldcontain">
            <label for="inputObservacaoMarcador">Observacao</label>
            <g:textField name="inputObservacaoMarcador" id="inputObservacaoMarcador" size="40" maxlength="255"/>
        </div>

        <div class="fieldcontain">
            <label for="inputTecnicoMarcador">
                Técnico logado <span class="required-indicator">*</span>
            </label>
            <g:select name="inputTecnicoMarcador" from="${operadores}" optionKey="id" class="many-to-one"
                      noSelection="['': '']"/>
        </div>

        <input type="button" id="btnConfirmar" value="Confirmar" class="save" style="margin-top: 20px;"/>
        &nbsp;&nbsp;<input type="button" id="btnCancelar" value="Cancelar" class="cancel" style="margin-top: 20px;"/>
    </g:form>
</div>