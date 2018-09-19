package org.apoiasuas

import grails.util.Holders
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.Modulos
import org.apoiasuas.util.StringUtils

class TemplatesTagLib {

    static defaultEncodeAs = [taglib: 'raw']

    def segurancaService

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoTexto = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        if (attrs.value in Date)
            attrs.value = ((Date)attrs.value).format("dd/MM/yyyy");
        out << g.campoEdicao(template: '/servico/campoEdicao', plugin: Modulos.NUCLEO,
                beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                      name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, obrigatorio: attrs.obrigatorio) {
            g.textField(attrs);
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoCheckbox = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        if (attrs.value in Date)
            attrs.value = ((Date)attrs.value).format("dd/MM/yyyy");
        out << g.campoEdicao(template: '/servico/campoEdicao', plugin: Modulos.NUCLEO,
                beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                      name: attrs.name, titulo: "", helpTooltip: attrs.helpTooltip, obrigatorio: attrs.obrigatorio) {
            out << g.checkBox(attrs);
            out << " "+attrs.titulo;
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoSelect = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        out << g.campoEdicao(template: '/servico/campoEdicao', plugin: Modulos.NUCLEO,
                beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                      name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, obrigatorio: attrs.obrigatorio) {
            g.select(attrs);
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicaoMemo = { attrs, body ->
        //<g:textArea name="${_name}" rows="8" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        attrs.classesDiv = 'tamanho-memo';
        if (! attrs.rows)
            attrs.rows = 8;

        out << g.campoEdicao(template: '/servico/campoEdicao', plugin: Modulos.NUCLEO, beanCamposEdicao: attrs.beanCamposEdicao,
                      quebraLinha: attrs.quebraLinha, name: attrs.name, titulo: attrs.titulo,
                      helpTooltip: attrs.helpTooltip, classesDiv: attrs.classesDiv,
                      obrigatorio: attrs.obrigatorio) {
            g.textArea(attrs);
        }
    }

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     * @attr classesDiv classes css a serem inseridas no div (uma ou mais separadas por virgulas)
     */
    def campoEdicao = { attrs, body ->
        //<g:textArea name="${_name}" rows="8" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);

        if (! attrs.rows)
            attrs.rows = 8;
        out << render(template: '/layouts/campoEdicao', plugin: Modulos.NUCLEO,
                      model: [beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                      name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, classesDiv: attrs.classesDiv,
                      obrigatorio: attrs.obrigatorio]) {
            raw(body())
        }
    }

    private void comumEdicao(Map attrs) {
        attrs.beanCamposEdicao = attrs.beanCamposEdicao ?: request['beanCamposEdicao'] ?: null;
        attrs.value = attrs.value ?: attrs.beanCamposEdicao?.(attrs?.name);
    }

    /**
     * @attr conteudo atalho para passar um conteúdo sem ter que implementar um corpo na tag (este último, no entanto, tem prevalência quando presente)
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr titulo string a ser exibida como titulo do campo
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr escondeVazio default TRUE - Indica se o campo deve ser ignorado na rendereização quando o conteúdo passado é vazio
     * @attr classeCss css para estilização do elemento que agrupa as informações deste campo
     */
    def campoExibicao = { attrs, body ->
        String escondeVazio = attrs.remove('escondeVazio') ?: 'true';

        Object conteudo = attrs.conteudo;
        if (conteudo && conteudo instanceof Date)
            conteudo = g.formatDate(date: conteudo)?.toString()
        else if (conteudo) {
            conteudo = conteudo?.toString();
        }

        //Substitui o escape padrao do Grails por um customizado que interpreta \n como <br>
        def newBody = body() ?: conteudo ? StringUtils.toHtml(conteudo) : null
        if (escondeVazio.toBoolean() && ! newBody)
            return;

        out << render(template: '/servico/campoExibicao', plugin: Modulos.NUCLEO, model: attrs) { newBody }
    }

    /**
     * Inclui uma sessao usando o template /layout/sessao do plugin nucleo
     * @attr REQUIRED titulo
     * @attr REQUIRED icone
     */
    Closure layoutSessao = { Map attrs, body ->
        if (! attrs.hasProperty('model'))
            attrs.model = [:];
        attrs.each {
            attrs.model.put(it.key, it.value);
        }
        attrs.template = "/layouts/sessao";
        attrs.plugin = Modulos.NUCLEO;
        out << g.render(attrs, body);
    }

    /**
     * Renders a template inside views for collections, models and beans.
     * ==> Ignora eventuais modulos que não estão instalados no deploy (caso se gere uma versao customizada
     * que nao inclua todos os modulos)
     * Examples:<br/>
     * &lt;g:render template="atemplate" collection="${users}" /&gt;<br/>
     * &lt;g:render template="atemplate" model="[user:user,company:company]" /&gt;<br/>
     * &lt;g:render template="atemplate" bean="${user}" /&gt;<br/>
     *
     * @attr template REQUIRED The name of the template to apply
     * @attr contextPath the context path to use (relative to the application context path). Defaults to "" or path to the plugin for a plugin view or template.
     * @attr bean The bean to apply the template against
     * @attr model The model to apply the template against as a java.util.Map
     * @attr collection A collection of model objects to apply the template to
     * @attr var The variable name of the bean to be referenced in the template
     * @attr plugin REQUIRED The plugin to look for the template in
     */
    def renderComModulo = { Map attrs, body ->
        if (Holders.pluginManager.hasGrailsPlugin(attrs.plugin))
            out << g.render(attrs)
        else
            log.warn("ignorando plugin não instalado: "+attrs.plugin);
        return null;
    }

    /**
     * Banner na tela main.gsp
     */
    def banner = { Map attrs, body ->
//        System.out.println("tag banner");
        if (segurancaService.usuarioLogado && segurancaService.servicoLogado && segurancaService.servicoLogado.token == ServicoSistema.Tokens.CRJ)
            attrs.template = "/personalizados/layouts/bannerCRJ"
        else
            attrs.template = "/layouts/banner";
        attrs.plugin = Modulos.NUCLEO;
        out << g.render(attrs, body);
    }


}
