var timerKeyUp = 0; //variavel global que detem a identificacao de determinado timer, para que ele seja cancelado posteriormente
var CONST_TEMPO_DELAY_KEYUP = 500; //0.5 segundos
var CONST_TAMANHO_MINIMO_PESQUISA = 3; //aguardar digitar 3 letras antes de pesquisar uma palavra

/**
 * Método para simular a "aplicação", ao objeto divPrincipal passado como parâmetro, dos métodos herdados da interface
 * InterfaceDivEditMarcador (na verdade, uma função javascript contendo as definições e implementações desta interface)
 */
function newInterfaceDivEditMarcador(divPrincipal) {
    var interfaceDivMarcador = new InterfaceDivEditMarcador();
    for(var k in interfaceDivMarcador) divPrincipal[k]=interfaceDivMarcador[k];
    return divPrincipal;
}

/**
 * ver newInterfaceDivEditMarcador()
 */
function InterfaceDivEditMarcador() {

    //Variáveis locais, no contexto de cada janela, guardando as referencias para os elementos do form de origem,
    //a fim de que os valores preenchidos possam ser retornados para a tela chamadora.
    var $hiddenObservacaoMarcador;
    var $hiddenTecnicoMarcador;
    var $checkHabilitadoMarcador;
    var $linkDescricaoMarcador;

    /**
     * Customizacao da janela a ser aberta
     */
    this.initDialog = function () {
        $(this).dialog({
                autoOpen: false,
                resizable: false,
                width: $(window).width() > 700 ? 700 : 'auto',
                modal: true
            }
        );
    }

    /**
     * Abre a janela modal de edição do marcador e preenche os campos com os valores atuais do marcador
     */
    this.janelaEditMarcador = function (spanMarcador) {
        var $divPrincipal = $(this);
        var $spanMarcador = $(spanMarcador);

        //Guarda ponteiro para os elementos (e inputs hidden) no formulario chamador
        $hiddenObservacaoMarcador = $spanMarcador.find('.observacao-marcadores');
        $hiddenTecnicoMarcador = $spanMarcador.find('.tecnico-marcadores');
        $checkHabilitadoMarcador = $spanMarcador.find('.check-marcadores');
        $linkDescricaoMarcador = $spanMarcador.find('.descricao-marcadores');
        //Preenche com os valores atuais

        $divPrincipal.find('#descricaoMarcador').text($linkDescricaoMarcador.text());
        $divPrincipal.find('#inputObservacaoMarcador').val($hiddenObservacaoMarcador.val());
        $divPrincipal.find('#inputTecnicoMarcador').val($hiddenTecnicoMarcador.val());
        $divPrincipal.dialog('open');
    };

/*
    this.janelaEditMarcador = function (linkDescricaoMarcador, hiddenObservacaoMarcador, hiddenTecnicoMarcador, checkHabilitadoMarcador) {
        var $divPrincipal = $(this);

        //Guarda ponteiro para os campos hidden no formulario chamador
        $hiddenObservacaoMarcador = $(hiddenObservacaoMarcador);
        $hiddenTecnicoMarcador = $(hiddenTecnicoMarcador);
        $checkHabilitadoMarcador = $(checkHabilitadoMarcador);
        //Preenche com os valores atuais

        $divPrincipal.find('#descricaoMarcador').text($(linkDescricaoMarcador).text());
        $divPrincipal.find('#inputObservacaoMarcador').val($hiddenObservacaoMarcador.val());
        $divPrincipal.find('#inputTecnicoMarcador').val($hiddenTecnicoMarcador.val());
        $divPrincipal.dialog('open');
    };
*/

    /**
     * Ao clicar "confirmar", preenche os campos hiddens no formulário que chamou a janela
     */
    this.confirmarEditMarcadorDialog = function() {
        var $divPrincipal = $(this);
        var $inputTecnicoMarcador = $divPrincipal.find('#inputTecnicoMarcador');
        if (! $inputTecnicoMarcador.val()) {
            alert("Favor selecionar o técnico logado");
            exit;
        }
        $hiddenObservacaoMarcador.val($divPrincipal.find('#inputObservacaoMarcador').val());
        $hiddenTecnicoMarcador.val($inputTecnicoMarcador.val());
        $checkHabilitadoMarcador.prop('checked', true);
        $divPrincipal.dialog('close');
    };

    this.cancelarEditMarcadorDialog = function() {
        var $divPrincipal = $(this);
        $divPrincipal.dialog('close');
    };

}


    /**
 * busca incremental de marcadores. Mostra marcador cujo texto contenha QUALQUER UMA DAS PALAVRAS PESQUISADAS (operador ou)
 */
