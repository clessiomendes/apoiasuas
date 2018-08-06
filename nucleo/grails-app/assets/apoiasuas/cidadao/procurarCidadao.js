/**
 * Funcao javascript usada para submeter pesquisas de cidadaos por numero, logradouro ou nome aa partir de um link
 * passando os parametros pela url em uma requisicao GET para o servidor
 */
function linkProcurarCidadao(link, url) {
    link.href = url + montaParametros();
};

function montaParametros(elementoPai) {
    if (! elementoPai)
        elementoPai = $(document);
    var $divPai = $(elementoPai)
    var $inputNomeOuCad = $divPai.find('#inputNomeOuCad')
    var $inputNumero = $divPai.find('#inputNumero')
    var $inputLogradouro = $divPai.find('#inputLogradouro')
    var $inputOutroMembro = $divPai.find('#inputOutroMembro')
    var $inputIdade = $divPai.find('#inputIdade')
    var $inputNis = $divPai.find('#inputNis')
    var $inputPrograma = $divPai.find('#inputPrograma')

    var params = "";
    if ($inputNomeOuCad.length && $inputNomeOuCad.val() != "")
        params += "&nomeOuCad="+ encodeURIComponent($inputNomeOuCad.val());
    if ($inputNumero.length && $inputNumero.val() != "")
        params += "&numero="+encodeURIComponent($inputNumero.val());
    if ($inputLogradouro.length && $inputLogradouro.val() != "")
        params += "&logradouro="+encodeURIComponent($inputLogradouro.val());
    if ($inputOutroMembro.length && $inputOutroMembro.val() != "")
        params += "&outroMembro="+encodeURIComponent($inputOutroMembro.val());
    if ($inputIdade.length && $inputIdade.val() != "")
        params += "&idade="+encodeURIComponent($inputIdade.val());
    if ($inputNis.length && $inputNis.val() != "")
        params += "&nis="+encodeURIComponent($inputNis.val());
    if ($inputPrograma && $inputPrograma.length && $inputPrograma.val() != "")
        params += "&programa="+encodeURIComponent($inputPrograma.val());
    if (params.substring(0,1) == "&")
        params = "?"+params.substring(1)
    return params
}

function linkProcurarCidadaoPopup(localJanela, url) {
    localJanela.abreJanela({titulo: 'Procurar cadastro familiar', url: url + montaParametros(localJanela.getDivModal()), largura: 900});
    return false;
};

/**
 * Simula um submit ao teclar enter dentro de uma caixa de texto
 */
function requisicaoProcurarCidadao(event, button) {
    if (event.keyCode == 13)
        button.click();
};
