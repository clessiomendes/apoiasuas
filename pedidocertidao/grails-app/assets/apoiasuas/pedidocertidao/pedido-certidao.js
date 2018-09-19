var janelaModalProcurarCidadao = new JanelaModalAjax();

/**
 * Carregamento inicial da página
 */
$(document).ready(function() {
    autoComplete();
});

/**
 * cacela o submit do botao de enviar comentario caso o comentario esteja vazio
 */
function preEnviarComentario() {
    return $('#txtComentario').val().trim() != "";
}

/**
 * Simula o click no hyperlink responsavel por fazer o download do documento apos o usuario
 * acionar os botoes de imprimir declaracao de pobreza ou imprimir pedido
 */
function downloadCertidaoOuPedido() {
    $('#reportParaBaixar').click();
}

/**
 * acionado dos botos "comentario" e "cancelar comentario" para, exibir ou ocultar o form de envio
 * de comentario
 */
function habilitarComentario() {
    var $spanComentario = $('#spanComentario');
    //alterna exibir e ocultar
    var habilitar = $spanComentario.css('display') == 'none';
    if (habilitar) { //mostra e direciona o foco para iniciar digitacao do comentario
        $spanComentario.css('display', 'inline');
        $spanComentario.find('input[type=text]').focus();
    } else { //oculta
        $spanComentario.css('display', 'none');
    }
}

/**
 * emula 1) o envio do comentario quando pressionar enter no campo do comentario ou
 * 2) o cancelamento do comentario ao pressionar esc
 */
function txtComentarioKeyDown(event) {
    //diferentes formas de se referir aa tecla pressionada para diferentes tipos de browser
    var enterPressed  = (event.key=='Enter' || event.which==13 || event.keyCode==13);
    var escPressed = (event.key=='Escape' || event.key=='Esc' || event.which==27 || event.keyCode==27);

    if (enterPressed)
        $('#btnEnviarComentario').click();
    if (escPressed)
        $('#btnCancelarComentario').click();
    //retorna falso caso uma das duas teclas tenha sido pressionada
    return ! (enterPressed || escPressed);
}

/**
* Evento de callback, acionado da tela popup (_tabelaListagem.gsp) para sinalizar a familia selecionada
*/
function selecionaPopup(event, result) {
    event.preventDefault();
    if (result.idFamilia) {
        $('#hiddenIdFamilia').val(result.idFamilia);
        //exibe o cad recem selecionado na tela
        if (result.cad) {
            $('#spanCad').text(" "+result.cad+" ");
            $('#spanSemCad').hide();
        }
        //submete uma chamada ajax para carregar os membros da familia selecionada, em formato json
        $.ajax({
            url: urlMembrosFamilia+"/"+result.idFamilia, //adiciona o id da familia aa url
            success: function(data) {
                membrosFamiliaJson = data;
                //atualiza as listas de selecao (que funcionam como autocomplete) dos campos de nome do solicitante e nome no registro
                autoComplete();
            },
            failure: function() {
                alert("Erro buscando membros da familia selecionada!")
            }
        });
        janelaModalProcurarCidadao.confirmada();
    }
    return false;
}

/**
 * chama a popup de selecao da familia
 */
function popupProcurarCidadao() {
    janelaModalProcurarCidadao.abreJanela({ titulo: "Pesquisa no Banco de Dados", largura: 900, url: urlProcurarCidadao })
}

/**
 * acionado do botao para cancelar a familia selecionada
 */
function limparFamilia() {
    $('#hiddenIdFamilia').val("");
    $('#spanCad').text("");
    membrosFamiliaJson = null;
    autoComplete();
}

