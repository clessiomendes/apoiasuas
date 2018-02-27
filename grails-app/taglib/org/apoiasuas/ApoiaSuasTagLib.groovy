package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.util.SimNao
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.lookup.DetalhesJSON
import org.apoiasuas.lookup.LookupRecord
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

class ApoiaSuasTagLib {
    static defaultEncodeAs = [taglib: 'raw']
    public static final String TABS_O_QUE_MONTAR = "TABS_O_QUE_MONTAR"
    public static final String TABS_MONTAR_MENU = "TABS_MONTAR_MENU"
    public static final String TABS_MONTAR_DIVS = "TABS_MONTAR_DIVS"
    public static final String COMPLEMENTO_OPCAO_DESATIVADA = " (opção desativada)"
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def segurancaService;
    def lookupService

    /**
     * Cria um div para um novo campo numa tela de formulario APENAS se o campo estiver previsto para o formulario em questao.
     * Acrescenta um label com o nome do campo e espera, no corpo o(s) input(s) a serem preenchidos
     *
     * @attr lista REQUIRED
     * @attr campoGrupo REQUIRED
     * @attr var
     * @attr status
     */
    def agrupaCampos = { attrs, body ->
        def var = attrs.var ?: "item"
        def status = attrs.status
        int i = 0
        ArrayList<String, List> listaDeGrupos = new ArrayList()
        String ultimoGrupo = "!#@*%!valor não utilizado"
//        if (attrs.lista instanceof List)
//            lista = attrs.lista
//        else
//            throw new RuntimeException("Esperado parâmetro lista do tipo java.util.List. Encontrado " + attrs.lista.class.name )

        //Primeiro separa os grupos em sub-listas
        attrs.lista.each { item ->
            String grupo = item."${attrs.campoGrupo}"
            if (grupo != ultimoGrupo)
                listaDeGrupos.add([grupo, new ArrayList()])
            ((List)listaDeGrupos.last()[1]).add(item)
            ultimoGrupo = grupo
        }

        //Para cada grupo encontrado...
        listaDeGrupos.each { grupo ->
            if (grupo[0]) { //se o grupo tiver nome, cria uma caixa para ele
                String classeCSS = "embedded ";
                if (StringUtils.contemIgnoraAcentos(grupo[0].toLowerCase(), "endereco"))
                    classeCSS += "endereco"
                out << '<fieldset class="'+classeCSS+'"><legend class="collapsable" style="cursor:pointer;">' + grupo[0] + '</legend>'
            }
            grupo[1].each { item -> //imprime o corpo da tag, usando o 'item' e o contador 'i' como parametros
                i++
                out << body((var):item, (status): i)
            }
            if (grupo[0])
                out << '</fieldset>'
            ultimoGrupo = grupo
        }
    }

    /**
     * Cria um div para um novo campo numa tela de formulario APENAS se o campo estiver previsto para o formulario em questao.
     * Acrescenta um label com o nome do campo e espera, no corpo o(s) input(s) a serem preenchidos pelo operador
     *
     * @attr campoFormulario REQUIRED
     * @attr focoInicial
     * @attr label Descricao do campo (sobrepoe-se ao definido na anotacao da classe de dominio)
     */
    def divCampoFormulario = { attrs, body ->
        CampoFormulario campoFormulario = attrs.campoFormulario
        boolean focoInicial = attrs.focoInicial
        String label = attrs.label

        if (campoFormulario) {
            String classeCSS = "fieldcontain " + (campoFormulario.mensagemErro ? 'error' : "") + " ";
            if (campoFormulario.multiplasLinhas > 1)
                classeCSS += " tamanho-memo "
            out << "<div class='$classeCSS'>";
            if (!label) {
                    label = campoFormulario.descricao
            }
            if (campoFormulario.obrigatorio)
                label += ' <span class="required-indicator">*</span> '
            out << '<label>' + label + '</label>'
            //Gera o input para preenchimento do campo.
            //Se, no entanto, um corpo já tiver sido fornecido, este sobrescreve o comportamento padrão
            if (body().asBoolean()) {
                out << body()
            } else {
                out << geraHtmlInput(campoFormulario, focoInicial)
            }
            out << '</div> '
        } else {
            log.error("Ignorando campo de formulário não encontrado: ${campoFormulario}")
        }
    }

