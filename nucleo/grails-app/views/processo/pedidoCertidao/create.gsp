<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="Pedido de Certidão" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#create-link" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="list" action="preList">Listar</g:link></li>
			</ul>
		</div>
		<div id="create-link" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>

			<g:render template="/mensagensPosGravacao" model="[bean: processoInstance]"/>

			<g:form url="[controller:'pedidoCertidaoProcesso', action:'saveNew']" >
				<fieldset class="form">
					<g:render template="/processo/pedidoCertidao/form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="Criar" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
