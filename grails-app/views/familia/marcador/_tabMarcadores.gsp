<%@ page import="org.apoiasuas.marcador.Marcador" %>
<asset:javascript src="familia/marcador/marcadores.js"/>
<asset:stylesheet src="jquery.clearField/jquery.clearField.css"/>
<asset:javascript src="jquery.clearField/jquery.clearField.js"/>
<asset:stylesheet src="animate.css"/>

%{--Associa os eventos de busca incremental no input de pesquisa e do botao expandir--}%
<g:javascript>
    $(document).ready(function() {
        inicializaEventosTabMarcadores(fieldsetProgramas);
        inicializaEventosTabMarcadores(fieldsetAcoes);
        inicializaEventosTabMarcadores(fieldsetVulnerabilidades);
        inicializaEventosTabMarcadores(fieldsetOutrosMarcadores);

        // Stop user to press enter in textbox
        $("#txtProcurar").keypress(function(event) {
            if (event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        });

        //pesquisa incremental à partir do input de busca
        $("#txtProcurar").on('input', function() {
            filtraMarcadores(this)
        } );

    } );
</g:javascript>

<g:javascript showif="${permiteInclusao == 'true'}">
    /**
     * onclick de novo marcador - abrir a janela modal correspondente à partir do botão "Novo Marcador" e atualizar os
     * marcadores disponiveis em seguida.
     * (obs: não pode ser movido para um arquivo .js porque a função grails createLink precisa ser processada na gsp)
     */
    function novoMarcadorPrograma(functionAtualizaMarcadores) {
        abreJanelaNovoMarcador("Criar novo Programa","${createLink(action:'createPrograma', params: [idFamiliaDestino: familiaInstance.id])}", functionAtualizaMarcadores);
    }
    function novoMarcadorAcao(functionAtualizaMarcadores) {
        abreJanelaNovoMarcador("Criar nova Ação","${createLink(action:'createAcao', params: [idFamiliaDestino: familiaInstance.id])}", functionAtualizaMarcadores);
    }
    function novoMarcadorVulnerabilidade(functionAtualizaMarcadores) {
        abreJanelaNovoMarcador("Criar nova Vulnerabilidade","${createLink(action:'createVulnerabilidade', params: [idFamiliaDestino: familiaInstance.id])}", functionAtualizaMarcadores);
    }
    function novoMarcadorOutroMarcador(functionAtualizaMarcadores) {
        abreJanelaNovoMarcador("Criar nova Sinalização","${createLink(action:'createOutroMarcador', params: [idFamiliaDestino: familiaInstance.id])}", functionAtualizaMarcadores);
    }
</g:javascript>

<g:javascript showif="${familiaInstance != null}">
    /**
     * Metodos a ser chamada após uma criação bem sucedida de um novo marcador. Tais funcionalidades só estarão dipsoníveis
     * à partir do caso de uso de familias e deverão ser ignoradas nos demais casos de uso (como o de Listagem).
     * (obs: não pode ser movido para um arquivo .js porque a função grails remoteFunction precisa ser processada na gsp)
     */
    function updateListProgramas() {
        $("#rolagemProgramas").html('<asset:image src="loading.gif"/> carregando...');
        ${remoteFunction(action:'listProgramasDisponiveis', id: familiaInstance.id,
                update: [success: 'rolagemProgramas', failure: 'rolagemProgramas'],
                onFailure: 'alert("Erro carregando lista de programas (via ajax)");'
        )};
    }
    function updateListAcoes() {
        $("#rolagemAcoes").html('<asset:image src="loading.gif"/> carregando...');
        ${remoteFunction(action:'listAcoesDisponiveis', id: familiaInstance.id,
                update: [success: 'rolagemAcoes', failure: 'rolagemAcoes'],
                onFailure: 'alert("Erro carregando lista de ações (via ajax)");'
        )};
    }
    function updateListVulnerabilidades() {
        $("#rolagemVulnerabilidades").html('<asset:image src="loading.gif"/> carregando...');
        ${remoteFunction(action:'listVulnerabilidadesDisponiveis', id: familiaInstance.id,
                update: [success: 'rolagemVulnerabilidades', failure: 'rolagemVulnerabilidades'],
                onFailure: 'alert("Erro carregando lista de vulnerabilidades (via ajax)");'
        )};
    }
    function updateListOutrosMarcadores() {
        $("#rolagemOutrosMarcadores").html('<asset:image src="loading.gif"/> carregando...');
        ${remoteFunction(action:'listOutrosMarcadoresDisponiveis', id: familiaInstance.id,
                update: [success: 'rolagemOutrosMarcadores', failure: 'rolagemOutrosMarcadores'],
                onFailure: 'alert("Erro carregando lista de sinalizações (via ajax)");'
        )};
    }
</g:javascript>

<h4 class="animated bounceInLeft label-procurar pointer">info específica:</h4> <input id="txtProcurar" type="text" size="15" class="clear-field"
       title="Digite uma ou mais palavras chaves para procurar uma sinalização (programa, vulnerabilidade, ação ou outras) já definida.">

<fieldset id="fieldsetProgramas" class="fieldsetMarcadores">
    <legend>Programas <g:helpTooltip chave="help.marcador.programas"/>
        <g:if test="${permiteInclusao == 'true'}">
            &nbsp;<input type="button" class="speed-button-adicionar-marcador" onclick="novoMarcadorPrograma(updateListProgramas);"
                         title="Caso ainda não exista, você pode definir um novo programa em execução no seu território">
        </g:if>
    &nbsp;<input type="button" class="speed-button-expandir"
                 title="Expandir para ver todos os programas disponíveis">
    </legend>

    <div id="rolagemProgramas" class="rolagem">
        <g:render template="/familia/marcador/divMarcadoresDisponiveis" model="${[marcadoresDisponiveis: programasDisponiveis,
                                                                         label: 'programasDisponiveis',
                                                                         nomeDiv: 'divEditPrograma',
                                                                         classeMarcador: 'marcadores-programa']}"/>
    </div>