    private String geraHtmlInput(CampoFormulario campoFormulario, boolean focoInicial) {
        if (campoFormulario.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO)
//        <g:select optionKey='id' optionValue="apelido" name="servico" id="servico" from="${Servico.list()}" noSelection="['null': '']"
//        onchange="${remoteFunction(controller: 'servico', action:'getServico', params:"'idServico='+escape(this.value)", onSuccess:'preencheEncaminhamentos(data)')}"/>
            return select(name: campoFormulario.caminhoCampo,
                    autofocus: focoInicial,
                    value: campoFormulario.valorArmazenado,
                    optionValue: 'username',
                    optionKey: 'id', //fixme: idUsuario
                    noSelection: ['': ''],
                    from: request.tecnicos
            )

        switch (campoFormulario.tipo) {
            case [CampoFormulario.Tipo.TEXTO, CampoFormulario.Tipo.INTEIRO]:
                if (campoFormulario.multiplasLinhas > 1)
                    return textArea(name: campoFormulario.caminhoCampo,
//                            cols: campoFormulario.tamanho,
                            rows: campoFormulario.multiplasLinhas,
                            autofocus: focoInicial,
                            value: campoFormulario.valorArmazenado)
                else
                    return textField(name: campoFormulario.caminhoCampo,
                            size: campoFormulario.tamanho,
                            autofocus: focoInicial,
                            class: campoFormulario.listaLogradourosCidadaos ? 'listaLogradouros' : '',
                            value: campoFormulario.valorArmazenado)
            case CampoFormulario.Tipo.TELEFONE:
                return render(template: "/emissaoFormulario/campoTelefone", model: [campoFormulario: campoFormulario])
            case CampoFormulario.Tipo.DATA:
                return textField(
                        name: campoFormulario.caminhoCampo,
                        class: "dateMask",
                        size: 10,
                        autofocus: focoInicial,
                        value: ((Date)campoFormulario.valorArmazenado)?.format("dd/MM/yyyy"))
            case CampoFormulario.Tipo.SELECAO:
                return select(name: campoFormulario.caminhoCampo,
                    autofocus: focoInicial,
                    value: campoFormulario.valorArmazenado,
                    noSelection: ['': ''],
                    from: campoFormulario.listaOpcoes())
            default:
                throw new RuntimeException("Impossível renderizar campo de entrada (input) para ${campoFormulario}. Tipo inesperado ${campoFormulario.tipo}".toString())
        }
    }

/**
 * Sobrescreve a tag padrao de geracao de links (link) para decorar itens de menu
 *
 * @attr controller The name of the controller to use in the link, if not specified the current controller will be linked
 * @attr action The name of the action to use in the link, if not specified the default action will be linked
 * @attr permissao ignora geracao do link caso o usuario nao detenha a permissao exigida

    Closure linkSeguro = { attrs, body ->
        if (attrs.controller == null && attrs.action == null && attrs.url == null && attrs.uri == null) {}

        String controller = attrs.controller
        String action = attrs.action

        if (controller) {
            GrailsApplication grailsApplication = getWebRequest().getAttributes().getGrailsApplication();
            final GrailsControllerClass controllerClass = (GrailsControllerClass) grailsApplication.getArtefactByLogicalPropertyName(ControllerArtefactHandler.TYPE, controller);
            if (controllerClass != null) {
                if (! action) {
                    action = controllerClass.getDefaultAction();
                }
                controllerClass.class
            }
        } else {
            out << sec.link(attrs, body)
        }
    }
*/

