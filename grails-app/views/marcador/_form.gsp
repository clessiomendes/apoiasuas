<%@ page import="org.apoiasuas.marcador.Marcador" %>
<%
    Marcador marcadorDTO = marcadorInstance;
%>

<f:with bean="${marcadorDTO}">
    <f:field label="Descrição" property="descricao"/>
</f:with>