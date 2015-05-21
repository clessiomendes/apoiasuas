<%@ page import="org.apoiasuas.formulario.EmissaoFormularioController" %>

<g:select name="${campoFormulario.caminhoCampo+'_select'}"
          from="${campoFormulario.formulario.cidadao?.familia?.telefones}"
          keys="${campoFormulario.formulario.cidadao?.familia?.telefones*.id}"
          onchange="document.getElementById('${campoFormulario.caminhoCampo}').value = (this.value == -1) ? '' : this.options[this.selectedIndex].text;"
          noSelection="['-1': 'novo']"/>
%{--onchange="(function(select) { alert(select.options[select.selectedIndex].text) })(this);"--}%
%{--
onchange="(function(select) {
          document.getElementById('${campoFormulario.caminhoCampo}').value = (select.value == -1) ? '' : select.options[select.selectedIndex].text
                })(this);"
--}%

%{--onchange="function { document.getElementById(${campoFormulario.caminhoCampo}).focus(); } ;"--}%

%{--TODO: esconder/exibir campos de novo telefone dependendo do select telefoneSelecionado--}%
%{--
<g:textField name="${EmissaoFormularioController.CAMPO_DDD_TELEFONE}" size="2"
             value="${request[EmissaoFormularioController.CAMPO_DDD_TELEFONE]}"/>
--}%
<g:textField name="${campoFormulario.caminhoCampo}"
             id="${campoFormulario.caminhoCampo}"
             size="${campoFormulario.tamanho}"
             value="${campoFormulario.valorArmazenado}"/>