    /**
    * @attr formulario
    */
    Closure actionSubmitOpcaoFomulario = { attrs, body ->
        Formulario formulario = attrs.formulario
        out << submitButton([value: formulario.nome, name: 'foo', onclick: "document.getElementById('preencherFormulario').idFormulario.value = '${formulario.id}'; document.getElementById('preencherFormulario').submit(); return true"])
    }

/**
 * Cria um icone de ajuda com um texto suspenso a ser exibido quando o mouse passar por ele. O texto a ser
 * exibido pode ser 1) passado como uma chave de internacionalizacao pelo parametro message ou 2) no corpo
 * da tag e, neste caso, algumas formatações basicas de HTML são suportadas como <br>, <b>, etc. 1) tem
 * prioridade sobre 2).
 *
 * @attr chave chave do arquivo de internacionalização. Se ausente ou não encontrada, utiliza-se o corpo da tag como fonte para o texto de ajuda
 * @attr args A list of argument values to apply to the message, when chave is used.
 */
    def helpTooltip = { attrs, body ->
        String mensagem = body();
        if (attrs.chave && message(code: attrs.chave, args: attrs.args))
            mensagem = message(code: attrs.chave, args: attrs.args)
        if (! mensagem?.trim())
            return;
        //substitui quebras de linha pela tag html <br>
        mensagem = mensagem.replaceAll("\n", "<br>");

        String classes = attrs.class ?: ''
        String styles = attrs.style ?: ''
        out << "<div class='help-tooltip $classes' style='$styles' >";
        out << asset.image(src: 'help.png');
        out << "    <div class='help-tooltip-text'>";
        out << mensagem;
        out << "    </div>";
        out << '</div>';
    }

/**
 * Tag que monta um componente JQuery UI tabs. Depende da existência de tags <i>tab</i> em seu corpo, tantas vezes
 * quantos forem o número de tabs presentes no componente.
 * Na implementação, é necessário se processar duas vezes o corpo da tag. Em cada processamento, usamos um flag no request indicando
 * o que deverá ser renderizado: 1o) os menus <li> esperados pelo componente de tabs e 2o) os conteudos dos <div>s
 * @attr id REQUESTED id unico para o componente de tabs
 * @attr style estilos css para personalizar o componente
 */
    def tabs = { attrs, body ->
        //MarkupBuilder para geração de HTML por meio de uma DSL groovy
        def html = new groovy.xml.MarkupBuilder(out)

        out << asset.javascript(src: 'apoiasuas-tabs.js');

        //Javascript JQuery para adicionar o comportamento do componente no div sendo criado
        g.javascript{
            '\n\njQuery(document).ready(function() {\n' +
            '   inicializaTabs(jQuery("#'+attrs.id+'"));\n' +
            '} );\n\n'
        }

        //No primeiro processamento das tags <g:tab>, usamos a flag para sinalizar a montagem do menu por <ul>
        request.setAttribute(TABS_O_QUE_MONTAR, TABS_MONTAR_MENU);
        html.div id: attrs.id, style: attrs.style, {
            ul {
               mkp.yieldUnescaped(body());
            }
            //No segundo processamento das tags <g:tab>, usamos a flag para sinalizar a montagem dos <div>s
            request.setAttribute(TABS_O_QUE_MONTAR, TABS_MONTAR_DIVS);
            mkp.yieldUnescaped(body());
        }

        //fim dos processamentos: remover a flag do request
        request.removeAttribute(TABS_O_QUE_MONTAR);
    }

/**
 * tag a ser usada dentro do corpo da tag <g:tabs>, uma para cada aba do componente
 * @attr id REQUESTED id único de cada tab dentro do componente
 * @attr titulo REQUESTED titulos presente nas orelhas de cada tab
 * @attr template se presente, renderiza o conteúdo da tab aa partir de um template _.gsp. caso contrário, o corpo da tag é que é renderizado
 * @attr model parâmetros a serem passados para o template como modelo
 * @attr roles se estiver presente, condiciona a renderizacao do conteudo aa permissao de acesso exigida
 * @attr showif teste para determinar se o tab será exibido ou não, também pode ser passado um atributo de ServicoSistema.acessoSeguranca para que seja feito o teste
 */
    def tab = { attrs, body ->
        //Uma vez que o parametro showif esta presente, testar se ele eh falso ou nulo e, neste caso, ignorar o conteúdo
        if (attrs.containsKey("showif") && (! attrs.showif))
            return;
        attrs.remove("showif");

        if (attrs.containsKey('roles') && ! SpringSecurityUtils.ifAnyGranted(attrs.roles))
            return;

        //MarkupBuilder para geração de HTML por meio de uma DSL groovy
        def html = new groovy.xml.MarkupBuilder(out)

        //primeiro processamento, a flag TABS_MONTAR_MENU sinaliza a montagem da lista <lu> esperada pelo componente
        if (request.getAttribute(TABS_O_QUE_MONTAR) == TABS_MONTAR_MENU) {
            html.li { // <li>
                a href: "#"+attrs.id, { // <a href="">
                    mkp.yieldUnescaped(attrs.titulo);
                } // </a>
            } // </li>
        //segundo processamento, a flag TABS_MONTAR_DIVS sinaliza a montagem do corpo da tab
        } else if (request.getAttribute(TABS_O_QUE_MONTAR) == TABS_MONTAR_DIVS) {
            //verifica se foi passado um template como parâmetro e, caso contrário, gera o conteúdo aa partir do corpo da tag
            def tabConteudo = (attrs.template ? g.render(template: attrs.template, model: attrs.model) : body());
            html.div id: attrs.id, { // <div id="">
                mkp.yieldUnescaped(tabConteudo)
            } // </div>
        } else {
//            throw new ApoiaSuasException("Necessário definir flag $TABS_O_QUE_MONTAR no request antes de usar a tag <g:tab>")
        }
    }

/**
 * Sobrescreve a tag padrao de geracao de links (link) para decorar itens de menu e testar permissoes de acesso.
 * General linking to controllers, actions etc. Examples:<br/>
 *
 * &lt;g:link action="myaction"&gt;link 1&lt;/gr:link&gt;<br/>
 * &lt;g:link controller="myctrl" action="myaction"&gt;link 2&lt;/gr:link&gt;<br/>
 *
 * @attr acessoServico verifica se o serviço logado tem acesso a determinada funcionalidade.
 * @attr imagem para exibir no botao do link
 * @attr controller The name of the controller to use in the link, if not specified the current controller will be linked
 * @attr action The name of the action to use in the link, if not specified the default action will be linked
 * @attr uri relative URI
 * @attr url A map containing the action,controller,id etc.
 * @attr base Sets the prefix to be added to the link target address, typically an absolute server URL. This overrides the behaviour of the absolute property, if both are specified.
 * @attr absolute If set to "true" will prefix the link target address with the value of the grails.serverURL property from Config, or http://localhost:&lt;port&gt; if no value in Config and not running in production.
 * @attr id The id to use in the link
 * @attr fragment The link fragment (often called anchor tag) to use
 * @attr params A map containing URL query parameters
 * @attr mapping The named URL mapping to use to rewrite the link
 * @attr event Webflow _eventId parameter
 * @attr elementId DOM element id

 */
    def linkMenu = { attrs, body ->
        RecursosServico acessoServico = attrs.remove("acessoServico")
        if (! segurancaService.acessoRecursoServico(acessoServico))
            return;

        String imagem = attrs.remove("imagem");
        if (imagem)
            attrs.put("style","background-image: url('${asset.assetPath(src: imagem)}')");

        out << link(attrs, body)
    }

