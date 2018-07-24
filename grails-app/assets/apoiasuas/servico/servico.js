var cropper;
var janelaModalImagem = new JanelaModalAjax();

function submitFormEncaminhamento(idFormulario, idServico) {
    $('#idFormulario').val(idFormulario);
    $('#idServico').val(idServico);
    $('#formPreencherFormulario').submit();
}

function imagemServico(fileInput) {
    console.log("fileInput.onChange");
    var canvas  = $("#canvas");
    var context = canvas.get(0).getContext("2d");
    if (cropper)
        cropper.destroy();
    if (fileInput.files && fileInput.files[0]) {
        if ( fileInput.files[0].type.match(/^image\//) ) {
            var reader = new FileReader();
            reader.onload = function(evt) {
                var img = new Image();
                img.onload = function() {
                     context.canvas.height = img.height;
                     context.canvas.width  = img.width;
                     context.drawImage(img, 0, 0);
                     cropper = new Cropper(context.canvas, {
                         aspectRatio: 1/1,
                         autoCropArea: 2, zoomable: false, rotatable: false
                     });

                     janelaModalImagem.abreJanela({titulo: "Imagem para o serviço", element: $("#divImagemServico")});

                    $('#formImagem').submit(function() {
                        // Get a string base 64 data url
                         var croppedImageDataURL = cropper.getCroppedCanvas({width: 160,height: 160}).toDataURL("image/png");
                         $('#urlImagem').val(croppedImageDataURL);
                         return true;
                     });
                };
                img.src = evt.target.result;
            };
            reader.readAsDataURL(fileInput.files[0]);
        } else {
            alert("Tipo de arquivo inválido! Escolha um arquivo de imagem.");
        }
    } else {
        alert('Nenhuma imagem selecionada.');
    }
};

function imagemSelecionar() {
    // Get a string base 64 data url
    var croppedImageDataURL = cropper.getCroppedCanvas({width: 160,height: 160}).toDataURL("image/png");
    $('#imgServico').attr('src', croppedImageDataURL);
    $('#urlImagem').val(croppedImageDataURL);
    $('#fileAction').val(FileActions.ATUALIZAR);
    janelaModalImagem.confirmada();
    //$result.append( $('<img>').attr('src', croppedImageDataURL) );
};

function imagemCancelar() {
    janelaModalImagem.cancelada();
};

$( document ).ready(function() {
    configuraUploadImagem();
});

function configuraUploadImagem() {

    //Define o menu ao clicar na imagem para troca-la
    //https://github.com/mar10/jquery-ui-contextmenu/wiki/ApiRef
    $(document).contextmenu({
        delegate: ".hasmenu",
        //autoFocus: true,
        autoTrigger: false,
        position: {my: "center", at: "center", collision: "fit"},
        preventContextMenuForPopup: true,
        preventSelect: true,
        taphold: true,
        menu: [{
            title: "carregar imagem",
            cmd: "upload",
            uiIcon: "ui-icon-folder-open"
        }, {
            title: "remover imagem",
            cmd: "clear",
            uiIcon: "ui-icon-trash"
        }],
        // Handle menu selection
        select: function (event, ui) {
            switch (ui.cmd) {
                case "upload":
                    $('#inputSelecionarArquivo').click();
                    break
                case "clear":
                    $('#imgServico').attr('src', $('#imgVazia').attr('src'));
                    $('#urlImagem').val('');
                    $('#fileAction').val(FileActions.ANULAR);
                    break
            }
        }
    });
};

/*
function removerAnexoClick(button) {
    var $linhaAnexo = $(button).closest('tr');
    var $hiddenRemover = $linhaAnexo.find('.removerAnexo').val(true);
    $linhaAnexo.hide(200);
}

function adicionarAnexo() {
    //$('#inputSelecionarAnexo').click();
    var $linhaClonada = $('#tabelaModelo tr').clone();
    $linhaClonada.find('.inputs-anexos').click();
    //adiciona na penultima linha da tabela (última reservada para o botão NOVO)
    $('#tableAnexos tr:last').prev().after($linhaClonada);
}

function anexoServico(fileInput) {
    if (fileInput.files && fileInput.files[0]) {
            var reader = new FileReader();
            reader.onload = function(evt) {
                console.debug(evt.target.result);
                //img.src = evt.target.result;
            };
    } else {
        alert('Nenhum arquivo selecionado.');
    }
};
*/

function adicionarLinhaEndereco() {
    var $linhaClonada = $('#modeloLinhaEnderecos').clone();
    $linhaClonada.removeAttr('id');
    $linhaClonada.removeClass('hidden');
    //adiciona na penultima linha da tabela (última reservada para o botão NOVO)
    $('#divEnderecos .linhaEnderecos:last').prev().after($linhaClonada);
    //$('#divEnderecos').append($linhaClonada);
}

function removerLinhaEndereco(button) {
    var $LinhaRemovida = $(button).closest('.linhaEnderecos').remove();
}

function atualizarUltimaVerificacao(data) {
    $('#txtUltimaVerificacao').val(data);
}