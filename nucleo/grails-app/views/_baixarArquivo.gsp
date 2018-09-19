<%@ page import="org.apoiasuas.AncestralController" %>

%{--Javascript que baixa automaticamente o arquivo de formulario guardado na sessao--}%
<g:if test="${AncestralController.getReportsParaBaixar(session)}">
    <g:link elementId="reportParaBaixar" controller="ancestral" action="baixarArquivo" hidden="" download=""></g:link>
    <script>
        //executado automaticamente apenas quando o HTML inteiro é recarregado. Em caso de atualizações pontuais via ajax, incluir a linha
        //abaixo no evento onComplete() das funcções grails/ajax
        document.getElementById('reportParaBaixar').click();
    </script>
</g:if>