    /**
     * Tag genérica para gerar um elemento SOMENTE SE UMA CONDIÇÃO FOR ATENDIDA (showif)
     * obs: todos os atributos ta tag sao automaticamente repassados para o elemento criado
     * @attr elemento REQUIRED
     * @attr showif
     */
    def custom = { attrs, body ->
        if (attrs.containsKey('showif')) {
            if (attrs.showif == null || attrs.showif == false)
                return;
            attrs.remove("showif");
        }

        String elementoHTML = attrs.remove('elemento');
        out << "<"+elementoHTML+" ";
        attrs.each { k, v ->
            if (v != null)
                out << (k ?: '') + '="' + (v ?: '') + '" '
        }
        out << ">"
        out << body();
        out << "</$elementoHTML> "
    }

    /**
     * Select personalizado para tabelas lookup
     *
     * @emptyTag
     *
     * @attr name REQUIRED
     * @attr bean
     * @attr tabela (se não informada, sera extraida do nome do campo)
     *
     * @attr id the DOM element id - uses the name attribute if not specified
     * @attr keys A list of values to be used for the value attribute of each "option" element.
     * @attr optionKey By default value attribute of each &lt;option&gt; element will be the result of a "toString()" call on each element. Setting this allows the value to be a bean property of each element in the list.
     * @attr optionValue By default the body of each &lt;option&gt; element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be a bean property of each element in the list.
     * @attr value The current selected value that evaluates equals() to true for one of the elements in the from list.
     * @attr multiple boolean value indicating whether the select a multi-select (automatically true if the value is a collection, defaults to false - single-select)
     * @attr valueMessagePrefix By default the value "option" element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be resolved from the I18n messages. The valueMessagePrefix will be suffixed with a dot ('.') and then the value attribute of the option to resolve the message. If the message could not be resolved, the value is presented.
     * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank - but you can also use 'null' in the case that you're passing the ID of an object
     * @attr disabled boolean value indicating whether the select is disabled or enabled (defaults to false - enabled)
     * @attr readonly boolean value indicating whether the select is read only or editable (defaults to false - editable)
     */
    def selectLookup = { attrs, body ->
        String tabela = attrs.remove('tabela');
        String campo = attrs.name.contains('.') ? attrs.name.substring(attrs.name.lastIndexOf('.')+1) : attrs.name;
        Boolean campoDetalhe = attrs.name.contains('detalhe.')

        //extrai a tabela do nome do campo (ignorando eventual prefixo do mesmo)
        if (! tabela)
            tabela = campo;

        //noSelection="['': '']"
        if (! attrs.noSelection)
            attrs.put("noSelection",['': '']);

        List<LookupRecord> opcoes = lookupService.tabelas[tabela];
        if (! opcoes)
            throw new ApoiaSuasException("Lookup $tabela não encontrada entre as tabelas")

        if (! attrs.keys)
            attrs.put("keys", opcoes.findAll { it.ativo }.collect { it.codigo }  );

        if (! attrs.from)
            attrs.put("from", opcoes.findAll { it.ativo }.collect { it.descricao } );

        if (! attrs.value && attrs.bean) {
            if (campoDetalhe)
                //busca valor no mapa de detalhes, usando o nome do campo
                attrs.put("value", ((DetalhesJSON)attrs.remove('bean')).mapaDetalhes[campo]?.codigo )
            else
                //busca valor no proprio objeto de dominio, interpretando-o como um mapa e usando o nome do campo
                attrs.put("value", attrs.remove('bean')[campo]);
        }

        //Se o valor do campo corresponder a uma opção inativa, acrescenta na lista de opcoes assim mesmo
        if ( attrs.value ) {
            LookupRecord valor = opcoes.find { it.codigo+"" == attrs.value };
            if (! valor) {
                attrs.keys << attrs.value
                attrs.from << "(opção desconhecida)"
            } else if (! valor.ativo) {
                attrs.keys << valor.codigo
                attrs.from << valor.descricao + COMPLEMENTO_OPCAO_DESATIVADA;
            }
        }
        out << hiddenField([name: attrs.name+"_tipo", value: CampoDetalhe.Tipo.LOOKUP]);
        out << hiddenField([name: attrs.name+"_tabela", value: tabela]);
        out << select(attrs, body)
    }

