<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Pedido de Certidão - acompanhamento</title>
    	<asset:stylesheet src="pedidocertidao/pedido-certidao.less"/>
    	<asset:javascript src="pedidocertidao/pedido-certidao.js"/>
        <asset:stylesheet src="sessao.less"/>
        <asset:javascript src="sessao.js"/>
	</head>

	<body>
		<h1>Pedido de Certidão - acompanhamento</h1>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="search" action="list">Procurar</g:link></li>
				<li><g:link class="create" action="create">Nova Demanda</g:link></li>
			</ul>
		</div>
		<div class="content scaffold-show" role="main">
            <g:tabs id="tabs" style="margin: 5px;">
                <g:tab id="tabHistorico" titulo="histórico" template="historico"/>
                <g:tab id="tabPedido" titulo="pedido">
					<g:form>

						<div id="divPedido">
							<tmpl:form/>
						</div>

						<g:submitToRemote action="save" class="gravar-pedido" update="divPedido" value="Gravar alterações" style="margin: 5px 5px 5px 0"
										title="Gravar Alterações" />
						<g:submitToRemote class="imprimir-pedido" value="Declaração de Pobreza" action="saveAndDownloadDeclaracao" style="margin: 5px 5px 5px 0"
										title="Imprimir declaração de pobreza" update="divPedido" onComplete="downloadCertidaoOuPedido();" />
						<g:submitToRemote class="imprimir-pedido" value="Pedido" action="saveAndDownloadPedido" style="margin: 5px 5px 5px 0"
										title="Imprimir pedido para envio ao cartório" update="divPedido" />
					</g:form>
				</g:tab>
            </g:tabs>
		</div>
	</body>
</html>
