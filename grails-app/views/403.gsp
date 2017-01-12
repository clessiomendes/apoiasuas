<!DOCTYPE html>
<html>
	<head>
		<title>Erro Acesso Negado</title>
		<meta name="layout" content="main">
		<g:if env="development"><asset:stylesheet src="especificos/errors.css"/></g:if>
	</head>
	<body>
	<ul class="errors">
		<li>Acesso negado</li>
	</ul>
		<g:if env="development">
			<g:renderException exception="${exception}" />
		</g:if>
	</body>
</html>
