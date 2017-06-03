/**
 * Funcao javascript usada para submeter pesquisas de cidadaos por numero, logradouro ou nome aa partir de um link
 * passando os parametros pela url em uma requisicao GET para o servidor
 */
function linkProcurarCidadao(link, url) {

    var params = "";
    var inputNomeOuCad = document.getElementById('inputNomeOuCad')
    var inputNumero = document.getElementById('inputNumero')
    var inputLogradouro = document.getElementById('inputLogradouro')
    var inputOutroMembro = document.getElementById('inputOutroMembro')
    var inputIdade = document.getElementById('inputIdade')
    var inputNis = document.getElementById('inputNis')
    var inputPrograma = document.getElementById('inputPrograma')
    if (inputNomeOuCad != null && inputNomeOuCad.value != "")
        params += "&nomeOuCad="+ encodeURIComponent(inputNomeOuCad.value);
    if (inputNumero != null && inputNumero.value != "")
        params += "&numero="+encodeURIComponent(inputNumero.value);
    if (inputLogradouro != null && inputLogradouro.value != "")
        params += "&logradouro="+encodeURIComponent(inputLogradouro.value);
    if (inputOutroMembro != null && inputOutroMembro.value != "")
        params += "&outroMembro="+encodeURIComponent(inputOutroMembro.value);
    if (inputIdade != null && inputIdade.value != "")
        params += "&idade="+encodeURIComponent(inputIdade.value);
    if (inputNis != null && inputNis.value != "")
        params += "&nis="+encodeURIComponent(inputNis.value);
    if (inputPrograma && inputPrograma != null && inputPrograma.value != "")
        params += "&programa="+encodeURIComponent(inputPrograma.value);
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
