<%@ page import="org.apoiasuas.marcador.Marcador" %>
<asset:javascript src="familia/marcador/marcadores.js"/>

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
<fieldset class="form">

    <g:form onsubmit="return false;">
%{--
        <span id="descricaoMarcador"></span>

        <br><br>
--}%

        <div class="fieldcontain">
            <label>Descrição</label>
            <span id="descricaoMarcador" class="property-value"></span>
        </div>

        <div class="fieldcontain">
            <label for="inputTecnicoMarcador">
                Técnico logado <span class="required-indicator">*</span>
            </label>
            <g:select name="inputTecnicoMarcador" from="${operadores}" optionKey="id" class="many-to-one"
                      noSelection="['': '']"/>
        </div>

        <div class="nova-linha"></div>

        <div class="fieldcontain">
            <label for="inputObservacaoMarcador">Observacao</label>
            <g:textField name="inputObservacaoMarcador" id="inputObservacaoMarcador" size="40" maxlength="255"/>
        </div>

    </g:form>

</fieldset>

<fieldset class="buttons">
        <a href="javascript:void(0)" id="btnConfirmar" class="save">Confirmar</a>
        <a href="javascript:void(0)" id="btnCancelar" class="close">Cancelar</a>
</fieldset>

</div>

