<%@ page import="org.apoiasuas.formulario.EmissaoFormularioController" %>

%{--Javascript que baixa automaticamente o arquivo de formulario guardado na sessao--}%
<g:if test="${EmissaoFormularioController.getFormularioParaBaixar(session)}">
    <g:link elementId="formularioParaBaixar" controller="emissaoFormulario" action="baixarArquivo" hidden="" download=""></g:link>
    <script>
//            document.body.onload =function(){
                document.getElementById('formularioParaBaixar').click();
//            };
    </script>
</g:if>
