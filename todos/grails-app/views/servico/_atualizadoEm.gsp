<%
    Object theBean = bean ?: beanCamposEdicao;
    String data = theBean ? g.formatDate(date: theBean.properties[campo+'Data']) : '';
%>
<span class="atualizado-em" title="atualizado em ${data}">
    <g:if test="${data}">
        <asset:image src="/refresh-gray.png" class="atualizado-em"/>${data}
    </g:if>
</span>