    /**
     * Select personalizado para o caso de uso de Detalhes da Familia
     *
     * @emptyTag
     *
     * @attr name REQUIRED
     * @attr bean
     *
     * @attr id the DOM element id - uses the name attribute if not specified
     * @attr keys A list of values to be used for the value attribute of each "option" element.
     * @attr optionKey By default value attribute of each &lt;option&gt; element will be the result of a "toString()" call on each element. Setting this allows the value to be a bean property of each element in the list.
     * @attr optionValue By default the body of each &lt;option&gt; element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be a bean property of each element in the list.
     * @attr value The current selected value that evaluates equals() to true for one of the elements in the from list.
     * @attr multiple boolean value indicating whether the select a multi-select (automatically true if the value is a collection, defaults to false - single-select)
     * @attr valueMessagePrefix By default the value "option" element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be resolved from the I18n messages. The valueMessagePrefix will be suffixed with a dot ('.') and then the value attribute of the option to resolve the message. If the message could not be resolved, the value is presented.
     * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank - but you can also use 'null' in the case that you're passing the ID of an object
     * @attr disabled boolean value indicating whether the select is disabled or enabled (defaults to false - enabled)
     * @attr readonly boolean value indicating whether the select is read only or editable (defaults to false - editable)
     */
    def selectSimNao = { attrs, body ->
        String campo = attrs.name.contains('.') ? attrs.name.substring(attrs.name.lastIndexOf('.')+1) : attrs.name;
        Boolean campoDetalhe = attrs.name.contains('detalhe.')

        //noSelection="['': '']"
        if (! attrs.noSelection)
            attrs.put("noSelection",['': '']);

        if (! attrs.from) {
            attrs.put("from", SimNao.values() );
            attrs.put("optionValue","descricao");
        }

        if (! attrs.value && attrs.bean) {
            if (campoDetalhe)
                //busca valor no mapa de detalhes, usando o nome do campo
                attrs.put("value", ((DetalhesJSON)attrs.remove('bean')).mapaDetalhes[campo]?.codigo )
            else
                //busca valor no proprio objeto de dominio, interpretando-o como um mapa e usando o nome do campo
                attrs.put("value", attrs.remove('bean')[campo]);
        }

        out << hiddenField([name: attrs.name+"_tipo", value: CampoDetalhe.Tipo.BOOLEAN]);
        out << select(attrs, body)
    }