</fieldset>

<fieldset id="fieldsetVulnerabilidades" class="fieldsetMarcadores">
    <legend>Vulnerabilidades <g:helpTooltip chave="help.marcador.vulnerabilidades"/>
        <g:if test="${permiteInclusao == 'true'}">
            &nbsp;<input type="button" class="speed-button-adicionar-marcador" onclick="novoMarcadorVulnerabilidade(updateListVulnerabilidades);"
                         title="Caso ainda não exista, você pode definir uma nova categoria de vulnerabilidades">
        </g:if>
    &nbsp;<input type="button" class="speed-button-expandir"
                 title="Expandir para ver todas as vulnerabilidades disponíveis">
    </legend>

    <div id="rolagemVulnerabilidades" class="rolagem">
        <g:render template="/familia/marcador/divMarcadoresDisponiveis" model="${[marcadoresDisponiveis: vulnerabilidadesDisponiveis,
                                                                                  label: 'vulnerabilidadesDisponiveis',
                                                                                  nomeDiv: 'divEditVulnerabilidade',
                                                                                  classeMarcador: 'marcadores-vulnerabilidade']}"/>
    </div>
</fieldset>

<fieldset id="fieldsetAcoes" class="fieldsetMarcadores">
    <legend>Ações <g:helpTooltip chave="help.marcador.acoes"/>
        <g:if test="${permiteInclusao == 'true'}">
            &nbsp;<input type="button" class="speed-button-adicionar-marcador" onclick="novoMarcadorAcao(updateListAcoes);"
                         title="Caso ainda não exista, você pode definir uma nova categoria de ações previstas para serem executadas com as famílias">
        </g:if>
    &nbsp;<input type="button" class="speed-button-expandir"
                 title="Expandir para ver todas as ações disponíveis">
    </legend>

    <div id="rolagemAcoes" class="rolagem">
        <g:render template="/familia/marcador/divMarcadoresDisponiveis" model="${[marcadoresDisponiveis: acoesDisponiveis,
                                                                         label: 'acoesDisponiveis',
                                                                         nomeDiv: 'divEditAcao',
                                                                         classeMarcador: 'marcadores-acao']}"/>
    </div>
</fieldset>

<fieldset id="fieldsetOutrosMarcadores" class="fieldsetMarcadores">
    <legend>Outros tipos de sinalização <g:helpTooltip chave="help.marcador.outros.marcadores"/>
        <g:if test="${permiteInclusao == 'true'}">
            &nbsp;<input type="button" class="speed-button-adicionar-marcador" onclick="novoMarcadorOutroMarcador(updateListOutrosMarcadores);"
                         title="Caso ainda não exista, você pode definir um novo tipo de sinalização para as famílias">
        </g:if>
    &nbsp;<input type="button" class="speed-button-expandir"
                 title="Expandir para ver todos as sinalizações disponíveis">
    </legend>

    <div id="rolagemOutrosMarcadores" class="rolagem">
        <g:render template="/familia/marcador/divMarcadoresDisponiveis" model="${[marcadoresDisponiveis: outrosMarcadoresDisponiveis,
                                                                         label: 'outrosMarcadoresDisponiveis',
                                                                         nomeDiv: 'divEditOutroMarcador',
                                                                         classeMarcador: 'marcadores-outro-marcador']}"/>
    </div>
</fieldset>

<div id="janelaNovoMarcador"></div>