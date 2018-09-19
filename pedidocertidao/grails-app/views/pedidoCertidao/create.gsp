<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Pedido de Certidão - acolhida de demanda</title>
    	<asset:stylesheet src="pedidocertidao/pedido-certidao.less"/>
    	<asset:javascript src="pedidocertidao/pedido-certidao.js"/>
        <asset:stylesheet src="sessao.less"/>
        <asset:javascript src="sessao.js"/>
	</head>
	<body>
		<h1>Pedido de Certidão - acolhida de demanda</h1>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="search" action="list">Procurar</g:link></li>
			</ul>
		</div>
		<div class="content scaffold-create" role="main">
			<fieldset class="form">
				<g:form>
					<g:render template="form"/>

					<g:actionSubmit id="btnGravar" type="submit" class="gravar-pedido" style="margin: 5px 5px 5px 0"
						   title="Gravar alterações" value="Gravar Alterações" action="save"
						   onclick="this.form.action='${createLink(action:'save')}'; "/>
					<g:actionSubmit id="btnImprimirDeclaracaoPobreza" type="button" class="imprimir-pedido" style="margin: 5px 5px 5px 0"
						   title="Imprimir declaração de pobreza" value="Declaração de Pobreza" action="saveAndDownloadDeclaracao"
						   onclick="this.form.action='${createLink(action:'saveAndDownloadDeclaracao')}'; "/>
					<g:actionSubmit id="btnImprimirPedido" type="button" class="imprimir-pedido" style="margin: 5px 5px 5px 0"
						   title="Imprimir pedido para envio ao cartório" value="Pedido" action="saveAndDownloadPedido"
						   onclick="this.form.action='${createLink(action:'saveAndDownloadPedido')}';"/>
				</g:form>
			</fieldset>
		</div>
	</body>
</html>
