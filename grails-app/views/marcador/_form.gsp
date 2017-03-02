<%@ page import="org.apoiasuas.marcador.Marcador" %>
<%
    Marcador marcadorDTO = marcadorInstance;
%>

<f:with bean="${marcadorDTO}">
    <f:field label="Descrição" property="descricao" widget-size="60"/>
    <f:field label="Habilitado" property="habilitado"/>
%{--Apresentar somente em modo de inclusão--}%
    <g:if test="${! marcadorDTO.id}">
        <div class="fieldcontain ${hasErrors(bean: marcadorInstance, field: 'servicoSistemaSeguranca', 'error')} required">
            <label for="servicoSistemaSeguranca">Disponível para</label>
            <g:select name="servicoSistemaSeguranca" noSelection="${['':'todos os serviços']}" from="${servicosDisponiveis.collect{it.nome}}" keys="${servicosDisponiveis.collect{it.id}}"/>
        </div>
    </g:if>
</f:with>