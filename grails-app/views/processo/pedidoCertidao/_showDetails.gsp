<%@ page import="org.apoiasuas.processo.PedidoCertidaoProcessoDTO" %>
<%
    org.apoiasuas.processo.PedidoCertidaoProcessoDTO processo = processo
%>

%{--Familia familia--}%
<g:if test="${processo.familia}">
<li class="fieldcontain">
    <span class="property-label">Familia</span>
    <span class="property-value" aria-labelledby="nomeCompleto-label">${processo.familia?.toString()}</span>
</li>
</g:if>

%{--UsuarioSistema operadorResponsavel--}%
<li class="fieldcontain">
    <span class="property-label">Operador responsável</span>
    <span class="property-value" aria-labelledby="nomeCompleto-label">${processo.operadorResponsavel?.username}</span>
</li>

%{--String dadosCertidao--}%
<li class="fieldcontain">
    <span class="property-label">Dados da certidão</span>
    <span class="property-value" aria-labelledby="nomeCompleto-label">${processo.dadosCertidao}</span>
</li>

%{--String cartorio--}%
<li class="fieldcontain">
    <span class="property-label">Cartório</span>
    <span class="property-value" aria-labelledby="nomeCompleto-label">${processo.cartorio}</span>
</li>

%{--String numeroAR--}%
<g:formRemote name="fool" method="post" url="[action: 'gravaAR', id: processo.id]"
              onSuccess="alert('AR gravada com sucesso');" onFailure="alert(XMLHttpRequest.responseText)">
    <div class="fieldcontain">
        <label for="numeroAR">Número AR</label>
        <g:textField name="numeroAR" value="${processo.numeroAR}"/>
        <g:actionSubmit class="save" action="gravaAR" id="btnGravaAR" value="Alterar"/>
    </div>
</g:formRemote>