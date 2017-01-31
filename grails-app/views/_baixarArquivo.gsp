<%@ page import="org.apoiasuas.AncestralController" %>

%{--Javascript que baixa automaticamente o arquivo de formulario guardado na sessao--}%
<g:if test="${AncestralController.getFormularioParaBaixar(session)}">
    <g:link elementId="formularioParaBaixar" controller="emissaoFormulario" action="baixarArquivo" hidden="" download=""></g:link>
    <g:javascript>
//            document.body.onload =function(){
                document.getElementById('formularioParaBaixar').click();
//            };
    </g:javascript>
</g:if>
