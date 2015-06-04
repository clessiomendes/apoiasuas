<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
  		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		<g:layoutHead/>
		<r:layoutResources/>
	</head>
	<body>
	<div id=${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "grailsLogoProd" : "grailsLogoNonProd"} role="banner">
		<table><tr><td>
			%{--FIXME: como direcionar para a raiz da aplicacao--}%
			<a href="${createLink(uri: '/')}"><asset:image src="apoiasuas_logo.png" alt="Grails"/></a>
			<sec:ifLoggedIn>
				</td><td>
			</sec:ifLoggedIn>
		</td></tr></table>
	</div>
	<g:layoutBody/>
		<div class=${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "footerProd" : "footerNonProd"} role="contentinfo">
        <form name="logout" method="POST" action="${createLink(controller: 'logout')}">
            %{--TODO: Alinhar botao de logout aa direita e estilizar SEM USAR TABELAS--}%
            Usuário: <sec:loggedInUserInfo field="username"/> ${org.apoiasuas.util.AmbienteExecucao.toString()}
            <input type="submit" value="sair">
        </form>
        </div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		<r:layoutResources/>
	</body>
</html>