function autoComplete() {
    var $inputsMembros = $('#txtNomeSolicitante, #txtNomeRegistro');

    //habilita funcao autocomplete nos campos correspondentes da tela
    $inputsMembros.autocomplete({
        source: getMembrosFamiliares(),
        select: selecionaMembro,
        minLength: 0,
        scroll: true
    }).focus(liga).click(liga); //o autocomplete sera acionado quando o campo receber foco ou for clicado com o mouse

    //tira a funcionalidade de autocomplete padrao do browser
    //Chrome 63 is ignoring autocomplete="off"  https://github.com/jackocnr/intl-tel-input/issues/668
    var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
    if (isChrome)
        $inputsMembros.attr('autocomplete', 'nope')
    else
        $inputsMembros.attr('autocomplete', 'off');

    /**
     * aciona o autocomplete manualmente
     */
    function liga() {
        $(this).autocomplete("search", "");
    }

    /**
     * monta uma lista de pares nome,id retornada para a propriedade source do componente autocomplete
     */
    function getMembrosFamiliares() {
        console.log(membrosFamiliaJson);
        var resultado = [];
        if (membrosFamiliaJson)
            $.each(membrosFamiliaJson, function(key, value) {
                resultado.push({"label":value.nome, "value":value.id});
            });
        return resultado;
    }

    /**
     * evento acionado ao selecionar uma opcao do componente
     * retorna sempre falso para evitar o comportamento padrao do componente
     */
    function selecionaMembro(event, ui) {
        var $txt = $(event.target);
        //identifica o objeto json do cidadao aa partir do id armazenado em .value
        var cidadao = findMembro(ui.item.value);
        if (cidadao) {
            //comportamentos distintos dependendo de qual campo da tela esta sendo alterado
            if ($txt.attr("id") == "txtNomeRegistro") { //nome na certidao
                if (! preencheCertidao(cidadao))
                    return false;
            } else if ($txt.attr("id") == "txtNomeSolicitante") { //nome do solicitante
                if (! preencheSolicitante(cidadao))
                    return false;
            }
            //armazena manualmente o nome selecionado no campo texto que acionou o componente
            $txt.val(cidadao.nome);
        }
        //retira o foco do input para que a caixa de selecao desapareca
        $txt.blur();
        return false;
    }

    /**
     * preenchimento automatico das informacoes da CERTIDAO
     * pede uma confirmacao antes de sobrescrever eventuais valores ja fornecidos
     * retorna falso para cancelar a operacao ou verdadeiro para continuar
     */
    function preencheCertidao(cidadao) {
        var confirmar = ($('#txtDataRegistro').val() != "")
        if (confirmar && ! confirm("Substituir informações atuais do formulário com as do cadastro de "+cidadao.nome+"?"))
            return false;
        $('#txtDataRegistro').val(cidadao.nascimento);
        return true;
    }

    /**
     * preenchimento automatico das informacoes do SOLICITANTE
     * pede uma confirmacao antes de sobrescrever eventuais valores ja fornecidos
     * retorna falso para cancelar a operacao ou verdadeiro para continuar
     */
    function preencheSolicitante(cidadao) {
        var confirmar = ( $('#txtMaeSolicitante').val() != ""
            || $('#txtPaiSolicitante').val() != ""
            || $('#txtIdentidadeSolicitante').val() != ""
            || $('#txtCPFSolicitante').val() != ""
            || $('#txtNacionalidadeSolicitante').val() != ""
            || $('#txtProfissaoSolicitante').val() != ""
            || $('#txtEstadoCivilSolicitante').val() != ""
            || $('#txtEnderecoSolicitante').val() != ""
            || $('#txtMunicipioSolicitante').val() != ""
            || $('#txtUfSolicitante').val() != ""
        )
        if (confirmar && ! confirm("Substituir informações atuais do formulário com as do cadastro de "+cidadao.nome+"?"))
            return false;
        $('#txtMaeSolicitante').val(cidadao.nomeMae);
        $('#txtPaiSolicitante').val(cidadao.nomePai);
        $('#txtIdentidadeSolicitante').val(cidadao.identidade);
        $('#txtCPFSolicitante').val(cidadao.cpf);
        $('#txtNacionalidadeSolicitante').val(cidadao.nacionalidade);
        $('#txtProfissaoSolicitante').val(cidadao.profissao);
        $('#txtEstadoCivilSolicitante').val(cidadao.estadoCivil);
        $('#txtEnderecoSolicitante').val(cidadao.endereco);
        $('#txtMunicipioSolicitante').val(cidadao.municipio);
        $('#txtUfSolicitante').val(cidadao.uf);
        return true;
    }

    /**
     * busca o cidadao na lista json de membros, pelo id
     */
    function findMembro(id) {
        var result = null
        $.each(membrosFamiliaJson, function(key, value) {
            if (value.id == id)
                result = value;
        });
        return result
    }

}