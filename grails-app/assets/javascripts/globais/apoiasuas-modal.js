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
function JanelaModalAjax() {
    //guarda globalmente a funcao de refresh para uso posterior (confirmacao da caixa de dialogo)
    var refreshFunction

    //Div (criado dinamicamente) que conterá a janela modal esperada pelo componente JQuery UI Modal
    var $divModal = $('<div/>');

    this.getDivModal = function() {
        return $divModal
    }

    /**
     * Abre uma janela modal com o título e a url (ou o conteudo HTML) passados
     * Os parametros são passados como {titulo: ..., url: ..., conteudoHTML: ..., element: ..., refreshFunction: ..., largura: ...}
     * @param titulo exibido na barra de topo da janela
     * @param url (usada apenas se conteudoHTML for nulo) montada com a action e a view necessarias para recuperar o html contendo um div a ser exibido no conteudo da janela
     * @param conteudoHTML o próprio conteúdo HTML a ser exibido, sem a necessidade de submeter nenhuma nova requisição ao servidor. Se presente, o parametro url é desprezado
     * @param element um <div> html representando o conteúdo inteiro da janela
     * @param refreshFunction o ponteiro para uma função callback que será acionada caso seja clicado um botao de confirmacao
     * @param largura em pixels, para a tela criada(default 700)
     * @param esconderBotaoFechar esconder botao fechar (x) no canto superior direito
     */
    //this.abreJanela = function(titulo, url, conteudoHTML, element) {
    this.abreJanela = function(parametros) {
        //Primeiro testa se apenas (e pelo menos) um dos três parâmetros [url, conteudoHTML ou element] foi passado
        if ( (parametros.url?1:0)+(parametros.conteudoHTML?1:0)+(parametros.element?1:0) != 1) {
            throw "Erro! Apenas (e pelo menos) um parâmetro entre url, conteudoHTML e element deve ser passado para abrejanela() em apoiasuas-modal.js"
            //alert("Erro! Apenas (e pelo menos) um parâmetro entre url, conteudoHTML e element deve ser passado para abrejanela() em apoiasuas-modal.js")
            //return;
        }

        if (parametros.conteudoHTML) {
            $divModal.html(parametros.conteudoHTML)
        } else if (parametros.element) {
            $(parametros.element).appendTo($divModal);
            $(parametros.element).show();
        } else if (parametros.url) {
            $divModal.html('<asset:image src="loading.gif"/> carregando...');
        } else {
            throw "Erro! conteudoHTML, element ou url devem ser fornecidos para abrejanela() em apoiasuas-modal.js"
        }

        var dialogClass = ""
        if (parametros.esconderBotaoFechar)
            dialogClass += "no-close "
        if (! parametros.titulo)
            dialogClass += "no-titlebar "

        refreshFunction  = parametros.refreshFunction;

        //exibe uma janela modal inicialmente vazia enquanto aguarda a resposta do servidor
        var largura = ifNull(parametros.largura, 700);
        $divModal.dialog({
            position:  {my: "center", at: "center", of: window},
            resizable: false,
            dialogClass: dialogClass,
            modal: true,
            title: ifNull(parametros.titulo, ""),
            width: $(window).width() > largura ? largura : 'auto'
        }).dialog('open');

        //Executa a chamada ajax para preencher a janela com o resultado retornado do servidor
        if (parametros.url)
            $.ajax({
                url: parametros.url,
                error: function( jqXHR, textStatus, errorThrown ) {
                    $divModal.html(jqXHR.responseText).dialog({position: ['center']}).dialog('open');
                },
                success: function(data) {
                    $divModal.html(data).dialog({position: ['center']}).dialog('open');
                }
            });
    }

    this.setOnClose = function(callback) {
        $divModal.on( "dialogclose", callback );
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
        if ($divModal.dialog('isOpen'))
            $divModal.dialog('close');
        if (refreshFunction !== undefined && refreshFunction !== null)
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
