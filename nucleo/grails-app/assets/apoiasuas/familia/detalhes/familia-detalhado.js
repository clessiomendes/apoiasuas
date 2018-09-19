//= require moment.js
//= require sessao.js

/**
 * Variaveis Globais
 */
modoEdicao = false;
modoCriacao = false;
modoContinuarCriacao = false;
modoConsulta = false;
//Contém uma referência para a aba (form de domicilio ou membro) atualmente sendo exibido
var $divFormSelecionado = null;

/**
 * Carregamento inicial da página. obs: tambem ha um bloco de carregamento no corpo da gsp, por conta das tags gsp do grails \${}
 */
$(document).ready(inicializa);

/**
 * Chamado na inicialização da página E NA CRIAÇÃO DE NOVOS FORMULÁRIOS DE MEMBROS
 */
function inicializa() {
    vinculosCamposDetalhados();
    autocompletePaiMaeCuidador();
    CamposImportantes.init();
    $('.despesa').on('change', despesaChange);
    $('.txtRendaMensal').on('change', rendaChange);
    $('.checkSemRenda').on('change', rendaChange);
    $('#btnAdicionarTelefone').on('click', btnAdicionarTelefoneClick);
    despesaChange();
    rendaChange();
    //alertaCamposInteiros();
}

/**
 * Exibe um formulario aa partir do click nos tabs. Retorna falso (indicando que eventual submit deva ser ignorado)
 * @param $div - o form a ser exibido
 * @param linkClicado - o link que foi clicado (para ser estilizado após o click)
 * @returns {boolean}
 */
function showForm(div, linkClicado) {
    $divFormSelecionado = $(div);
    //destaca apenas o link selecionado dentre os demais disponiveis no cabecalho
    $('.links-forms').removeClass('link-selecionado');
    $(linkClicado).addClass('link-selecionado');

    //exibe apenas o formulario correspondente ao link selecionado
    if (!$divFormSelecionado.is(":visible")) {
        $divFormSelecionado.removeClass('hidden')
        $('.forms-detalhados').hide(0);
        $divFormSelecionado.show("slide", {direction: "up"}, 200, function () {
            realinhaToolTips($divFormSelecionado);
        });
    }

    return false;
}

/**
 * Introduz um delay de 1 segundo antes de começar a pesquisa
 */
var delayProcura;
function delayProcurarCampoDetalhe(txtProcura) {
    clearTimeout(delayProcura);
    if (txtProcura == '') //restaura a tela rapidamente quando limpar o campo de procura
        procurarCampoDetalhe(txtProcura)
    else
        delayProcura = setTimeout(function () { //enquanto o operador estiver digitando (aguarda meio segundo) não aplica o filtro nos campos
            procurarCampoDetalhe(txtProcura);
        }, 500);
};

