var conteudoEncaminhamentoAlterado = false;

/**
* Funcao executada apos a chamada ajax para preencher os campos do formulario com os dados do servico escolhido
*/
function dadosServico(data) {
    if (data)
        $('#divAnexarFichaServico').show()
    else
        $('#divAnexarFichaServico').hide();

    if (data && data.nomeFormal)
        $("#avulso\\.destino").val(data.nomeFormal);
    $("#liDescricao .property-value").text(data && data.descricao ? data.descricao : '');
    $("#liPublico .property-value").text(data && data.publico ? data.publico : '');

    $("#liUltimaVerificacao .property-value").text(data && data.ultimaVerificacaoStr ? data.ultimaVerificacaoStr : '');
    $("#liDocumentos .property-value").text(data && data.documentos ? data.documentos : '');
    $("#liFluxo .property-value").text(data && data.fluxo ? data.fluxo : '');
    $("#liTelefones .property-value").text(data && data.telefones ? data.telefones : '');
    $("#liSite .property-value").text(data && data.site ? data.site : '');
    $("#liEnderecos .property-value").text(data && data.enderecos ? data.enderecos : '');

    atualizaSelectEnderecos(data);

    if (data) {
        //Antes de atualizar a descricao do encaminhamento, pede uma confirmação (se a mesma tiver sido alterado pelo operador)
        var alterar = true;
        if ($("#avulso\\.descricao_encaminhamento").val() && conteudoEncaminhamentoAlterado)
            alterar = confirm("Sobrescrever o detalhamento do encaminhamento com o padrão para " + data.apelido + "?");
        if (alterar) {
            $("#avulso\\.descricao_encaminhamento").val(data.encaminhamentoPadrao);
            conteudoEncaminhamentoAlterado = false;
        }
    }

    clickAnexoFichaServico($("#checkAnexoFichaServico"), data ? false : true);
}

/**
 * preenchimento do select de enderecos aa partir do campo memo de enderecos
 */
function atualizaSelectEnderecos(data) {
    var $selectEndereco = $('#selectEndereco');
    $selectEndereco.editableSelect('clear');
    if (data && data.enderecos)
        data.enderecos.split(/\r?\n/).forEach(function (linhaEndereco) {
            if (linhaEndereco.trim() != '') {
                $selectEndereco.editableSelect('add', linhaEndereco);
            }
        });

    //Chrome 63 is ignoring autocomplete="off"  https://github.com/jackocnr/intl-tel-input/issues/668
    var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
    if (isChrome)
        $selectEndereco.attr('autocomplete', 'nope');
}

/**
 * Exibe ou esconde o conteudo do fieldset da ficha do servico a ser anexada ao encaminhamento
 */
function clickAnexoFichaServico(checkbox, forceUncheck) {
    if (forceUncheck || ! $(checkbox).is(':checked'))
        $('#fieldsetAnexoFichaServico ol').slideUp(300)
    else
        $('#fieldsetAnexoFichaServico ol').slideDown(300);
}

/**
 * Retorna verdadeiro caso a tela de emissão deste formulário esteja sendo reaberta uma segunda vez, geralmente,
 * como consequencia do botão voltar do browser
 * @param idFormularioEmitido - usado para testar, via cookie se o formulário já foi emitido
 * @returns {boolean}
 */
function testaFormularioJaAberto(idFormularioEmitido) {
    console.debug(idFormularioEmitido);
    var cookieFormularioEmitido = cookie.get("encaminhamento.fomrularioEmitido"+idFormularioEmitido);
    if (! cookieFormularioEmitido) {
        console.debug('primeiro acesso');
        cookie.set("encaminhamento.fomrularioEmitido"+idFormularioEmitido, true);
        return false;
    } else {
        console.debug('reacesso');
        return true;
    }
}