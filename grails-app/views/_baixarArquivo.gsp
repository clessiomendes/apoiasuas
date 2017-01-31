<%@ page import="org.apoiasuas.AncestralController" %>

%{--Javascript que baixa automaticamente o arquivo de formulario guardado na sessao--}%
<g:if test="${AncestralController.getReportParaBaixar(session)}">
    <g:link elementId="reportParaBaixar" controller="ancestral" action="baixarArquivo" hidden="" download=""></g:link>
    <g:javascript>
//            document.body.onload =function(){
                document.getElementById('reportParaBaixar').click();
//            };
    </g:javascript>
</g:if>