    /**
     * Campo de multi-seleção aa partir de uma tabela lookup
     *
     * @emptyTag
     *
     * @attr name REQUIRED the name of the checkbox
     * @attr bean
     * @attr help-tooltip prefixo I18N para mensagens de ajuda para cada opção da tabela (obs: no arquivo .properties, cada mensagem sera composto pelo prefixo passado como parametro acrescida de "." e o código correspondente da tabela lookup. Ex: help.lookup.violacao.1
     * @attr classeOpcao classes(s) css para formatar cada descrição (span) das opções geradas
     * @attr classeCheckbox classe(s) css para cada input checkbox gerado
     * @attr value lista com os codigos de valores a serem marcados por padrao (separados por virgulas)
     * @attr disabled if evaluates to true sets to checkbox to disabled
     * @attr readonly if evaluates to true, sets to checkbox to read only
     */
    def multiLookup = { attrs, body ->
//      optionKey="key" optionValue="value"
//      name="detalhe.areaOcupacao"
//      value="${localDtoFamilia.retornaDetalhes().areaOcupacao}"

        String tabela = attrs.remove('tabela');
        //extrai o campo aa partir do nome da elemento (ultima parte depois do ".")
        String campo = attrs.name.contains('.') ? attrs.name.substring(attrs.name.lastIndexOf('.')+1) : attrs.name;
        Boolean campoDetalhe = attrs.name.contains('detalhe.')
        String attrHelpTooltip = attrs.remove('help-tooltip')

        //extrai a tabela do nome do campo (ignorando eventual prefixo do mesmo)
        if (! tabela)
            tabela = campo;

        List<LookupRecord> opcoes = lookupService.tabelas[tabela];
        if (! opcoes)
            throw new ApoiaSuasException("Lookup $tabela não encontrada entre as  tabelas")

        //preenche uma lista de valores atuais, buscadas da tag ou do bean
        List conteudoAtual = [];
        if (attrs.value) {
            conteudoAtual = attrs.value.split(",");
        }  else if (attrs.bean) {
            if (campoDetalhe)
                //busca valores no mapa de detalhes, usando o nome do campo
                conteudoAtual = ((DetalhesJSON) attrs.remove('bean')).mapaDetalhes[campo]?.codigosList
            else
                //busca valor no proprio objeto de dominio, interpretando-o como um mapa e usando o nome do campo
                conteudoAtual = attrs.remove('bean')[campo];
        }

        out << hiddenField([name: attrs.name+"_tipo", value: CampoDetalhe.Tipo.MULTI_LOOKUP]);
        out << hiddenField([name: attrs.name+"_tabela", value: tabela]);
        opcoes.each { LookupRecord opcao ->
            //Mostrar tanto ativos quanto inativos que foram selecionados no passado
            if (opcao.ativo || opcao.codigo+"" in conteudoAtual) {
                Map checkAttrs = [:];
                checkAttrs << [id: attrs.name+opcao.codigo]
                checkAttrs << [name: attrs.name]
                checkAttrs << [value: opcao.codigo+""]
                checkAttrs << [checked: opcao.codigo+"" in conteudoAtual]
                checkAttrs << [readonly: attrs.readonly]
                checkAttrs << [disabled: attrs.disabled]
                checkAttrs << [class: attrs.classeCheckbox]
                //adiciona automaticamente todos os eventos on... (onclick, onchange, etc) a cada checkbox
                attrs.each {
                    if (it.key.toString().toLowerCase().startsWith("on"))
                        checkAttrs << [(it.key): it.value]
                }
                out << "<span class='${attrs.classeOpcao}' >"
                out << checkBox(checkAttrs);
                out << "<span>" + opcao.descricao + (opcao.ativo ? "" : COMPLEMENTO_OPCAO_DESATIVADA)
                if (attrHelpTooltip) {
                    String mensagemProperties = message(code: attrHelpTooltip+"."+opcao.codigo, default:'')
                    if (mensagemProperties)
                        out << helpTooltip(chave: mensagemProperties);
                }
                out << "</span></span>"
            }
        }

        //TODO: Se o valor do campo corresponder a uma opção inexistente, acrescenta na lista de opcoes assim mesmo
//            if (! valor) {
//                attrs.keys << attrs.value
//                attrs.from << "(opção desconhecida)"
//            }

    }