function filtraMarcadores(textInput, spans) {
    clearTimeout(timerKeyUp);
    timerKeyUp = setTimeout( function() {
        var words = $(textInput).val().match(/[^\s]+|\s+[^\s+]$/g);
        $(spans).show();
        spans.each(function(){
            for(x=0; (words != null) && (x < words.length); x++) {
                //ignora as primeiras 2 letras digitadas
                if (words[x].length >= CONST_TAMANHO_MINIMO_PESQUISA) {
                    if (! contem(this, words[x]))
                        $(this).hide();
                }
            }
        });
    }, CONST_TEMPO_DELAY_KEYUP);
}

function inicializaEventosTabMarcadores(fieldsetMarcadores/*, divMarcadores*/) {
    var $fieldsetMarcadores = $(fieldsetMarcadores)
    //onkeyup - pesquisa incremental à partir do input de busca
    $fieldsetMarcadores.find(".input-search").keyup( function() {
        filtraMarcadores(this, $fieldsetMarcadores.find('span'))
    } );

    //onclick - expandir o fieldset de marcadores
    $fieldsetMarcadores.find(".speed-button-expandir").click( function() {
        $(this).hide();
        $fieldsetMarcadores.find("div.rolagem").css("max-height","500px");
    } );
}

function contem(elementoDOM, palavraString) {
    palavraString = removeAcentos(palavraString).toLowerCase();
    textoSpan = removeAcentos($(elementoDOM).text()).toLowerCase()

    if (palavraString.length >= CONST_TAMANHO_MINIMO_PESQUISA && textoSpan.includes(palavraString))
        return true
    else
        return false
};

function removeStopWords(frase) {
    var stop_words = new Array(
        "a",
        "à",
        "agora",
        "ainda",
        "alguém",
        "algum",
        "alguma",
        "algumas",
        "alguns",
        "ampla",
        "amplas",
        "amplo",
        "amplos",
        "ante",
        "antes",
        "ao",
        "aos",
        "após",
        "aquela",
        "aquelas",
        "aquele",
        "aqueles",
        "aquilo",
        "as",
        "até",
        "através",
        "cada",
        "coisa",
        "coisas",
        "com",
        "como",
        "contra",
        "contudo",
        "da",
        "daquele",
        "daqueles",
        "das",
        "de",
        "dela",
        "delas",
        "dele",
        "deles",
        "depois",
        "dessa",
        "dessas",
        "desse",
        "desses",
        "desta",
        "destas",
        "deste",
        "deste",
        "destes",
        "deve",
        "devem",
        "devendo",
        "dever",
        "deverá",
        "deverão",
        "deveria",
        "deveriam",
        "devia",
        "deviam",
        "disse",
        "disso",
        "disto",
        "dito",
        "diz",
        "dizem",
        "do",
        "dos",
        "e",
        "é",
        "e'",
        "ela",
        "elas",
        "ele",
        "eles",
        "em",
        "enquanto",
        "entre",
        "era",
        "essa",
        "essas",
        "esse",
        "esses",
        "esta",
        "está",
        "estamos",
        "estão",
        "estas",
        "estava",
        "estavam",
        "estávamos",
        "este",
        "estes",
        "estou",
        "eu",
        "fazendo",
        "fazer",
        "feita",
        "feitas",
        "feito",
        "feitos",
        "foi",
        "for",
        "foram",
        "fosse",
        "fossem",
        "grande",
        "grandes",
        "há",
        "isso",
        "isto",
        "já",
        "la",
        "la",
        "lá",
        "lhe",
        "lhes",
        "lo",
        "mas",
        "me",
        "mesma",
        "mesmas",
        "mesmo",
        "mesmos",
        "meu",
        "meus",
        "minha",
        "minhas",
        "muita",
        "muitas",
        "muito",
        "muitos",
        "na",
        "não",
        "nas",
        "nem",
        "nenhum",
        "nessa",
        "nessas",
        "nesta",
        "nestas",
        "ninguém",
        "no",
        "nos",
        "nós",
        "nossa",
        "nossas",
        "nosso",
        "nossos",
        "num",
        "numa",
        "nunca",
        "o",
        "os",
        "ou",
        "outra",
        "outras",
        "outro",
        "outros",
        "para",
        "pela",
        "pelas",
        "pelo",
        "pelos",
        "pequena",
        "pequenas",
        "pequeno",
        "pequenos",
        "per",
        "perante",
        "pode",
        "pôde",
        "podendo",
        "poder",
        "poderia",
        "poderiam",
        "podia",
        "podiam",
        "pois",
        "por",
        "porém",
        "porque",
        "posso",
        "pouca",
        "poucas",
        "pouco",
        "poucos",
        "primeiro",
        "primeiros",
        "própria",
        "próprias",
        "próprio",
        "próprios",
        "quais",
        "qual",
        "quando",
        "quanto",
        "quantos",
        "que",
        "quem",
        "são",
        "se",
        "seja",
        "sejam",
        "sem",
        "sempre",
        "sendo",
        "será",
        "serão",
        "seu",
        "seus",
        "si",
        "sido",
        "só",
        "sob",
        "sobre",
        "sua",
        "suas",
        "talvez",
        "também",
        "tampouco",
        "te",
        "tem",
        "tendo",
        "tenha",
        "ter",
        "teu",
        "teus",
        "ti",
        "tido",
        "tinha",
        "tinham",
        "toda",
        "todas",
        "todavia",
        "todo",
        "todos",
        "tu",
        "tua",
        "tuas",
        "tudo",
        "última",
        "últimas",
        "último",
        "últimos",
        "um",
        "uma",
        "umas",
        "uns",
        "vendo",
        "ver",
        "vez",
        "vindo",
        "vir",
        "vos",
        "vós"
    );
    // Review all the words
    for (x=0; x < stop_words.length; x++) {
        var regexp = new RegExp("\\b"+stop_words[x]+"\\b", "g");
        frase = frase.replace(regexp, "");
    }
    return frase;
}

