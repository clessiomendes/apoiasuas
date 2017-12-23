<%@ page import="org.apoiasuas.processo.PedidoCertidaoProcessoDTO" %>
<%
    org.apoiasuas.processo.PedidoCertidaoProcessoDTO processo = processo
%>

%{--Familia familia--}%
<g:if test="${processo.familia}">
<li class="fieldcontain">
    <span class="property-label">Familia</span>
    <span class="property-value" aria-labelledby="nomeCompleto-label">${processo.familia?.montaDescricao()}</span>
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

<fieldset id="atualizar" class="embedded"><legend>Atualizar</legend>

    <g:formRemote name="fool" method="post" url="[action: 'gravar', id: processo.id]" update="mensagemGravar">

        %{--String observacoes--}%
        <div class="fieldcontain tamanho-memo" >
                <label for="observacoesInternas">Observacoes (internas do sistema)</label>
                <g:textArea name="observacoesInternas" rows="3" value="${processo.observacoesInternas}"/>
        </div>

        %{--String numeroAR--}%
        <div class="fieldcontain">
                <label for="numeroAR">Número AR</label>
                <g:textField name="numeroAR" value="${processo.numeroAR}"/>
        </div>

        <br>
        <g:actionSubmit class="save" action="gravar" id="btnGravar" value="Gravar"/>
        <br><div id="mensagemGravar"></div>

    </g:formRemote>

</fieldset>