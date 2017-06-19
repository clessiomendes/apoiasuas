/**
 * Classe que encapsula a criação de uma janela modal usando o componente JQuery UI Dialog e carregando o conteúdo
 *      dinamicamente com ajax.
 *
 * O método abreJanela() recebe o título e uma url a ser submetida ao servidor via ajax e o resultado exibido na janela.
 *
 * O método loadHTML(), atualiza a janela diretamente com o conteudo desejado passado como parametro,
 *      sem a necessidade de submeter uma url ao servidor
 *
 * requisitos: a página onde essa classe sera usada deve incluir a biblioteca JQuery UI
 *
 * Usamos um pattern de definição de uma "classe" em javascprit emulada por uma função. Ver:
 * https://mikewest.org/2005/03/component-encapsulation-using-object-oriented-javascript
 * e http://www.crockford.com/javascript/private.html
 *
 * @param refreshFunction função OPCIONAL a ser chamada quando o objeto sendo exibido na janela modal for alterado
 *                        e essa alteração precisar ser refletida na tela original
 */
function JanelaModalAjax(refreshFunction, largura) {
    //var self = this;
    largura = typeof largura !== 'undefined' ? largura : 700;

    //Div (criado dinamicamente) que conterá a janela modal esperada pelo componente JQuery UI Modal
    var $divModal = $('<div/>');

    /**
     * Abre uma janela modal com o título e a url (ou o conteudo HTML) passados
     * @param titulo exibido na barra de topo da janela
     * @param url (usada apenas se conteudoHTML for nulo) montada com a action e a view necessarias para recuperar o html contendo um div a ser exibido no conteudo da janela
     * @param conteudoHTML o próprio conteúdo HTML a ser exibido, sem a necessidade de submeter nenhuma nova requisição ao servidor. Se presente, o parametro url é desprezado
     */
    this.abreJanela = function(titulo, url, conteudoHTML) {
        //var $janelaMonitoramento = $("#janelaMonitoramento");
        //exibe uma janela modal inicialmente vazia enquanto aguarda a resposta do servidor
        $divModal.html(conteudoHTML ? conteudoHTML : '<asset:image src="loading.gif"/> carregando...').dialog({
            position:  {my: "center", at: "center", of: window},
            resizable: false,
            modal: true,
            title: titulo,
            width: $(window).width() > largura ? largura : 'auto'
        }).dialog('open');

        //Executa a chamada ajax para preencher a janela com o resultado retornado do servidor
        if (! conteudoHTML)
            $.ajax({
                url: url,
                error: function( jqXHR, textStatus, errorThrown ) {
                    $divModal.html(jqXHR.responseText).dialog({position: ['center']}).dialog('open');
                },
                success: function(data) {
                    $divModal.html(data).dialog({position: ['center']}).dialog('open');
                }
            });
    }

    this.fechaJanela = function () {
        $divModal.dialog('close');
    }

    /**
     * Chamar quando se quiser fechar a janela considerando a ação como cancelada
     */
    this.cancelada = function () {
        this.fechaJanela();
    }

    /**
     * Chamar quando se quiser fechar a janela considerando a ação como confirmada
     */
    this.confirmada = function () {
        $divModal.dialog('close');
        if (typeof refreshFunction != 'undefined' && refreshFunction != null)
            refreshFunction();
    }

    /**
     * atualiza a janela diretamente com o conteudo desejado passado como parametro,
     * sem a necessidade de submeter uma url ao servidor
     * @param html HTML contendo o conteúdo a ser carregado na janela
     */
    this.loadHTML = function (html) {
        $divModal.html(html);
    }
}