    /**
     * Checkbox específico para campos de detalhe
     *
     * @emptyTag
     *
     * @attr name REQUIRED
     * @attr bean
     *
     * @attr checked if evaluates to true sets to checkbox to checked
     * @attr disabled if evaluates to true sets to checkbox to disabled
     * @attr readonly if evaluates to true, sets to checkbox to read only
     * @attr id DOM element id; defaults to name
     */
/*
    Closure checkDetalhe = { attrs ->
        Boolean campoDetalhe = attrs.name.contains('detalhe.')
        if (! campoDetalhe)
            throw new ApoiaSuasException("TAG checkDetalhe (${attrs.name}) exclusiva para campos de detalhe")
        String campo = attrs.name.contains('.') ? attrs.name.substring(attrs.name.lastIndexOf('.')+1) : attrs.name;

        out << hiddenField([name: attrs.name+"_tipo", value: CampoDetalhe.Tipo.BOOLEAN]);

        attrs.put("value", SimNao.SIM.toString() )
        attrs.put("checked", SimNao.sim(((DetalhesJSON)attrs.remove('bean')).mapaDetalhes[campo]?.codigo ))
        out << checkBox(attrs);
    }
*/

    /**
     * Tag genérica para gerar um elemento SOMENTE SE UMA CONDIÇÃO FOR ATENDIDA (showif)
     * obs: todos os atributos ta tag sao automaticamente repassados para o elemento criado
     * @attr bean usado para teste de erros no campo
     * @attr model usado para teste de erros no campo
     * @attr field usado para teste de erros no campo
     * @attr showif teste de exibicao
     * @attr showto teste de exibicao para customiazacao por abrangencia territorial. Deve conter um ou mais CustomizacoesService.Codigos
     * @attr hidefrom teste de exibicao para customiazacao por abrangencia territorial. Deve conter um ou mais CustomizacoesService.Codigos
     */
    def fieldcontain = { attrs, body ->
        if (attrs.containsKey('showif')) {
            if (attrs.showif == null || attrs.showif == false)
                return;
            attrs.remove("showif");
        }

        if (attrs.containsKey('showto') && ! testaCustomizacao(attrs, 'showto'))
            return;
        attrs.remove('showto');

        if (attrs.containsKey('hidefrom') && testaCustomizacao(attrs, 'hidefrom'))
            return;
        attrs.remove('hidefrom');

        //acrescenta a classe fieldcontain ao conjunto de classes já passadas
        String classesCss = attrs.class ?: "";
        classesCss += " fieldcontain ";
        //simula a tag hasErrors em ValidationTagLib
        if (attrs.containsKey('bean') || attrs.containsKey('model')) {
            Map errorAttrs = attrs.findAll { it.key.toString() in ['bean','model','field'] }
            attrs.remove('bean')
            attrs.remove('model')
            attrs.remove('field')
            ValidationTagLib validationTagLib = grailsAttributes.applicationContext.getBean(ValidationTagLib.name)
            if (validationTagLib.extractErrors(errorAttrs))
                classesCss += " error "
        }

        attrs.class = classesCss;
        attrs.elemento = "div"
        out << custom(attrs, body);
    }

    public boolean testaCustomizacao(Map attrs, String key) {
        //como o servico CustomizacoesService tem escopo de sessao, nao pode ser declarado diretamente em uma taglib
        CustomizacoesService customizacoesService = grailsApplication.mainContext.customizacoesService;
        def codigos = attrs.remove(key);
        return customizacoesService.contem(codigos);
    }

}
