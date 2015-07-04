package org.apoiasuas

import org.apoiasuas.anotacoesDominio.InfoDominioUtils
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.seguranca.UsuarioSistema

class ApoiaSuasTagLib {
    static defaultEncodeAs = [taglib: 'raw']
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
                    optionKey: 'nomeCompleto',
                    noSelection: ['': ''],
                    from: request.usuarios
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
 */
    Closure linkMenu = { attrs, body ->
//        sec.ifAnyGranted roles: attrs.permissao, body: {
//            out << '<li class="controller">'
            out << sec.link(attrs, body)
//            out << '</li>'
//        }
    }

    /**
    * @attr formulario
    */
    Closure actionSubmitOpcaoFomulario = { attrs, body ->
//        <g:actionSubmit value="${it.nome}" action="preencherFormulario" onclick="this.form.idFormulario.value = '${it.id}'; return true"/>
        Formulario formulario = attrs.formulario
        out << actionSubmit([value: formulario.nome, onclick: "document.getElementById('preencherFormulario').idFormulario.value = '${formulario.id}'; document.getElementById('preencherFormulario').submit(); return true"])
    }

}