function procurarCampoDetalhe(txtProcura) {

    var textoBuscado = removeAcentos($(txtProcura).val().trim());

    //ignora os 2 primeiros caracteres digitados
    if (textoBuscado.length > 0 && textoBuscado.length < 3)
        return;

    //Volta ao estado original da tela: Mostra tudo
    $('.forms-detalhados .sessao-detalhes, .forms-detalhados .conteudo-sessao, ' +
        '.forms-detalhados fieldset, .forms-detalhados .fieldcontain').show(0);
    //como esta exibindo todas as sessoes, tem que mudar o icone em cada uma delas para "esconder"    
    $('.sessao-detalhes .imagem-fold').attr('src', imgVerMenos);
    $('.sessao-detalhes .right').css('background-image', imgVerMenosTodos);
    //faz um refresh completo dos vinculos
    vinculosCamposDetalhados();

    //encerra. não é uma busca
    if (textoBuscado.trim() == "")
        return;

    //varre cada div de formulario fazendo uma busca (recursiva) na sua arvore de campos e grupos de campos
    $('.forms-detalhados').each(function () {
        var $divForm = $(this);
        if ($divForm.attr('id') != 'divNovoMembro') //ignora o form auxiliar usado como modelo para novos membros
            proximoNo($divForm);
    });

    /**
     * funcao recursiva para buscar o texto digitado numa arvore onde campos sao as folhas e subgrupos (fieldset ou sessao) sao os nos
     * retorna verdadeiro se encontrou pelo menos uma ocorrencia na sub-arvore neste passo da recursao
     */
    function proximoNo($elementoPai) {

        //assume inicialmente que nenhuma ocorrencia foi encontrada
        var result = false;

        //procurar tanto nos campos descendentes, quanto em possiveis subgrupos de campos (fieldset ou sessao)
        $elementoPai.find('fieldset, .sessao-detalhes, .fieldcontain').each(function () {
            var $divElementoFilho = $(this);

            //teste de campos (último nível da recursão)
            if ($divElementoFilho.is('.fieldcontain')) {
                //tenta buscar o texto digitado em qualquer elemento contido no div do campo (label, palavra-chave, check-box, etc)
                if (match(textoBuscado, $divElementoFilho))
                    //informa para o nivel superior da recursao que PELO MENOS uma ocorrencia foi encontrada nesta sessao (desde que ela não seja em um campo que não será exibido)
                    result = result || ! $divElementoFilho.is('.sempre-escondido')
                else
                    $divElementoFilho.hide(0); //se nao encontrar a palavra chave, esconde o campo inteiro

                //teste de subgrupos de campos
            } else if ($divElementoFilho.is('fieldset, .sessao-detalhes')) {
                //tenta encontrar o texto na legenda do subgrupo (legend para fieldset, .cabecalho-sessao para sessao, palavra-chave para ambos)
                if (match(textoBuscado, $divElementoFilho.find('legend, .cabecalho-sessao, palavra-chave'))) {
                    result = true; //informa para o nivel superior da recursao que uma ocorrencia foi encontrada
                } else {
                    //se nao encontrar, continua tentando no nivel inferior da arvore do subgrupo
                    if (proximoNo($divElementoFilho.is('.sessao-detalhes') ? $divElementoFilho.find('.conteudo-sessao') : $divElementoFilho))
                        result = true; //informa para o nivel superior da recursao que uma ocorrencia foi encontrada
                    else
                        $divElementoFilho.hide(0); //se nao encontrou nos niveis inferiores, esconder o subgrupo inteiro
                }
            }
        });
        return result;
    }

    /**
     * Tenta bucar o texto no elemento ou em um subconjunto restrito de elementos selecionados via jQuery
     * @returns {boolean}
     */
    function match(textoBuscado, $elementos) {
        //o jquery representa elementos sempre como um array, mesmo que se trate de um unico elemento especifico
        for (var i = 0; i < $elementos.length; i++) {
            if (contem(textoBuscado, $($elementos[i]).text()))
                return true;
        }
        return false;
    }

    /**
     * Funcao para buscar um string em outra, ignorando acentos de maiusculas
     * @param parte - a palavra chave sendo buscada
     * @param conjunto - o texto que pode conter (ou nao) a palavra chave
     * @returns {*}
     */
    function contem(parte, conjunto) {
        if (!parte || !conjunto)
            return false;
        return removeAcentos(conjunto).toLowerCase().includes(parte.toLowerCase())
    }

}

function ultimoValor($jquerySelector) {
    var max = -1;
    $jquerySelector.each(function() {
        var ord = $(this).val()
        if (jQuery.isNumeric(ord))
            max = Math.max(max, ord);
    });
    return max+1;
}

/**
 * Caso o operador tenha acionado o botao de "Novo Membro", criamos um novo tab e formulario correspondente (clonado) na tela
 */
function novoMembro() {
    var $formPrincipal = $('#formPrincipal')
    //determinando o proximo ord
    //var proximoIndice = $formPrincipal.children('.forms-cidadao').length;
    var proximoIndice = ultimoValor($('.ord-cidadao'));
    console.log('proximo ord '+proximoIndice);

    //adiciona novo div aa partir do modelo
    var $divMembroClonado = $('#divNovoMembro').clone();
    //Muda o nome do div
    $divMembroClonado.attr('id', 'divMembros[' + proximoIndice + ']');
    //Muda o indice do membro correspondente ao novo cidadao em todos os elementos que serao submetidos (aqueles que contem membros[X] no nome
    $divMembroClonado.find('*').each(function () {
        var $this = $(this);
        //Verifica se eh um elemento do tipo membros[X]
        if ($this.attr('name') && $this.attr('name').indexOf('membros[') == 0) {
            //Substitui o nome de membros[new] para memtros[X], onde X é o índice para o próximo membro gerado
            var novo = 'membros[' + proximoIndice + ']' + cutFrom($this.attr('name'), '.')
            //atualiza nome e id do elemento
            $this.attr('name', novo);
            $this.attr('id', novo);
            //atualiza o conteudo do hidden ord recem copiado para conter o indice do membro
            if (cutFrom($this.attr('name'), '.') == '.ord')
                $this.val(proximoIndice);
        }
    })

    //adiciona o div novo ao conjunto de divs da tela (inicialmente escondido)
    $divMembroClonado.hide(0);
    $formPrincipal.append($divMembroClonado);

    //adiciona novo link nos tabs
    var $novoTab = $("<a href='#' class='links-forms cidadao-detalhado' title='novo cidadao'>novo membro</a>");
    //$novoTab.insertAt(-1, $('#navFamilia ul')[0]);
    //$('#navFamilia').children('ul').append($novoTab);
    //$novoTab.wrap('<li></li>');

    var $excluirNovoTab = $("<a href='#' id='linkExcluirMembro[" + proximoIndice + "]' class='cidadao-remover' title='excluir cidadao'>x</a>");
    //$('#navFamilia').children('ul').append($excluirNovoTab);
    //$excluirNovoTab.wrap('<li></li>');

    $li = $('<li>');
    $li.append($novoTab);
    $li.append($excluirNovoTab);
    $('#navFamilia').children('ul').append($li);

    //associa o tab ao form
    $novoTab.on("click", function () {
        showForm($divMembroClonado, $novoTab);
    });
    $excluirNovoTab.on("click", function () {
        $divMembroClonado.remove();
        $novoTab.remove();
        $excluirNovoTab.remove();
    });
    //Navega ate o div criado simulando um click no novo tab
    $novoTab.click();
    //Navega ate o primeiro input do novo div (o input do campo NomeCompleto)
    $divMembroClonado.find('.fieldcontain:eq(0) input:eq(0)').focus();

    //re-executa todos os comandos de inicialização da página
    inicializa();
}

