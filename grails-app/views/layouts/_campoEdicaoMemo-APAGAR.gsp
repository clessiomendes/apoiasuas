<%@ page import="org.apoiasuas.util.ApoiaSuasException" %>
<%
    Object _bean = beanCamposEdicao
    String _nomeCampo = nomeCampo;
    //string utilizada como exemplo no campo (aparece em cinza quando ele esta vazio)
    String _placeholder = placeholder ?: null;
%>
<tmpl:campoEdicao tamanhoMemo="true">
    <g:textArea name="${_nomeCampo}" rows="8" value="${_bean?.(_nomeCampo+'')}" placeholder="${_placeholder}"/>
</tmpl:campoEdicao>