/**
 * busca incremental de marcadores (programas, acoes, etc)
 */
marcadoresSimilares = function(textInput, spans) {
    var $divPrincipal = $('#create-marcador');
    var fraseLimpa = removeStopWords(textInput.value.toLowerCase());
    // Split out all the individual words in the phrase
    var words = fraseLimpa.match(/[^\s]+|\s+[^\s+]$/g);
    var spanCounter = 0;
    //Vetor que contera apenas os marcadores pontuados
    var rank = [];

    //Percorre todos marcadores previstos na tela de origem
    if (words != null)
        spans.each(function(){
            var spanClonado = $(this).clone(true);
            var score = 0;
            for(x=0; x < words.length; x++) {
                //Aumenta a "pontuacao" deste marcador para cada palavra compativel encontrada
                //procurar APENAS palavras com 3 ou mais caracteres que estejam presentes na descricao do marcador
                if (contem(spanClonado, words[x])) {
                    //if (words[x].length > 2 && spanClonado.text().toLowerCase().includes(words[x])) {
                    spanClonado.html(spanClonado.html().replace(new RegExp("("+words[x]+")", "gi"), "<b style='color:rgba(255, 0, 0, 0.41)'>$1</b>"));
                    score++;
                }
            }
            //guarda o marcador para futuramente exibilo na tela
            if (score > 0)
                rank.push({ score: score, span: spanClonado })
        });

    //ordena os marcadores de acordo com sua pontuacao (numero de palavras similares encontradas)
    rank.sort(function (a, b) {
        if (a.score > b.score) {
            return 1;
        }
        if (a.score < b.score) {
            return -1;
        }
        return 0;
    });

    //finalmente, adiciona apenas os marcadores pontuados no fieldset de exibicao de resultados similares
    var fieldsetMarcadoresFiltradosDialog = $divPrincipal.find('#fieldsetMarcadoresFiltradosDialog');
    fieldsetMarcadoresFiltradosDialog.hide();
    fieldsetMarcadoresFiltradosDialog.find('.marcadores-similares').remove();
    for(x = rank.length-1; x >= 0; x--) {
        fieldsetMarcadoresFiltradosDialog.append(rank[x].span);
        fieldsetMarcadoresFiltradosDialog.show();
    }
};

/**
 * Permite esperar por um delay de 1s antes de acionar a função javascript. Especialmente util
 * para o evento onkeyup de caixas de texto não serem acionadas muitas vezes durante a digitacao.
 * Obtido de http://stackoverflow.com/a/1909508/1916198
 */
marcadoresSimilaresComDelay = function(textInput) {
    var spans = $("#marcadoresDisponiveisDialog").children();
    clearTimeout(timerKeyUp);
    timerKeyUp = setTimeout( function() {
        marcadoresSimilares(textInput, spans);
    }, CONST_TEMPO_DELAY_KEYUP);
};

function abreJanelaNovoMarcador(titulo, url, functionAtualizaMarcadores) {
    var $janelaNovoMarcador = $("#janelaNovoMarcador");
    $janelaNovoMarcador[0].functionAtualizaMarcadores = functionAtualizaMarcadores;
    //exibe uma janela modal inicialmente vazia enquanto aguarda a resposta do servidor
    $janelaNovoMarcador.html('<asset:image src="loading.gif"/> carregando...').dialog({
        position:  {my: "center", at: "center", of: window},
        resizable: false,
        modal: true,
        title: titulo,
        width: $(window).width() > 700 ? 700 : 'auto',
    }).dialog('open');

    //Executa a chamada ajax para preencher a janela com o resultado retornado do servidor
    $.ajax({
        url: url,
        error: function( jqXHR, textStatus, errorThrown ) {
            $janelaNovoMarcador.html(jqXHR.responseText).dialog({position: ['center']}).dialog('open');
        },
        success: function(data) {
            $janelaNovoMarcador.html(data).dialog({position: ['center']}).dialog('open');
        }
    });
}

function fechaJanelaNovoMarcadorEAtualiza() {
    $("#janelaNovoMarcador")[0].functionAtualizaMarcadores();
    fechaJanelaNovoMarcador()
}

function fechaJanelaNovoMarcador() {
    $("#janelaNovoMarcador").dialog('close');
}
