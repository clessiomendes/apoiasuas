<%@ page import="org.apoiasuas.util.ApoiaSuasException" %>
<%
    Object _bean = beanCamposEdicao
    String _nomeCampo = nomeCampo;
    //string utilizada como exemplo no campo (aparece em cinza quando ele esta vazio)
    String _placeholder = placeholder ?: null;
%>
<tmpl:campoEdicao>
    <g:textField name="${_nomeCampo}" size="60" maxlength="255" value="${_bean?.(_nomeCampo+'')}" placeholder="${_placeholder}"/>
</tmpl:campoEdicao>
