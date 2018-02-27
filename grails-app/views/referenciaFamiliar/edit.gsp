<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Troca da referência familiar</title>
		<asset:stylesheet src="pure-css/tables.css"/>
	</head>
	<body>

	<g:javascript>
    /**
     * Valida o preenchimento de todos os campos e cancela a submissão
     */
    function validacoes() {
    	var validado = true
		if (! $("#novaReferencia").val()) {
			alert('${message(code: "erro.referencia.familiar")}');
			validado = false;
		}
		$(".parentesco-membro").each(function(i){
			if (! $(this).val()) {
				alert('${message(code: "erro.parentescos.obrigatorios")}');
				validado = false;
				return false; //interrompe a iteração do .each()
			}
		});
		return validado;
    }
	</g:javascript>

		<div id="trocar-referencia" class="content scaffold-edit" role="main">
			<h1>Mudança de referência familiar e parentescos</h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${familiaInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${familiaInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>

				<table id="composicao-atual" class="pure-table pure-table-striped pure-table-bordered" style="max-width: 600px; margin: 10px auto;">
					<tr>
						<th colspan="3" style="text-align: center;">Configuração Familiar Atual</th>
					</tr>
					<g:each in="${familiaInstance.getMembrosOrdemPadrao(true)}" var="m">
						<tr>
							%{--<span class="property-value" aria-labelledby="membros-label" style="display: block; margin: 5px">--}%
							<td>${m.referencia ? raw("<b>Referência</b>") : m.parentescoReferencia }</td>
							<td>${m.nomeCompleto }</td>
							<td style="width: 4em">${m.idade ? m.idade + " anos" : ""}</td>
							%{--</span>--}%
						</tr>
					</g:each>
				</table>

			<g:form controller="referenciaFamiliar" action="edit" id="${familiaInstance.id}">
				<g:hiddenField name="version" value="${familiaInstance?.version}" />
				<table id="nova_composicao" class="pure-table pure-table-striped pure-table-bordered" style="max-width: 600px; margin: 10px auto;">
					<tr>
%{--
						<th>Parentesco</th>
						<th>Nome</th>
						<th>Idade</th>
--}%
						<th colspan="3" style="text-align: center;">Nova Configuração Familiar</th>
					</tr>
					<tr>
						%{--<span class="property-value" aria-labelledby="membros-label" style="display: block; margin: 5px">--}%
						<td><b>Referência</b></td>
						<td>
							<g:select style="max-width:200px;" id="novaReferencia"
									  optionKey='id' optionValue="nomeCompleto" name="novaReferencia"
									  from="${familiaInstance.getMembrosOrdemPadrao(true)}"
									  value="${novaReferencia?.id}" noSelection="['': '']" onchange="submit();"/>
						</td>
						<td>${novaReferencia?.idade ? novaReferencia?.idade + " anos" : ""}</td>
						%{--</span>--}%
					</tr>
					<g:each in="${demaisMembros}" var="m">
						<tr>
							%{--<span class="property-value" aria-labelledby="membros-label" style="display: block; margin: 5px">--}%
							<g:hiddenField name="idMembro" value="${m.id}"/>
							<td style="white-space: nowrap">
								<g:textField name="parentescoMembro" class="parentesco-membro" size="7" title="Grau de parentesco deste membro em relação à referência selecionada"/>
								<g:helpTooltip>Grau de parentesco deste membro (${m.nomeCompleto}) em relação à referência (${novaReferencia?.nomeCompleto})</g:helpTooltip>
							</td>
							<td>${m.nomeCompleto }</td>
							<td style="width: 4em">${m.idade ? m.idade + " anos" : ""}</td>
							%{--</span>--}%
						</tr>
					</g:each>
				</table>
%{--
				<fieldset class="form">

				</fieldset>
--}%

				<fieldset class="buttons">
					<g:actionSubmit action="save" id="${familiaInstance.id}" class="save" value="Confirmar" onclick="return validacoes();"/>
					<g:actionSubmit action="cancel" id="${familiaInstance.id}" class="cancel" value="Cancelar"/>
				</fieldset>
			</g:form>
		</div>

	</body>
</html>
