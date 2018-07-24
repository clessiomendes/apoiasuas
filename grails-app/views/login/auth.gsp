<%@ page import="org.apoiasuas.util.AmbienteExecucao" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
    <asset:stylesheet src="login/login.less"/>
</head>

<body>
<div id='login'>
    <div class='inner'>
        <div class='fheader'>
            <g:if test="${AmbienteExecucao.isDesenvolvimento()}">
                Ambiente de Desenvolvimento <br> Login: clessio Senha: senha
            </g:if>
            <g:if test="${AmbienteExecucao.isDemonstracao()}">
                Ambiente de Demonstração <br> Login: joao Senha: demo
            </g:if>
            <g:if test="${AmbienteExecucao.isProducao()}">
                Entre com login e senha
            </g:if>
        </div>

        <g:if test='${flash.message}'>
            <div class='login_message'>${flash.message}</div>
        </g:if>

        <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>

            <p>
                <label for='username'>Login</label>
                <input type='text' class='text_' name='j_username' id='username'/>
            </p>

            <p>
                <label for='password'>Senha</label>
                <input type='password' class='text_' name='j_password' id='password'/>
            </p>

            <p id="remember_me_holder">
                <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                <label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
            </p>

            <p>
                <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
            </p>
        </form>
    </div>
</div>
<script type='text/javascript'>
    <!--
    clearInterval(timerSessaoExpirada);

    (function() {
        document.forms['loginForm'].elements['j_username'].focus();
    })();
    // -->
</script>
</body>
</html>