function vinculosCamposDetalhados() {
    //refresh dos vinculos entre campos no formulario dos dados da Familia
    $('#divFamilia').each(function () {
        vinculosCamposFamilia(this);
    });
    //percorre os formularios ESPECIFICOS DE CIDADAO e faz o refresh dos vinculos entre campos
    $('.forms-cidadao').each(function () {
        vinculosCamposCidadao(this);
    });
}

/**
 * Esconde ou exibe alguns elementos visuais de acordo com o preenchimento de campos relacionados
 * @param componenteForm - formulario a ser incicializado ou um elemento qualquer nele contido
 */
function vinculosCamposFamilia(componenteForm) {
    //busca o div de familia correspondente ao elemento
    var $componenteForm = $(componenteForm)
    var $divFamilia = $componenteForm.divFormMae();

    //testa se a funcao foi chamada aa partir de um elemento especifico (todos = false) ou se para refazer todos os vinculos do formulario
    var todos = $componenteForm.is($divFamilia);
    var tempoAnimacao = todos ? 0 : 200;

    //se a funcao foi chamada para um elemento especifico, atualiza somente os vinculos desse elemento
    if (todos || $componenteForm.is($('#selectOcupacao')))
        mostraSe($('#selectOcupacao').val() == "SIM", $('#divNomeOcupacao'), tempoAnimacao);
    if (todos || $componenteForm.is($('#selectQuilombola')))
        mostraSe($('#selectQuilombola').val() == "SIM", $('#divComunidadeQuilombola'), tempoAnimacao);
    if (todos || $componenteForm.is($('#selectIndigena')))
        mostraSe($('#selectIndigena').val() == "SIM", $('#divPovoIndigena'), tempoAnimacao);
    if (todos || $componenteForm.is($('#selectBolsaFamilia'))) {
        $('.pbf-sim,.pbf-nao').hide(0); //pra evitar efeitos visuais estranhos, esconde os dois selects antes
        mostraSe($('#selectBolsaFamilia').val() == "true", $('.pbf-sim'), tempoAnimacao);
        mostraSe($('#selectBolsaFamilia').val() != "true", $('.pbf-nao'), tempoAnimacao);
    }


    sugestoesCampos($divFamilia);

    /**
     * Exibe $div aa partir do select cuja opcao "SIM" tenha sido selecionada pelo operador
     * @param select
     * @param $div
     */
    function mostraSe(condicao, $div, tempoAnimacao) {
        if (condicao) { //exibir
            $div.fadeIn(tempoAnimacao);
            $div.removeClass('sempre-escondido');
        } else { //esconder
            $div.limpaConteudo();
            $div.fadeOut(tempoAnimacao);
            $div.addClass('sempre-escondido');
        }
    }

};

/**
 * Esconde ou exibe alguns elementos visuais de acordo com o preenchimento de campos relacionados
 * @param componenteForm - formulario a ser incicializado ou um elemento qualquer nele contido
 * @param todos - se true, atualizar todos os vinculos, se false, atualizar apenas do campo que disparou o evento
 */
