<%@ page import="org.apoiasuas.importacao.TentativaImportacao" %>



<div class="fieldcontain ${hasErrors(bean: tentativaImportacaoInstance, field: 'criador', 'error')} ">
	<label for="criador">
		<g:message code="tentativaImportacao.criador.label" default="Criador" />
		
	</label>
	<g:select id="criador" name="criador.id" from="${org.apoiasuas.seguranca.UsuarioSistema.list()}" optionKey="id" value="${tentativaImportacaoInstance?.criador?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: tentativaImportacaoInstance, field: 'informacoesDoProcessamento', 'error')} ">
	<label for="informacoesDoProcessamento">
		<g:message code="tentativaImportacao.informacoesDoProcessamento.label" default="Informacoes Do Processamento" />
		
	</label>
	<g:textArea name="informacoesDoProcessamento" cols="40" rows="5" maxlength="100000" value="${tentativaImportacaoInstance?.informacoesDoProcessamento}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: tentativaImportacaoInstance, field: 'linhasPreProcessadas', 'error')} ">
	<label for="linhasPreProcessadas">
		<g:message code="tentativaImportacao.linhasPreProcessadas.label" default="Linhas Pre Processadas" />
		
	</label>
	<g:field name="linhasPreProcessadas" type="number" value="${tentativaImportacaoInstance.linhasPreProcessadas}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: tentativaImportacaoInstance, field: 'linhasProcessadasConclusao', 'error')} ">
	<label for="linhasProcessadasConclusao">
		<g:message code="tentativaImportacao.linhasProcessadasConclusao.label" default="Linhas Processadas Conclusao" />
		
	</label>
	<g:field name="linhasProcessadasConclusao" type="number" value="${tentativaImportacaoInstance.linhasProcessadasConclusao}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: tentativaImportacaoInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="tentativaImportacao.status.label" default="Status" />
		
	</label>
	<g:select name="status" from="${org.apoiasuas.importacao.StatusImportacao?.values()}" keys="${org.apoiasuas.importacao.StatusImportacao.values()*.name()}" value="${tentativaImportacaoInstance?.status?.name()}"  noSelection="['': '']"/>

</div>

