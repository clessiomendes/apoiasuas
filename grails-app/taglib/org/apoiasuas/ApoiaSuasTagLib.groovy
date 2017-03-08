package org.apoiasuas

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apoiasuas.anotacoesDominio.InfoDominioUtils
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.plugins.web.taglib.FormTagLib
import org.codehaus.groovy.grails.plugins.web.taglib.JavascriptTagLib
import org.codehaus.groovy.grails.web.util.StreamCharBuffer

class ApoiaSuasTagLib {
    static defaultEncodeAs = [taglib: 'raw']
    public static final String TABS_O_QUE_MONTAR = "TABS_O_QUE_MONTAR"
    public static final String TABS_MONTAR_MENU = "TABS_MONTAR_MENU"
    public static final String TABS_MONTAR_DIVS = "TABS_MONTAR_DIVS"
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

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
            if (grupo[0]) //se o grupo tiver nome, cria uma caixa para ele
                out << '<fieldset class="embedded"><legend class="collapsable" style="cursor:pointer;">'+grupo[0]+'</legend>'
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
     * Acrescenta um label com o nome do campo e espera, no corpo o(s) input(s) a serem preenchidos
     *
     * @attr instancia REQUIRED
     * @attr definicaoFormulario REQUIRED
     * @attr caminhoPropriedade REQUIRED o nome do campo (ou o caminho ate ele atraves das associacoes)
     * @attr label Descricao do campo (sobrepoe-se ao definido na anotacao da classe de dominio)
     */
    def divCampoFormulario = { attrs, body ->
        Object instancia = attrs.instancia
        String caminhoPropriedade = attrs.caminhoPropriedade
        Formulario definicaoFormulario = attrs.definicaoFormulario
        String label = attrs.label
        if (caminhoPropriedade && definicaoFormulario) {
            InfoPropriedadeDominio info = InfoDominioUtils.infoPropriedadePeloCaminho(instancia.getClass(), caminhoPropriedade)
            if (info && definicaoFormulario.campos.find { it.codigo == info.codigo() }) {
                out << '<div class="fieldcontain ' + hasErrors(bean: instancia, field: caminhoPropriedade, 'error') + ' ">'
                if (!label) {
//                    if (info.descricaoI18N())
//                        label = message(code: info.descricaoI18N())
//                    else
                        label = info.descricao()
                }
                out << '<label>' + label + '</label>'
                out << body()
                out << '</div> '
            } else {
                log.error("Ignorando campo de formulário não encontrado: ${info.codigo()}")
            }
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
    def divCampoFormularioCompleto = { attrs, body ->
        //TODO: juntar divCampoFormulario e divCampoFormularioCompleto
        CampoFormulario campoFormulario = attrs.campoFormulario
        boolean focoInicial = attrs.focoInicial
        String label = attrs.label

        if (campoFormulario) {
            out << '<div class="fieldcontain ' + hasErrors(bean: campoFormulario, 'error') + ' ">'
            if (!label) {
//                if (campoFormulario.descricaoI18N)
//                    label = message(code: campoFormulario.descricaoI18N)
//                else
                    label = campoFormulario.descricao
            }
            if (campoFormulario.obrigatorio)
                label += ' <span class="required-indicator">*</span> '
            out << '<label>' + label + '</label>'
            //Gera o input para preenchimento do campo.
            //Se, no entanto, um corpo já tiver sido fornecido, este sobrescreve o comportamento padrão
//            out << body ?: textField([
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
                            cols: campoFormulario.tamanho,
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
//        <g:actionSubmit value="${it.nome}" action="preencherFormulario" onclick="this.form.idFormulario.value = '${it.id}'; return true"/>
        Formulario formulario = attrs.formulario
        out << submitButton([value: formulario.nome, name: 'foo', onclick: "document.getElementById('preencherFormulario').idFormulario.value = '${formulario.id}'; document.getElementById('preencherFormulario').submit(); return true"])
    }

    /**
     * Overriding FormTagLib.submitButton to check for conditions before rendering
     */
    Closure submitButton = { attrs ->
        if (attrs.showif != null && attrs.showif == false)
            return;
        attrs.remove("showif");

        //Eh preciso buscar a tag original antes de executa-la, pois ela foi sobrescrita
        FormTagLib original = grailsAttributes.applicationContext.getBean(FormTagLib.name)
        original.submitButton.call(attrs)
    }

    /**
     * Overriding JavascriptTagLib.javascript to check for conditions before rendering
     * @attr showif teste para saber se o javascript sera incluido ou nao na pagina
     * @attr src The name of the javascript file to import. Will look in web-app/js dir
     * @attr library The name of the library to include. e.g. "jquery", "prototype", "scriptaculous", "yahoo" or "dojo"
     * @attr plugin The plugin to look for the javascript in
     * @attr contextPath the context path to use (relative to the application context path). Defaults to "" or path to the plugin for a plugin view or template.
     * @attr base specifies the full base url to prepend to the library name
     *
     */
    Closure javascript = { attrs, body ->
        //Uma vez que o parametro showif esta presente, testar se ele eh falso ou nulo e, neste caso, ignorar o javascript
        if (attrs.containsKey("showif") && (! attrs.showif))
            return;
        attrs.remove("showif");

        //Eh preciso buscar a tag original antes de executa-la, pois ela foi sobrescrita
        JavascriptTagLib original = grailsAttributes.applicationContext.getBean(JavascriptTagLib.name)
        original.javascript.call(attrs, body)
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
        log.debug("Help Tooltip Tag")
        String mensagem = body();
        if (attrs.chave && message(code: attrs.chave, args: attrs.args))
            mensagem = message(code: attrs.chave, args: attrs.args)
        if (! mensagem?.trim())
            return;

        out << "<div class='help-tooltip'>";
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

        //Javascript JQuery para adicionar o comportamento do componente no div sendo criado
        g.javascript{
            'jQuery(document).ready(function() {\n' +
            '   jQuery("#'+attrs.id+'").tabs();\n' +
            '} );'
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
 * Tag simples que gera um span com o corpo em seu interior mediante um teste
 * @attr showif REQUIRED teste usado com a tag if
 * @attr id id para o spam
 * @attr style estilos css para personalizar o componente
 */
    def spamCondicional = { attrs, body ->
        def showif = attrs.remove('showif')
        if (showif == null || showif == false)
            return;

        //MarkupBuilder para geração de HTML por meio de uma DSL groovy
        def html = new groovy.xml.MarkupBuilder(out)
        html.spam attrs, {
            mkp.yieldUnescaped(body());
        }
    }

}
