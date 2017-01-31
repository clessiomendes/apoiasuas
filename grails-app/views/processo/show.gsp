
<%@ page import="org.apoiasuas.processo.TarefaDTO; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.seguranca.UsuarioSistema" %>
<!DOCTYPE html>
<html>
	<head>
        <meta name="layout" content="main">
        <title>Ver processo</title>
	</head>
	<body>

%{--
    Alguns casos de uso de emissao de formulario terminam redirecionando para a exibicao de um processo criado automaticamente.
    Se for este o caso, o template incluido abaixo permite baixar o arquivo que foi criado na requisicao anterior.
--}%
    <g:render template="/baixarArquivo"/>

    <a href="#show-processo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="list" action="preList">Listar</g:link></li>
			</ul>
		</div>
		<div id="show-processo" class="content scaffold-show" role="main">
			<h1>${processo.descricao} de <g:formatDate date="${processo.inicio}"/></h1>
			<g:if test="${flash.message}">
			    <div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list processo">

                <g:if test="${processo?.fim}">
                    <li class="fieldcontain">
                        <span id="inicio-label" class="property-label">Concluido em</span>
                        <span class="property-value" aria-labelledby="inicio-label"><g:formatDate date="${processo.fim}"/></span>
                    </li>
                </g:if>

            %{-- renderiza um template customizado para o processo especifico sendo exibido --}%
                <g:if test="${templateProcessoEspecifico}">
                    <g:render template="${templateProcessoEspecifico}" model="${['processo', processo]}"/>
                </g:if>

                <g:if test="${processo.tarefas}">
                    <fieldset id="fieldsetTarefas" class="embedded"><legend>Histórico</legend>
                        <g:each in="${processo.tarefas}" var="tarefa">
                            <g:form action="reabreTarefa" id="${tarefa.id}">
                            <li class="fieldcontain">
                                <span class="property-label">${tarefa.descricao}</span>
                                <span class="property-value" aria-labelledby="membros-label">
                                    <g:if test="${tarefa.situacao == TarefaDTO.SituacaoTarefa.CONCLUIDA}">
                                        Concluída por ${tarefa.responsavel?.username} em <g:formatDate date="${tarefa.fim}"/>
                                        <g:submitButton showif="${processo.fim == null}" name="reabreTarefa" class="save" value="voltar" />
                                    </g:if>
                                    <g:if test="${tarefa.situacao == TarefaDTO.SituacaoTarefa.PENDENTE}">
                                        Aguardando por ${tarefa.responsavel?.username} desde <g:formatDate date="${tarefa.inicio}"/>
                                    </g:if>
                                    <g:if test="${tarefa.situacao == TarefaDTO.SituacaoTarefa.CANCELADA}">
                                        Cancelada com ${tarefa.responsavel?.username} em <g:formatDate date="${tarefa.fim}"/>
                                    </g:if>
                                </span>
                            </li>
                            </g:form>
                        </g:each>
                    </fieldset>
                </g:if>

                <g:if test="${processo.tarefasPendentes}">
                    <fieldset id="fieldsetTarefas" class="embedded"><legend>Próximo passo</legend>
                        <g:each in="${processo.tarefasPendentes}" var="tarefa">
                            <g:form action="concluiTarefa" id="${tarefa.id}">

                                <g:if test="${! tarefa.ultimaPendente}">
                                <li class="fieldcontain">
                                    <span class="property-label">Próximo responsável</span>
                                    <span class="property-value" aria-labelledby="membros-label">
                                        <g:select required="" name="proximoResponsavel" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}" keys="${ususariosDisponiveis.collect{it.id}}"/>
                                    </span>
                                </li>
                                </g:if>

                                <li class="fieldcontain">
                                    <span class="property-label"></span>
                                    <span class="property-value" aria-labelledby="membros-label">
                                        <g:submitButton name="${tarefa.proximosPassos[0]}" class="save" value="${tarefa.proximosPassos[0]}" />
                                        %{--Caso exista mais de uma proxima tarefa, exibir ao lado de cada botão a tarefa atual correspondente--}%
                                        ${processo.tarefasPendentes.size() > 1 ? "("+tarefa.descricao+")" : ""}
                                    </span>
                                </li>


                            </g:form>
                        </g:each>
                    </fieldset>
                </g:if>


            </ol>
			<g:form id="${processo.id}">
				<fieldset class="buttons">
					<g:actionSubmit class="delete" action="cancelaProcesso" value="Cancelar" onclick="return confirm('Confirma a exclusão deste processo inteiro?');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
