function removerTelefoneClick(button) {
    var $linhaTelefone = $(button).closest('tr');
    var $hiddenRemover = $linhaTelefone.find('.removerTelefone').val(true);
    $linhaTelefone.hide(200);
}

function adicionarTelefone() {
    var $linhaClonada = $('#tabelaModelo tr').clone();
    //adiciona na penultima linha da tabela (última reservada para o botão NOVO)
    $('#tableTelefones tr:last').prev().after($linhaClonada);
}

$(document).ready(function() {
//Inicialização dos eventos
    $(".obs").focus(function () {
        $(this).css("height", "54px");
    });

    $(".obs").blur(function () {
        $(this).css("height", "19px");
    });
});

