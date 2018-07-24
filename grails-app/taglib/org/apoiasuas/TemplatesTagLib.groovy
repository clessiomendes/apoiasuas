package org.apoiasuas

import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils

class TemplatesTagLib {

    static defaultEncodeAs = [taglib: 'raw']

    /**
     * @attr REQUIRED name atributo da classe de dominio cujo valor sera alimentado por este campo
     * @attr beanCamposEdicao instância à partir da qual obter o valor atual do campo (caso não seja passada, ainda tenta-se obter uma variável de mesmo nome no escopo do request)
     * @attr titulo string a ser exibida como titulo do campo
     * @attr quebraLinha se presente e verdadeiro, adiciona a classe css quebra-linha
     * @attr helpTooltip tooltip que aparece ao lado do titulo. pode ser uma string ou uma chave do arquivo de internacionalizacao
     * @attr obrigatorio sinaliza o campo como obrigatório
     */
    def campoEdicaoTexto = { attrs, body ->
        //<g:textField name="${_name}" size="60" maxlength="255" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        out << g.campoEdicao(template: '/servico/campoEdicao', beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
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
     * @attr obrigatorio sinaliza o campo como obrigatório
     */
    def campoEdicaoMemo = { attrs, body ->
        //<g:textArea name="${_name}" rows="8" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);
        attrs.classesDiv = 'tamanho-memo';
        if (! attrs.rows)
            attrs.rows = 8;

        out << g.campoEdicao(template: '/servico/campoEdicao', beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
                      name: attrs.name, titulo: attrs.titulo, helpTooltip: attrs.helpTooltip, classesDiv: attrs.classesDiv,
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
     */
    def campoEdicao = { attrs, body ->
        //<g:textArea name="${_name}" rows="8" value="${_bean?.(_name+'')}" placeholder="${_placeholder}"/>
        comumEdicao(attrs);

        if (! attrs.rows)
            attrs.rows = 8;
        out << render(template: '/layouts/campoEdicao', model: [beanCamposEdicao: attrs.beanCamposEdicao, quebraLinha: attrs.quebraLinha,
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

        out << render(template: '/servico/campoExibicao', model: attrs) { newBody }
    }


}