function vinculosCamposCidadao(componenteForm) {
    //busca o div de cidadao correspondente ao elemento
    var $componenteForm = $(componenteForm)
    var $divCidadao = $componenteForm.divFormMae();

    //testa se a funcao foi chamada aa partir de um elemento especifico (todos = false) ou se para refazer todos os vinculos do formulario
    var todos = $componenteForm.is($divCidadao);
    var tempoAnimacao = todos ? 0 : 200;

    //se a funcao foi chamada para um elemento especifico, atualiza somente os vinculos desse elemento
    if (todos || $componenteForm.is($divCidadao.find('.checkMaeDesconhecida')))
        mostraSe($divCidadao, !$divCidadao.find('.checkMaeDesconhecida').prop('checked'), 'txtNomeMae', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.checkPaiDesconhecido')))
        mostraSe($divCidadao, !$divCidadao.find('.checkPaiDesconhecido').prop('checked'), 'txtNomePai', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.checkSemRenda')))
        mostraSe($divCidadao, !$divCidadao.find('.checkSemRenda').prop('checked'), 'txtRendaMensal', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.selectDeficiencia')))
        mostraSe($divCidadao, $divCidadao.find('.selectDeficiencia').val() == 'SIM', 'deficiencia', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.selectEstudando')))
        mostraSe($divCidadao, $divCidadao.find('.selectEstudando').val() == 'SIM', 'estudando', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.selectInstitucionalizado')))
        mostraSe($divCidadao, $divCidadao.find('.selectInstitucionalizado').val() == 'SIM', 'divInstituicaoAcolhedora', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.selectIdentidadeSexual')))
        mostraSe($divCidadao, $divCidadao.find('.selectIdentidadeSexual').val() == '0'/*outros*/, 'divOutraIdentidadeSexual', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.selectDoencaGrave')))
        mostraSe($divCidadao, $divCidadao.find('.selectDoencaGrave').val() == 'SIM', 'divNomeDoenca', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.selecSituacaoRua')))
        mostraSe($divCidadao, $divCidadao.find('.selecSituacaoRua').val() == 'SIM', 'situacao-rua', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.txtDataNascimento')))
        mostraSe($divCidadao, $divCidadao.find('.txtDataNascimento').val() == '', 'divIdadeAproximada', tempoAnimacao);

    if (todos || $componenteForm.is($divCidadao.find('.opcaoViolacao')))
        mostraSe($divCidadao, $divCidadao.find('.opcaoViolacao:checked').length > 0, 'divDetalheViolacao', tempoAnimacao);

    sugestoesCampos($divCidadao);

    /**
     * Mostra (ou esconde), caso a condicao passada seja verdadeira, todos os elementos com a classe classeCss, contidos em divMae
     * @param divMae - pode ser o div principal do formulario ou qualquer elemento aa partir do qual se possa chegar a ele
     */
    function mostraSe(divMae, condicao, classeCss, tempoAnimacao) {
        var $elementos = $(divMae).divFormMae().find('.' + classeCss)
        if (condicao) { //exibir
            $elementos.fadeIn(tempoAnimacao);
            $elementos.removeClass('sempre-escondido');
        } else { //esconder
            $elementos.fadeOut(tempoAnimacao);
            $elementos.addClass('sempre-escondido');
            $elementos.limpaConteudo();
        }
    }

};

/**
 * Esconde ou exibe alguns elementos visuais de acordo com o preenchimento de campos relacionados
 * Deve ser chamado em todos os formularios
 * @param divMae - formulario submetido ao refresh dos vinculos de campos
 */
function sugestoesCampos(divMae) {
    var $divMae = $(divMae).divFormMae();

    //Percorre todos os campos passiveis de sugestão e exibe as sugestoes apenas se o conteudo do campo estiver vazio
    $divMae.find('.sugestaoPreenchimento').closest('.fieldcontain').each(function () {
        var $divCampo = $(this);
        if ($divCampo.find('.destinoSugestao1').val()) {
            $divCampo.find('.sugestaoPreenchimento').hide(0)
        } else {
            $divCampo.find('.sugestaoPreenchimento').show(0);
        }
    });

};

/**
 * Evento de click em botões que preencham automaticamente um (ou ate dois) campos com valores sugeridos pelo sistema
 * (ex: local de nascimento "belo horizonte (MG)"
 * @param componenteSugestao botão que acionou o evento
 */
function clickSugestao(componenteSugestao) {
    //identifica o contexto (div fieldcontain) em que o botão de sugestão foi acionado
    var $divCampo = $(componenteSugestao).closest('.fieldcontain');
    //busca os valores sugeridos
    var origemSugestao1 = $divCampo.find('.origemSugestao1').text();
    var origemSugestao2 = $divCampo.find('.origemSugestao2').text();
    //preenche com as sugestoes
    $divCampo.find('.destinoSugestao1').val(origemSugestao1);
    $divCampo.find('.destinoSugestao2').val(origemSugestao2);
    //inibe a sugestao
    $divCampo.find('.sugestaoPreenchimento').fadeOut(200);
}

/*
 //pula de um campo para outro no mesmo formulario
 function irPara(elementoOrigem, classeCss) {
 var $destino = $(elementoOrigem).divFormMae().find('.'+classeCss)
 $destino.focus();

 //desloca a tela para não ficar escondido
 var alturaBarraNavegacao = $('#navFamilia').outerHeight();
 window.scrollBy(0, -1 * (alturaBarraNavegacao + 20));

 //pisca para chamar a atenção do operador
 $destino.fadeTo(500, 0, function() { $(this).fadeTo(500, 1.0); });
 $destino.fadeTo(500, 0, function() { $(this).fadeTo(500, 1.0); });
 }
 */

function atualizaTituloAba(input) {
    //primeiro nome
    var nomeAba = input.value.split(" ")[0];
    if (nomeAba)
    //primeira letra maiuscula
        nomeAba = nomeAba.substr(0, 1).toUpperCase() + nomeAba.substr(1).toLowerCase();
    else
        nomeAba = '(sem nome)';
    $('.nav .link-selecionado').text(nomeAba);
    $('.nav .link-selecionado').attr('title', input.value);
}

var ModoInicializacao = {
    criacao: function() {
        modoCriacao = true;
        $('#btnCreate').removeClass('hidden');
        $('#txtNomeReferencia').focus();
    },

    edicao: function(idCidadao) {
        modoEdicao = true;
        $('.links-forms').removeClass('hidden');
        $('#btnAdicionarCidadao').removeClass('hidden');
        $('#btnImprimir').removeClass('hidden');
        $('#btnGravar').removeClass('hidden');
        $('#btnConcluir').removeClass('hidden');
        if (idCidadao) { //se um id de cidadao for passado, abre edição na aba deste cidadao
            //Procura pelo id do cidadao em todas as abas
            var $divCidadao = $('.id-cidadao[value="'+idCidadao+'"]').closest('.forms-cidadao');
            //Retorna a ordem (ord) correspondente do cidadao nas abas
            var ordCidadao = $divCidadao.find('.ord-cidadao').val();
            $('.cidadao-detalhado:eq('+ordCidadao+')').click();
        } else //se nenhum id de cidadao for passado, abre edição na aba da familia
            $('#linkFamilia').click();
    },

    continuarCriacao: function() {
        modoContinuarCriacao = true;
        $('.links-forms').removeClass('hidden');
        $('#btnAdicionarCidadao').removeClass('hidden');
        $('#btnImprimir').removeClass('hidden');
        $('#btnGravar').removeClass('hidden');
        $('#btnConcluir').removeClass('hidden');
        //clica na aba da referencia familiar
        $('.links-forms.cidadao-detalhado')[0].click();
        //dirige o foco para o segundo campo do formulario da referencia familiar
        $('.forms-cidadao .fieldcontain input')[1].focus();
    }
}

/**
 * Preenche automaticamente membros da familia
 */
function autocompletePaiMaeCuidador() {

    $('.txtNomePai, .txtNomeMae, .txtCuidadorPrincipal').autocomplete({
        minLength: 0,
        scroll: true
    }).focus(function () {
        $this = $(this);
        var parentes = listaParentes($this);
        //Busca pelo inicio do nome ( https://api.jqueryui.com/autocomplete/#entry-examples )
        $this.autocomplete({
            source: function (request, response) {
                var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex(request.term), "i");
                response($.grep(parentes, function (item) {
                    return matcher.test(item);
                }));
            }
        });
        $this.autocomplete("search", "");
    });

    /**
     * Lista todos os nomes preenchidos nas abas de membros, EXCETO NA ABA ATUAL,
     * incluindo os campos de nome completo, nome da mae e do pai
     */
    function listaParentes($elementoAtual) {
        var result = [];
        var $divCidadaoAtual = $elementoAtual.divFormMae();
        $('.forms-cidadao').each(function () {
            var $divCidadao = $(this);
            if (!$divCidadao.is($divCidadaoAtual))
                $divCidadao.find('.txtNomePai, .txtNomeMae, .txtNomeCompleto').each(function () {
                    if ($(this).val() && !result.includes($(this).val()))
                        result.push($(this).val());
                });
        });
        //ordena resultado despresando letras maiusculas/minusculas
        return result.sort(function (a, b) {
            return (a.toLowerCase() < b.toLowerCase()) ? -1 : 1;
        });
    }
}

/**
 * Evento disparado após erro de gravação via ajax
 * @param errorCode codigo HTTP de erro
 * @param responseText string contendo mensagens de erro coletadas durante tentativa de gravacao
 */
function erroSave(status, responseText) {

    if (status == "401" /*This request requires HTTP authentication*/) {
        //alert("favor fazer login");
        janelaModalLogin.abreJanela({url: urlLoginAjax, largura: 500});
        clearInterval(timerSessaoExpirada);
        return;
    }

    if (status != "422" /*validation error*/) {
        Snackbar.show( {text: 'Erro inesperado durante a gravação', pos: 'bottom-center', duration: 0,
                        showSecondButton: true, secondButtonText: 'detalhes', secondButtonTextColor: '#cc0000',
                        backgroundColor: coresSnackbar.erro.background, textColor: coresSnackbar.erro.text,
                        actionTextColor: coresSnackbar.erro.text, actionText: 'X',
                        onSecondButtonClick: function() { //exibe mais detalhes do erro em uma nova janela no browser
                            window.open().document.open().write(responseText);
                        }});
        return;
    }

    Snackbar.show( {text: 'Erro na gravação', pos: 'bottom-center', duration: 0,
                    backgroundColor: coresSnackbar.erro.background, textColor: coresSnackbar.erro.text,
                    actionTextColor: coresSnackbar.erro.text, actionText: 'X'});

    //converte a String JSON para um objeto javascript com a mesma estrutura do JSON
    var errosFamilia = JSON.parse(responseText);

    //define o div que conterá os erros da familia
    var $divErroFamilia = $('#divFamilia .erroValidacao')

    removeMensagensErro();

    //mensagens de erro globais da familia
    var erroNaFamilia = adicionaMensagem($divErroFamilia, errosFamilia.errosGlobais.mensagens);

    //mensagens de erro para cada campo da familia
    for (var nomeCampo in errosFamilia.mapaCampos) {
        ressaltaCampo($divErroFamilia, nomeCampo);
        erroNaFamilia = adicionaMensagem($divErroFamilia, errosFamilia.mapaCampos[nomeCampo].mensagens) || erroNaFamilia;
    }

    if (erroNaFamilia)
        ressaltaLink($('#linkFamilia'));

    //percorre cada membro em busca de erros
    for (var ordMembro in errosFamilia.mapaMembros) {
        adicionaMensagemMembro(ordMembro, errosFamilia.mapaMembros[ordMembro])
    }

    //determina o div do membro correspondente aa mensagem de erro a ser exibida, ressalta os campos na tela e exibe as mensagens
    function adicionaMensagemMembro(ordMembro, errosCidadao) {
        var $divMembro = $('#divMembros\\['+ordMembro+'\\] .erroValidacao')
        var $linkMembro = $('.cidadao-detalhado:eq('+ordMembro+')')
        ressaltaLink($linkMembro);
        for (var nomeCampo in errosCidadao.mapaCampos) {
            ressaltaCampo($divMembro, "."+nomeCampo);
            adicionaMensagem($divMembro, errosCidadao.mapaCampos[nomeCampo].mensagens)
        }
    }

    function ressaltaLink($linkMembro) {
        $linkMembro.addClass("aba-erro");
    }

    function ressaltaCampo($divErro, nomeCampo) {
        var $divForm = $divErro.closest(".forms-detalhados")
        var $divFieldContain = $divForm.find('*[name$="'+nomeCampo+'"]').closest(".fieldcontain");
        $divFieldContain.addClass("error");
    }

    function adicionaMensagem($divErro, mensagensErro) {
        if (!mensagensErro || mensagensErro.length == 0)
            return false;

        //Cria o elemento de lista para conter as mensagens de erro dentro do div (ou seleciona, caso ja exista)
        var $ul
        if ($divErro.children("ul").length == 0)
            $ul = $("<ul class='errors' role='alert'></ul>").appendTo($divErro)
        else
            $ul = $divErro.find("ul");

        //adiciona cada mensagem de erro na lista
        mensagensErro.forEach(function (mensagemErro) {
            $ul.append("<li>" + mensagemErro + "</li>");
        })
        return true;
    }
}

/**
 * Limpa todos os erros exibidos anteriormente, em todas as abas
 */
function removeMensagensErro() {
    $('.erroValidacao').empty();
    $('.fieldcontain.error').removeClass('error');
    $('.aba-erro').removeClass('aba-erro');
}

/**
 * Sinaliza gravação com sucesso e atualiza os ids dos novos membros gravados
 * @param responseText JSON com um mapa [ord, id] para cada membro
 */
function sucessoSave(responseText, mensagemSucesso, duracao) {
    if (! mensagemSucesso)
        mensagemSucesso = 'Família gravada com sucesso';
    if (! duracao)
        duracao = 5000;
    removeMensagensErro();
    Snackbar.show( {text: mensagemSucesso, pos: 'bottom-center', duration: duracao,
                    backgroundColor: coresSnackbar.info.background, textColor: coresSnackbar.info.text,
                    actionTextColor: coresSnackbar.info.text, actionText: 'X'});

    //todos os membros já foram persistidos
    //$('.cidadao-remover').remove();

    //percorre cada membro e, se for novo, insere um input hidden contendo o novo id gerado
    var mapaMembros = responseText['membros'];
    for (var ordMembro in mapaMembros) {
        var $divMembro = $('#divMembros\\['+ordMembro+'\\]')
        if ($divMembro.find('[name="membros\\['+ordMembro+'\\].id"]').length == 0)
            $divMembro.prepend("<input type='hidden' class='id-cidadao' name='membros["+ordMembro+"].id' value='"+mapaMembros[ordMembro]+"'/>")
        //como já foi gravado, não é mais possível excluir o membro diretamente pela aba
        $('#linkExcluirMembro\\['+ordMembro+'\\]').remove();
    }

    //percorre cada telefone: se for novo, insere um input hidden contendo o novo id gerado
    var mapaTelefones = responseText['telefones'];
    $('#tableTelefones .linha-telefone').each(function() {
        var $tr = $(this);

        var id = $tr.find('.idTelefone').val();
        //remover da tela telefones removidos do banco de dados:
        if (id) {
            console.log("id: " + id);
            //busca o id na tela entre os ids presentes no banco de dados
            var idPresente = false;
            for (var key in mapaTelefones) {
                if (mapaTelefones[key] == id)
                    idPresente = true;
            }
            if (!idPresente)
                $tr.remove()
        //atualizar id de novo telefone gravado no banco de dados:
        } else { //novo registro
            var chave = ifNull( $tr.find('.ddd-telefone').val() , '')
                + ifNull( $tr.find('.numero-telefone').val() , '');
            var novoId = mapaTelefones[chave];
            $tr.find('.idTelefone').val(novoId);
            console.log("novo telefone: "+chave+", id: "+novoId);
        }
    });
}

/*
function alertaCamposInteiros() {
    $('.integerMask').each(function () {
        $(this).on('keypress', function(event) {
            var char = event.which || event.keyCode;
            if (String.fromCharCode(char) === "." || String.fromCharCode(char) === ",")
                alert('Favor digitar o valor sem pontos e vírgulas, arrendondando se necessário')
        });
    });
}
*/

var CamposImportantes = {
    /**
     * Inicialização chamada no carregamento da página E NA CRIAÇÃO DE NOVOS MEMBROS
     */
    init: function() {
        //atribui "manualmente" ao checkbox Beneficiario Bolsa Familia (value=1) o comportamento de campos importantes
        $('.opcao-publico-prioritario input:checkbox[value="1"]').addClass('importante');

        //associa os eventos de mudanca de conteudo automaticamente para todos inputs com classe .importante*
        $('.importante').on('change', CamposImportantes.onChangeImportante);
        $('.importante-maior-18').on('change', CamposImportantes.onChangeImportanteMaior18);
        $('.importante-menor-18').on('change', CamposImportantes.onChangeImportanteMenor18);

        //evento especifico para o preenchimento da data de nascimento (acionado em conjunto com o evento onChangeImportante, pois a data de nascimento em si eh importante)
        $('.txtDataNascimento').on('change', function(){
            var $divCidadao = $(this).divFormMae();
            //a cada mudanca no campo data de nascimento, disparar o evento onChange dos campos cuja importancia esteja vinculada aa data de nascimento
            $divCidadao.find('.importante-maior-18').trigger('change');
            $divCidadao.find('.importante-menor-18').trigger('change');
        });

        //evento especifico para o preenchimento da idade aproximada (acionado em conjunto com o evento onChangeImportante, pois a idade aproximada em si eh importante)
        $('.txtIdadeAproximada').on('change', function(){
            var $divCidadao = $(this).divFormMae();
            //ignora caso ja haja um valor preenchido no campo data de nascimento, que tem prioridade sobre a idade aproximada
            if ($divCidadao.find('.txtDataNascimento').val() != '')
                return;
            //a cada mudanca no campo data de nascimento, disparar o evento onChange dos campos cuja importancia esteja vinculada aa data de nascimento
            $divCidadao.find('.importante-maior-18').trigger('change');
            $divCidadao.find('.importante-menor-18').trigger('change');
        });

        //aciona o evento de mudanca para colorir os campos necessarios
        $('.importante').trigger('change');
    },

    onChangeImportante: function() {
        //tenta colorir, se o campo estiver vazio
        CamposImportantes.colorir(this, true);
    },

    onChangeImportanteMenor18: function() {
        var maior18 = CamposImportantes.eMaior18(this);
        //tenta colorir se maior18==false ou maior18==null
        CamposImportantes.colorir(this, ! (maior18 === true));
    },

    onChangeImportanteMaior18: function() {
        var maior18 = CamposImportantes.eMaior18(this);
        //tenta colorir se maior18==true ou maior19==null
        CamposImportantes.colorir(this, ! (maior18 === false));
    },

    /**
     * Define se um campo deve ser colorido ou não, levando-se em conta: 1) o criterio inicial passado como parametro e 2) se o campo esta vazio.
     * Nem sempre o elemento que contem o valor a ser testado é o mesmo a colorir. No caso de checkbox, é o span em que esta contido como um t odo que será colorido
     * @param elemento elemento que acionou o evento
     * @param criterioColorir se vier false, o campo nao sera colorido. Se vier true, ainda precisamos verificar se o conteudo esta vazio antes de colorir
     */
    colorir: function(elemento, criterioColorir) {
        var $elementoValor = $(elemento)
        var $elementoColorir

        //determina o elemento a colorir
        if ($elementoValor.is(':checkbox')) { //checkbox
            criterioColorir = criterioColorir && ! $elementoValor.is(':checked'); //não colorir se já estiver marcado
            $elementoColorir = $elementoValor.closest('span'); //buscar o span pai do checkbox para colorir
        } else { //edit, memo, select...
            criterioColorir = criterioColorir && $elementoValor.val() === ''; //não colorir se já estiver preenchido
            $elementoColorir = $elementoValor; //colorir o próprio input
        }

        if (criterioColorir) {
            $elementoColorir.addClass('importante-vazio')
        } else {
            $elementoColorir.removeClass('importante-vazio')
        }
    },

    /**
     * Retorna verdadeiro se >=18, falso se <18 e null se não houver uma data de nascimento valida
     */
    eMaior18: function(caller) {
        //descobre o div relativo a este cidadao
        var $divCidadao = $(caller).divFormMae();
        //descobre a data de nascimento deste cidadao
        var $txtNascimento = $divCidadao.find('.txtDataNascimento');
        var idade = null
        if ($txtNascimento.val()) {
            //converte a data de nascimento
            var nascimento = moment($txtNascimento.val(), "DD/MM/YYYY", true)
            idade = nascimento.isValid() ? (moment().diff(nascimento, 'years')) : null
        } else {
            var $txtIdadeAproximada = $divCidadao.find('.txtIdadeAproximada');
            idade = isNaN($txtIdadeAproximada.val()) ? null : parseInt($txtIdadeAproximada.val());
        }

        return idade ? (idade >= 18) : null;
    }

}

/**
 * A cada mudanca em um campo de despesa, soma todas as despesas e exibe no spanTotalDespesas
 */
function despesaChange() {
    var total = 0;
    $('.despesa').each(function() {
        var valor = $(this).val();
        if (! isNaN(valor))
            total += (+valor)
    })
    $('#spanTotalDespesas').text(total);
}

/**
 * A cada mudanca em um campo de renda, soma todas as rendas de cada membro e exibe no spanTotalRendas
 */
function rendaChange() {
    var total = 0;
    $('.forms-cidadao').each(function() {
        var $divCidadao = $(this);
        //ignora eventual valor preechido se o checkbox Sem Renda estiver marcado
        if (! $divCidadao.find('.checkSemRenda').is(':checked')) {
            var valor = $divCidadao.find('.txtRendaMensal').val();
            if (! isNaN(valor))
                total += (+valor)
        }
    })
    $('#spanTotalRenda').text(total);
}

(function($) {
    /**
     * Busca o div do form que contem o elemento atual, seja um form de Domicilio, seja de de um Mebro Familiar
     */
    $.fn.divFormMae = function() {
        return $(this).closest('.forms-detalhados');
    }

    /**
     * Limpa o conteudo de todos os inputs, selects e textareas contidos no elemento atual, INCLUSIVE O PRÓPRIO ELEMENTO
     */
    $.fn.limpaConteudo = function() {
        //Primeiro limpa todos os inputs filhos (incluindo o proprio elemento, se ele for)
        $(this).find(':input').addBack(':input').each( function() {
            switch(this.type) {
                case 'password':
                case 'select-multiple':
                case 'select-one':
                case 'text':
                case 'textarea':
                    $(this).val('');
                    break;
                case 'checkbox':
                case 'radio':
                    this.checked = false;
            }
        });
    }
}(jQuery));

/**
 * Adiciona o evento onChange nos novos inputs com classe .importante recem criados
 */
function btnAdicionarTelefoneClick() {
    $('.importante').on('change', CamposImportantes.onChangeImportante);
}

/**
 * Após clicar em imprimir, caso a chamada ajax tenha sido bem sucedida
 */
function sucessoImprimir(data) {
    sucessoSave(data, "Família gravada com sucesso. Preparando download...", 6000);
    window.location = actionDowloadCadastro;

    //Desabilita o botão por 20 segundos, para evitar excesso de envios para o servidor
    var $btnImprimir = $('#btnImprimir');
    $btnImprimir.prop('disabled', true);
    setTimeout(function() { $btnImprimir.prop('disabled', false); }, 20000);
}

/**
 * Após clicar em concluir, caso a chamada ajax tenha sido bem sucedida
 */
function sucessoConcluir(data) {
    sucessoSave(data);
    var destino = actionEscolherFormulario;

    //acrescenta na url de escolherFormulario o id do membro atualmente exibido (se houver)
    var $hiddenIdCidadao = $divFormSelecionado.find('.id-cidadao');
    if ($hiddenIdCidadao)
        destino += "?idCidadao="+$hiddenIdCidadao.val();
    window.location = destino;
}

function submitCriacao(button) {
    //console.debug('submitCriacao');
    //console.debug(button.name + button.value);
    $(button.form).append($('<input>', {
            type: 'hidden',
            name: button.name,
            value: button.value
    }));

    //chama o metodo padrao (global) que testa o servidor antes de submeter um formulario
    submitProtegido(button.form);
}
