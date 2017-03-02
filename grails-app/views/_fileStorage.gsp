%{--
Template para renderizar um controle de escolha de arquivo para upload

Espera um parametro (via modelo) de nome "fileName" contendo o nome do arquivo atualmente presente no BD ou nulo se nenhum

Descrição: Renderiza botões de inclusao e remocao, e passa o arquivo para upload no parametro de request FileStorageDTO.INPUT_FILE
Também passa um parametro de request FileStorageDTO.FILE_ACTION apontando se houve a) a anulacao do campo, b) a manutencao do valor anterior
ou c) a inclusao de um novo arquivo. No caso b), ao gravar a instancia no banco, deve-se ignorar o valor passado em FileStorageDTO.INPUT_FILE
e manter o arquivo intacto (pois ele nao vai ser reenviado no request como se poderia supor).
--}%
<%@ page import="org.apoiasuas.fileStorage.FileStorageDTO" %>

<input type="button" id="adicionarArquivo" name="adicionarArquivo" class="speed-button-exportar" onclick="adicionarArquivoClick();" title="escolher um arquivo em seu computador"/>
&nbsp;<input type="button" id="limparArquivo" name="limparArquivo" class="speed-button-adicionar" onclick="limparArquivoClick(); return false;" title="limpar"/>
&nbsp;&nbsp;<span id="nomeArquivo">${fileName == null ? "(nenhum arquivo selecionado)" : fileName}</span>

%{--this is your file input tag, so i hide it! i used the onchange event to update the choosen file name-->--}%
<div style='height: 0px;width: 0px; overflow:hidden;'>
    <input name="${FileStorageDTO.INPUT_FILE}" id="${FileStorageDTO.INPUT_FILE}" type="file" onchange="mudarArquivo(this)"/>
    <g:hiddenField name="${FileStorageDTO.FILE_ACTION}" value="${FileStorageDTO.FileActions.MANTER_ATUAL}"/>
</div>

%{--Ao carregar a página, verificar se ja existe um arquivo selecionado --}%
<g:if test="${fileName == null}">
    <g:javascript>
        //Inicialização da página
        jQuery(document).ready(function () {
            limparArquivoClick();
        });
    </g:javascript>
</g:if>

<g:javascript>

        function adicionarArquivoClick(){
            //Simula o clique no file input chamando o evento mudarArquivo em seguida (se algum arquivo for de fato escolhido)
            document.getElementById("${FileStorageDTO.INPUT_FILE}").click();
        }

        function limparArquivoClick(){
            document.getElementById("${FileStorageDTO.INPUT_FILE}").value = "";
            mudarArquivo(document.getElementById("${FileStorageDTO.INPUT_FILE}"));
        }

        function mudarArquivo(input){
            console.log("input.files.length "+input.files.length);
            if (input.files.length == 0) { //nenhum arquivo selecionado, limpar
                console.log("arquivo nulo");
                document.getElementById("nomeArquivo").innerHTML = "(nenhum arquivo selecionado)";
                document.getElementById("${FileStorageDTO.FILE_ACTION}").value = "${FileStorageDTO.FileActions.ANULAR}";
                document.getElementById("limparArquivo").style.visibility = 'hidden';
            } else { //arquivo selecionado. assumir o primeiro e ignorar os demais, se for multipla selecao
                var fileSize = input.files[0].size;
                const MAX_FILE_SIZE = ${FileStorageDTO.MAX_FILE_SIZE}
    if (fileSize > MAX_FILE_SIZE) { //testar tamanho maximo do arquivo
        alert("O tamanho do arquivo escolhido ("+fileSizeToString(fileSize)+") é maior do que o permitido ("+fileSizeToString(MAX_FILE_SIZE)+")")
        limparArquivoClick();
        return;
    }
    var selectedFileName = input.files[0].name; //busca o nome do primeiro arquivo (ignora os demais, se for multipla selecao)
    console.log("arquivo escolhido: "+selectedFileName);
    document.getElementById("nomeArquivo").innerHTML = selectedFileName;
    document.getElementById("${FileStorageDTO.FILE_ACTION}").value = "${FileStorageDTO.FileActions.ATUALIZAR}";
                document.getElementById("limparArquivo").style.visibility = 'visible';
            }
        }

</g:javascript>
