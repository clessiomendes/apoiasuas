var timerKeyUp = 0; //variavel global que detem a identificacao de determinado timer, para que ele seja cancelado posteriormente
const TEMPO_DELAY_KEYUP = 500; //0.5 segundos
const TAMANHO_MINIMO_PESQUISA = 3; //aguardar digitar 3 letras antes de pesquisar uma palavra

/**
 * Método para simular a "aplicação", ao objeto divPrincipal passado como parâmetro, dos métodos herdados da interface
 * InterfaceDivNovoMarcador (na verdade, uma função javascript contendo as definições e implementações desta interface)
 */
function newInterfaceDivNovoMarcador(divPrincipal) {
    var interfaceDivMarcador = new InterfaceDivNovoMarcador();
    for(var k in interfaceDivMarcador) divPrincipal[k]=interfaceDivMarcador[k];
    return divPrincipal;
}

/**
 * ver newInterfaceDivNovoMarcador
 */
function InterfaceDivNovoMarcador() {

    /**
     * Customizacao da janela a ser aberta
     */
    this.initDialog = function() {
        $(this).dialog({
                autoOpen: false,
                resizable: false,
                width: $(window).width() > 700 ? 700 : 'auto',
                //width: 700,
                modal: true
            }
        );
    }

    /**
     * Abre ou fecha a janela modal de novo marcador
     * @param exibir
     */
    this.janelaNovoMarcador = function(exibir) {
        var $divPrincipal = $(this);
        $divPrincipal.find('#descricaoAcao').val("");
        var fieldsetMarcadoresFiltradosDialog = $divPrincipal.find('#fieldsetMarcadoresFiltradosDialog');
        fieldsetMarcadoresFiltradosDialog.hide();
        fieldsetMarcadoresFiltradosDialog.find('.marcadores-similares').remove();
        if (exibir)
            $divPrincipal.dialog('open')
        else
            $divPrincipal.dialog('close')
    };

    /**
     * busca incremental de marcadores (programas, acoes, etc)
     */
    this.marcadoresSimilares = function(textInput, spans) {
        var $divPrincipal = $(this);
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
    this.marcadoresSimilaresComDelay = function(textInput) {
        divPrincipal = this; //precisamos guardar a referencia à própria instancia para ser usada dentreo de setTimeout
        var spans = $(this).find("#marcadoresDisponiveisDialog").children();
        clearTimeout(timerKeyUp);
        timerKeyUp = setTimeout( function() {
            divPrincipal.marcadoresSimilares(textInput, spans);
        }, TEMPO_DELAY_KEYUP);
    }

    /**
     * Ao clicar "confirmar", criar uma nova entrada no fieldset associado (cotendo um texto e um hidden)
     */
    this.confirmarNovoMarcadorDialog = function(hiddenNovosMarcadores, fieldsetMarcadoresTelaOriginal, classeMaracadores) {
        //var inputDescricao = divPrincipal.querySelector("#inputDescricaoMarcador");
        //var descricao = $(inputDescricao).val();
        var descricao = $(this).find("#inputDescricaoMarcador").val();

        if (! descricao || ! descricao.trim()) {
            alert("Preencha uma descrição antes de confirmar.")
        } else {
            //FIXME: diferenciar o tipo de marcador
            var novoSpan = $("<span class='"+classeMaracadores+"'/>");
            novoSpan.text(" "+descricao);
            var novoCheck = $("<input type='checkbox' class='check-marcadores' disabled='disabled' checked='checked' />");
            var novoHidden = $("<input type='hidden' name='"+hiddenNovosMarcadores+"' value='"+descricao+"' />");
            //var novoHidden = $("<input type='hidden' name='novaAcao' value='qualquer' />");
            novoHidden.appendTo(novoSpan);
            novoCheck.prependTo(novoSpan);
            novoSpan.prependTo(fieldsetMarcadoresTelaOriginal);
            this.janelaNovoMarcador(false);
        }
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
                if (words[x].length >= TAMANHO_MINIMO_PESQUISA) {
                    if (! contem(this, words[x]))
                        $(this).hide();
                }
            }
        });
    }, TEMPO_DELAY_KEYUP);
}

function inicializaEventos(fieldsetMarcadores, divMarcadores) {
    //onkeyup - pesquisa incremental à partir do input de busca
    $(fieldsetMarcadores).find(".input-search").keyup( function() {
        filtraMarcadores(this, $(fieldsetMarcadores).find('span'))
    } );

    //onclick - abrir a janela modal correspondente à partir do botão "Novo Marcador"
    if (divMarcadores)
        $(fieldsetMarcadores).find(".btn-adicionar-marcador").click( function() {
            divMarcadores.janelaNovoMarcador(true);
        } );

    //onclick - expandir o fieldset de marcadores
    $(fieldsetMarcadores).find(".btn-expandir-marcador").click( function() {
        $(this).hide();
        $(fieldsetMarcadores).find("div.rolagem").css("max-height","500px");
    } );
}

function contem(elementoDOM, palavraString) {
    palavraString = removeAcentos(palavraString).toLowerCase();
    textoSpan = removeAcentos($(elementoDOM).text()).toLowerCase()

    if (palavraString.length >= TAMANHO_MINIMO_PESQUISA && textoSpan.includes(palavraString))
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
