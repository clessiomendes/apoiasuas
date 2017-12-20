<%
    org.apoiasuas.processo.PedidoCertidaoProcessoDTO processoDTO = processoInstance
    List<org.apoiasuas.seguranca.UsuarioSistema> operadoresLocal = operadores
%>

<div class="fieldcontain ${hasErrors(bean: processoDTO, field: 'cadTransiente', 'error')} ">
    <label for="cadTransiente">
        Cad
    </label>
    <g:textField maxlength="10" size="3" name="cadTransiente" value="${processoDTO?.cadTransiente}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: processoDTO, field: 'operadorResponsavel', 'error')} required">
    <label for="operadorResponsavel">
        Técnico responsável
        <span class="required-indicator">*</span>
    </label>
    <g:select id="operadorResponsavel" required="" name="operadorResponsavel.id" from="${operadoresLocal}" optionKey="id" value="${processoDTO?.operadorResponsavel?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: processoDTO, field: 'dadosCertidao', 'error')} required">
    <label for="dadosCertidao">
        Dados da certidão
        <span class="required-indicator">*</span>
    </label>
    <span>
    <g:textField maxlength="250" size="80" required="" name="dadosCertidao" value="${processoDTO?.cadTransiente}"/>
    <br>Ex: Certidão de nascimento de Maria da Silva, nascida em 01/01/2001, filha de Joana da Silva e João da Silva
    </span>
</div>

<div class="fieldcontain ${hasErrors(bean: processoDTO, field: 'cartorio', 'error')} required">
    <label for="cartorio">
        Cartório
        <span class="required-indicator">*</span>
    </label>
    <g:textField maxlength="250" required="" size="80" name="cartorio" value="${processoDTO?.cartorio}"/>
    <br>Ex: Cartório de Registro de Pessoas Naturais, distrito de Ariranha, Pedra Azul - MG
</div>

<br>

<div class="fieldcontain ${hasErrors(bean: processoDTO, field: 'numeroAR', 'error')}">
    <label for="numeroAR">
        Número AR
    </label>
    <g:textField maxlength="20" size="10" name="numeroAR" value="${processoDTO?.numeroAR}"/>
</div>
