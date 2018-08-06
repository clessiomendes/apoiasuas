<%@ page import="org.apoiasuas.util.ApoiaSuasException" %>
<%
    String _classesDiv = classesDiv ?: '';
    String _obrigatorio = obrigatorio?.toBoolean() ? raw('<span class="required-indicator">*</span>') : '';
    if (obrigatorio?.toBoolean())
        _classesDiv = _classesDiv + ' required ';
    if (quebraLinha?.toBoolean())
        _classesDiv = _classesDiv + ' quebra-linha ';

    String _helpTooltip = helpTooltip ? (
            g.message(code: helpTooltip) ? g.helpTooltip(chave: helpTooltip) : g.helpTooltip(body: helpTooltip)
    ) : '';

%>
<div class="fieldcontain ${_classesDiv} ${hasErrors(bean: beanCamposEdicao, field: name, 'error')}">
    <label>${titulo}${_obrigatorio}${_helpTooltip}</label>
    ${raw(body())}
</div>
