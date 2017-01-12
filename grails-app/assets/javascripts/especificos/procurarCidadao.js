/**
 * Função javascript usada para submeter pesquisas de cidadaos por numero, logradouro ou nome aa partir de um link
 * passando os parametros pela url em uma requisicao GET para o servidor
 */
function linkProcurarCidadao(link, url, inputNomeOuCodigoLegado, inputNumero, inputLogradouro) {
    var params = "";
    if (inputNomeOuCodigoLegado != null && inputNomeOuCodigoLegado.value != "")
        params += "&nomeOuCodigoLegado="+ encodeURIComponent(inputNomeOuCodigoLegado.value);
    if (inputNumero != null && inputNumero.value != "")
        params += "&numero="+encodeURIComponent(inputNumero.value);
    if (inputLogradouro != null && inputLogradouro.value != "")
        params += "&logradouro="+encodeURIComponent(inputLogradouro.value);
    if (params.substring(0,1) == "&")
        params = "?"+params.substring(1)
    link.href = url + params
};

/**
 * Simula um submit ao teclar enter dentro de uma caixa de texto
 */
function requisicaoProcurarCidadao(event, button) {
    if (event.keyCode == 13)
        button.click();
};
