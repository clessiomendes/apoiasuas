package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import org.apoiasuas.util.ApoiaSuasException
import org.codehaus.groovy.grails.plugins.jquery.JQueryProvider
import org.codehaus.groovy.grails.plugins.web.taglib.FormTagLib
import org.codehaus.groovy.grails.plugins.web.taglib.JavascriptProvider
import org.codehaus.groovy.grails.plugins.web.taglib.JavascriptTagLib

/**
 * Reescrevendo algumas taglibs padroes do grails
 */
class OverrideTagLib {

    /**
     * Sobrescreve JavascriptTagLib.submitToRemote, basicamente para expor todos os atributos passados para o input button criado
     *
     * @attr url The url to submit to, either a map contraining keys for the action,controller and id or string value
     * @attr update Either a map containing the elements to update for 'success' or 'failure' states, or a string with the element to update in which cause failure events would be ignored
     * @attr before The javascript function to call before the remote function call
     * @attr after The javascript function to call after the remote function call
     * @attr asynchronous Whether to do the call asynchronously or not (defaults to true)
     * @attr method The method to use the execute the call (defaults to "post")
     */
    Closure submitToRemote = { attrs, body ->
        if (attrs.containsKey('onclick'))
            throw new ApoiaSuasException("O evento onclick não pode ser definido diretamente para a tag submitToRemote. Use 'before' ou 'after' ");

        JavascriptProvider p = JQueryProvider.newInstance()

        attrs.forSubmitTag = ".form"
        p.prepareAjaxForm(attrs)
        attrs.onclick = remoteFunction(attrs) + 'return false'
        attrs.remove('forSubmitTag');
        attrs.type = 'button'

        out << withTag(name: 'input', attrs: attrs) {
            out << body()
        }
    }


    /**
     * Sobrescreve JavascriptTagLib.javascript para fazer testes condicionais de exibicao
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
     * Sobrescreve FormTagLib.submitButton para fazer testes condicionais de exibicao
     * @attr name REQUIRED the field name
     * @attr value the button text
     * @attr type input type; defaults to 'submit'
     * @attr event the webflow event id
     * @attr showif condição de teste
     * @attr roles Um ou mais perfis de acesso ao sistema para restringir a exibição do botão
     */
    Closure submitButton = { attrs ->
        if (attrs.containsKey('showif') && attrs.showif == false)
            return;
        attrs.remove("showif");

        if (attrs.containsKey('roles') && ! SpringSecurityUtils.ifAnyGranted(attrs.roles))
            return;
        attrs.remove("roles");

        //Eh preciso buscar a tag original antes de executa-la, pois ela foi sobrescrita
        FormTagLib original = grailsAttributes.applicationContext.getBean(FormTagLib.name)
        original.submitButton.call(attrs)
    }

    /**
     * Sobrescreve FormTagLib.select para fazer testes condicionais de exibicao e para automatizar selecao de mapas
     *
     * @attr showif condição de teste
     * @attr roles Um ou mais perfis de acesso ao sistema para restringir a exibição deste elemento
     * @attr objectValue substitui o atributo value, usando um objeto cujo tipo espera-se que seja compativel com o atributo 'from'
     *
     * @attr name REQUIRED the select name
     * @attr id the DOM element id - uses the name attribute if not specified
     * @attr from REQUIRED The list or range to select from
     * @attr keys A list of values to be used for the value attribute of each "option" element.
     * @attr optionKey By default value attribute of each &lt;option&gt; element will be the result of a "toString()" call on each element. Setting this allows the value to be a bean property of each element in the list.
     * @attr optionValue By default the body of each &lt;option&gt; element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be a bean property of each element in the list.
     * @attr value The current selected value that evaluates equals() to true for one of the elements in the from list.
     * @attr multiple boolean value indicating whether the select a multi-select (automatically true if the value is a collection, defaults to false - single-select)
     * @attr valueMessagePrefix By default the value "option" element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be resolved from the I18n messages. The valueMessagePrefix will be suffixed with a dot ('.') and then the value attribute of the option to resolve the message. If the message could not be resolved, the value is presented.
     * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank - but you can also use 'null' in the case that you're passing the ID of an object
     * @attr forcarEscolha Se true, elimina a opção vazia da lista (se sobrepõe ao parametros noSelection)
     * @attr disabled boolean value indicating whether the select is disabled or enabled (defaults to false - enabled)
     * @attr readonly boolean value indicating whether the select is read only or editable (defaults to false - editable)
     */
    Closure select = { attrs ->
        if (attrs.containsKey('showif') && attrs.showif == false) {
            return;
            attrs.remove("showif");
        }

        if (attrs.containsKey('roles') && ! SpringSecurityUtils.ifAnyGranted(attrs.roles)) {
            return;
            attrs.remove("roles");
        }

        //o valor do select será setado aa partir de um objeto, e não de um id, como prevê a tag original
        if (attrs.containsKey('objectValue')) {
            boolean contido = false;
            attrs.from.each{
                if (it.id == attrs.objectValue?.id)
                    contido = true;
            }
            if (attrs.objectValue && ! contido)
                attrs.from = attrs.from + attrs.objectValue
            attrs.value = attrs.objectValue?.id
            attrs.remove("objectValue");
        }

        if (attrs.from instanceof Map) {
            if (! attrs.containsKey('optionKey'))
                attrs.optionKey = "key"
            if (! attrs.containsKey('optionValue'))
                attrs.optionValue = "value"
        }

        if (! attrs.containsKey('noSelection'))
            attrs.noSelection = ['': '']
        if (attrs.forcarEscolha?.toBoolean())
            attrs.remove('noSelection');

        //Eh preciso buscar a tag original antes de executa-la, pois ela foi sobrescrita
        FormTagLib original = grailsAttributes.applicationContext.getBean(FormTagLib.name)
        original.select.call(attrs)
    }

}
