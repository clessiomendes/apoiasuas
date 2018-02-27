%{--
                LOGIN EM TELA MODAL VIA AJAX

Com base na proposta em http://grails-plugins.github.io/grails-spring-security-core/2.0.x/guide/authentication.html#ajax

--}%

<asset:stylesheet src="login/login.less"/>

<script type='text/javascript'>
    <!--
    (function() {
        document.forms['ajaxLoginForm'].elements['j_username'].focus();
    })();

    /**
     * Substituicao o comportamento padrao de submissao do formulario pela chamada ajax authAjax()
     */
    $(function() {
       $("#ajaxLoginForm").submit(function(event) {
          event.preventDefault();
          authAjax();
       });
    });

    /**
     * Implementa uma submissao de login via ajax cujo resultado, se positivo, resulta apenas no fechamento da janela
     * modal e continuacao do sistema na tela aberta anteriormente ao login.
     */
    function authAjax() {
       $("#loginMessage").html("Autenticando...").show();

       var form = $("#ajaxLoginForm");
       $.ajax({
          url:       form.attr("action"),
          method:   "POST",
          data:      form.serialize(),
          dataType: "JSON",
          success: function(json, textStatus, jqXHR) {
             if (json.success) {
                form[0].reset();
                $("#loginMessage").empty();
                 janelaModalLogin.fechaJanela();
                 //Reinicia contagem de tempo de sessao
                 iniciaTimerSessaoExpirada();
             } else if (json.error)
                $("#loginMessage").html('<span class="errorMessage">' + json.error + "</error>");
             else
                $("#loginMessage").html(jqXHR.responseText);
          },
          error: function(jqXHR, textStatus, errorThrown) {
             if (jqXHR.status == 200) { // na verdade, nao eh um erro
                 janelaModalLogin.fechaJanela();
             } else if (jqXHR.status == 401 && jqXHR.getResponseHeader("Location")) {
                // the login request itself wasn't allowed, possibly because the
                // post url is incorrect and access was denied to it
                $("#loginMessage").html('<span class="errorMessage">' +
                   'Sorry, there was a problem with the login request</error>');
             }
             else {
                var responseText = jqXHR.responseText;
                if (responseText) {
                   var json = $.parseJSON(responseText);
                   if (json.error) {
                      $("#loginMessage").html('<span class="errorMessage">' + json.error + "</error>");
                      return;
                   }
                } else {
                   responseText = "Sorry, an error occurred (status: " + textStatus + ", error: " + errorThrown + ")";
                }
                $("#loginMessage").html('<span class="errorMessage">' + responseText + "</error>");
             }
          }
       });
    }
    // -->
    //# sourceURL=authAjax

</script>

<div id='login'>
    <div class='inner' style='width: inherit; margin: 10px'>
        <div class='fheader'>Atenção! Após ${session.maxInactiveInterval / 60} minutos sem uso, é necessário um novo login para continuar usando o sistema.</div>

        <form action="${request.contextPath}/j_spring_security_check" method='POST' id='ajaxLoginForm' class='cssform' autocomplete='off'>
            <p>
                <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
                <input type='text' class='text_' name='j_username' id='username'/>
            </p>

            <p>
                <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
                <input type='password' class='text_' name='j_password' id='password'/>
            </p>

            <p id="remember_me_holder">
                <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                <label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
            </p>

            <p>
                <input type='submit' id="authAjax" value='${message(code: "springSecurity.login.button")}'/>
                <input type='button' value='Cancelar' onclick="janelaModalLogin.cancelada();"/>
            </p>
        </form>

        <div class='login_message' id="loginMessage"></div>
    </div>
</div>
