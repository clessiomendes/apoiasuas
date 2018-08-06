<script>
	$(document).ready(function() {
		//Sempre que essa janela de erro é exibida, significa que a tela não esta sincronizada com a situação atual do
		//banco de dados e, portanto, fazemos um refresh completo dos eventos exibidos.
		atualiza();
	});
</script>

<ul class="errors" role="alert">
	<li>Erro na operação!</li>
	<li>${detalhesErro}</li>
</ul>

<fieldset class="buttons">
    <a href="javascript:void(0)" class="close" onclick="janelaModal.cancelada();">Fechar</a>
</fieldset>
