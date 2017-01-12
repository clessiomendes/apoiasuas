<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
    <asset:stylesheet src="especificos/login.less"/>
</head>

<body>
<div id='login'>
    <div class='inner'>
        <div class='fheader'>Escolha o serviço de trabalho</div>

        <g:if test='${flash.message}'>
            <div class='login_message'>${flash.message}</div>
        </g:if>

        <g:form action="servicoEscolhido" method="PUT" class='cssform' autocomplete='off'>
            <p>
                <label for='servicoSistema'>Serviço</label>
                <g:select autofocus="" name="servicoSistema" noSelection="${['':'']}" from="${servicosDisponiveis.collect{it.nome}}" keys="${servicosDisponiveis.collect{it.id}}" required="" />
            </p>
            <p>
                <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
            </p>
        </g:form>
    </div>
</div>
</body>
</html>
