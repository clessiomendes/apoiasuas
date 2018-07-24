<%@ page import="org.apoiasuas.formulario.CampoFormulario; org.apoiasuas.formulario.definicao.FormularioEncaminhamento; org.apoiasuas.InicioController; org.apoiasuas.redeSocioAssistencial.Servico; org.apoiasuas.formulario.Formulario" %>
<%
    org.apoiasuas.formulario.Formulario localDtoFormulario = dtoFormulario
%>

<asset:javascript src="/emissaoFormulario/encaminhamento.js"/>
<asset:javascript src="cookie.js"/>
<asset:javascript src="select2/select2.js"/>
<asset:stylesheet src="select2/select2.css"/>
<asset:javascript src="editable-select/jquery-editable-select.js"/>
<asset:stylesheet src="editable-select/jquery-editable-select.css"/>

<g:javascript>
    /**
    * chamada ajax para obter os dados do cadastro do servico
    */
    function selectServicoChange() {
        if ($("#selectServico").val() != 'null') {
            //Se tiver servico selecionado, preenche os campos associados
            var paramsEncaminhamento = { "idServico" : escape($("#selectServico").val()), "idFamilia" : escape($('#familia\\.id').val()), "idCidadao" : escape($('#cidadao\\.id').val()) }
            <g:remoteFunction controller="servico" action="getServico" onSuccess="dadosServico(data)" params="paramsEncaminhamento"/>
        } else {
            //Se não, limpa os campos associados
            dadosServico(null);
        }
    }

    $(document).ready(function() {
        //evento para indicar alguma alteracao na descricao do encaminhamento (usado para perguntar antes de sobrescrever)
        $("#avulso\\.descricao_encaminhamento").on('keypress', function(){
            conteudoEncaminhamentoAlterado = true;
        })

        $('#selectServico').select2();
        $('#selectEndereco').editableSelect();
        //Sempre que carregar a página (exceto quando o botão back é acionado), submete a chamada ajax identica de selecao de servico
        if (! testaFormularioJaAberto(${localDtoFormulario.formularioEmitido.id})) {
            $('#selectEndereco').val('${localDtoFormulario.getCampo(FormularioEncaminhamento.CODIGO_CAMPO_ENDERECO_DESTINO).valorArmazenado}');
            selectServicoChange();
        }
    });
</g:javascript>

<style>
    #divAnexarFichaServico li {
        display: block;
    }
    #divAnexarFichaServico {
        margin-top: 1em;
        display: block;
    }
    img.checked {
        width: 50px;
        height: 50px;
    }
    li.ultima-verificacao {
        display: inline-block !important;
    }

    /* melhorias visuais no componente editable-select */
    ul.es-list {
        cursor: pointer;
    }
    input.es-input {
        background: none;
    }
/*
    span.ui-icon-close {
        /!* adaptando compoente clear-field para coexistir com o componente editable-select *!/
        right: 20px !important;
    }
*/
</style>

<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(FormularioEncaminhamento.CODIGO_CAMPO_NOME_COMPLETO)}" />
<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(FormularioEncaminhamento.CODIGO_CAMPO_CAD)}" />

<div class="fieldcontain">
    <label>
        Serviço, programa, projeto ou benefício
    </label>
%{--
    <span id="spanNomeServico" style="margin-right: 10px">${idServico ? Servico.get(idServico).apelido : 'nenhum serviço selecionado'}</span>
    <g:hiddenField name="idServico" value="${idServico}"/>
    <button class="speed-button-procurar" onclick="procurarServico()"/>
--}%
    <g:select optionKey='id' optionValue="apelido" name="idServico" id="selectServico"
              from="${org.apoiasuas.redeSocioAssistencial.Servico.list().sort({it.apelido})}"
              noSelection="['null': '.']"
              class="js-example-theme-single"
              value="${idServico && idServico != 'null' ? new Long(idServico) : ''}" style="max-width:400px;"
              onchange="selectServicoChange()"/>
</div>

<br>

<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(FormularioEncaminhamento.CODIGO_CAMPO_DESTINO)}" focoInicial="true"/>
%{--<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(FormularioEncaminhamento.CODIGO_CAMPO_ENDERECO_DESTINO)}" />--}%
<div class="fieldcontain">
    <label>Endereço</label>
    <select id="selectEndereco" name="avulso.${FormularioEncaminhamento.CODIGO_CAMPO_ENDERECO_DESTINO}" style="width:600px;"></select>
</div>
<br>
<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(FormularioEncaminhamento.CODIGO_CAMPO_DESCRICAO_ENCAMINHAMENTO)}" />
<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(CampoFormulario.CODIGO_DATA_PREENCHIMENTO)}" />
<g:divCampoFormulario campoFormulario="${localDtoFormulario.getCampo(CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO)}" />

<br>
<div id="divAnexarFichaServico" class="fieldcontain">
    <fieldset id="fieldsetAnexoFichaServico" class="embedded">
        <legend>
            <g:checkBox id="checkAnexoFichaServico" name="anexoFichaServico" checked="${erroValidacao ? (anexoFichaServico ? true : false) : true}" onchange="clickAnexoFichaServico(this);"/>
            <span class="${InicioController.novoRecurso("31/08/2018","novo-recurso")}">Anexar ficha descritiva do serviço</span>
        </legend>
        <ol class="property-list">
            <asset:image src="check-gray.png" class="checked"/>
            <g:campoExibicao id="liUltimaVerificacao" escondeVazio="false" titulo="Informações de" classeCss="ultima-verificacao"/>
            <g:campoExibicao id="liDescricao" escondeVazio="false" titulo="Descrição detalhada"/>
            <g:campoExibicao id="liPublico" escondeVazio="false" titulo="Restrição do público atendido"/>
            <g:campoExibicao id="liDocumentos" escondeVazio="false" titulo="Documentos necessários"/>
            <g:campoExibicao id="liEnderecos" escondeVazio="false" titulo="Endereços e horários de atendimento"/>
            <g:campoExibicao id="liFluxo" escondeVazio="false" titulo="Fluxo do atendimento"/>
            <g:campoExibicao id="liTelefones" escondeVazio="false" titulo="Telefones de atendimento ao público"/>
            <g:campoExibicao id="liSite" escondeVazio="false" titulo="Site na internet"/>
        </ol>
    </fieldset>
</div>

