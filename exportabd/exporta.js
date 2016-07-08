/** Script para enviar automaticamente um arquivo em Excel (formato .xls) para ser importado pelo servidor.
  * Deve ser executado a partir da linha de comando: cscript exportaBancoDados.js
  * Utiliza arquivos intermediarios para 1) verificar se houve alteracao no arquivo desde a ultima exportacao
  * (e ignora a exportacao arquivo caso nao tenha havido) e 2) Converter do formato .xls para .xlsx
  * E necessario que haja uma versao 2007 ou maior do Excel instalado localmente. Tambem faz uso do aplicativo
  * curl, que tem diferentes versoes dependendo da versao do windows (XP, 7, 32bits, 64bits). Podem ser baixados
  * de http://curl.haxx.se/download.html.
  */

WScript.Echo("Iniciando javascript");

//WScript.Echo(getText("http://www.microsoft.com/default.htm"));

var fso = new ActiveXObject("Scripting.filesystemobject");
var shell = new ActiveXObject( "WScript.Shell" );

if (WScript.Arguments.count() != 1) {
    WScript.echo("parametros incorretos. forma correta: cscript exporta.js nome-arquivo-configuracao");
    WScript.Quit(404)
}
var ARQUIVO_CONFIGURACOES = WScript.Arguments(0);

//var BASE_DIR = "Y:\\Consolidado Eletrônico\\Planilhas Monitoramento e Informação 2016 - CRAS Havaí Ventosa"
//var BASE_DIR = "g:\\workspaces\\CRAS"
//var args:String[] = System.Environment.GetCommandLineArgs();
//var BASE_DIR = WScript.Arguments(0);

var CAMINHO_ARQUIVO_ORIGINAL = ""; // = "Y:\\Consolidado Eletrônico\\Planilhas Monitoramento e Informação 2016 - CRAS Havaí Ventosa\\Cadastro de Famílias 2.0 - 2016 CRAS.xls"
var NOME_ARQUIVO_INTERMEDIARIO = ""; // = "intermediario.xls";
//var COMANDO_CURL = '"Y:\\BANCO DE DADOS GPSOB\\apoiasuas\\curl.exe" -k http://apoiacras.cleverapps.io/importacaoFamilias/restUpload -F "qqfile=@' + caminhoArquivoConvertido+'"';

if (fso.FileExists(ARQUIVO_CONFIGURACOES)) {
    var regex = {
        section: /^\s*\[\s*([^\]]*)\s*\]\s*$/,
        param: /^\s*([\w\.\-\_]+)\s*=\s*(.*?)\s*$/,
        comment: /^\s*;.*$/
    };

    f = fso.OpenTextFile(ARQUIVO_CONFIGURACOES);
// Read from the file and display the results.
    while (!f.AtEndOfStream) {
        var linha = f.ReadLine();
        if (regex.comment.test(linha)) {
        } else if (regex.param.test(linha)) {
            var match = linha.match(regex.param);
            if (match[1] == "CAMINHO_ARQUIVO_ORIGINAL")
                CAMINHO_ARQUIVO_ORIGINAL = match[2].replace(/\\/g, "\\\\");
            if (match[1] == "NOME_ARQUIVO_INTERMEDIARIO")
                NOME_ARQUIVO_INTERMEDIARIO = match[2].replace(/\\/g, "\\\\");
        }
    }
    f.Close();
} else {
    WScript.echo("Arquivo de configurações não encontrado: " + ARQUIVO_CONFIGURACOES)
    WScript.Quit(404)
}

if (CAMINHO_ARQUIVO_ORIGINAL != "")
    WScript.echo("CAMINHO_ARQUIVO_ORIGINAL: " + CAMINHO_ARQUIVO_ORIGINAL)
else {
    WScript.echo("CAMINHO_ARQUIVO_ORIGINAL não encontrado em " + ARQUIVO_CONFIGURACOES)
    WScript.Quit(404)
}
if (CAMINHO_ARQUIVO_ORIGINAL != "")
    WScript.echo("NOME_ARQUIVO_INTERMEDIARIO: " + NOME_ARQUIVO_INTERMEDIARIO);
else {
    WScript.echo("NOME_ARQUIVO_INTERMEDIARIO não encontrado em " + ARQUIVO_CONFIGURACOES)
    WScript.Quit(404)
}

var arquivoOriginal = fso.GetFile(CAMINHO_ARQUIVO_ORIGINAL);
if (fso.FileExists(NOME_ARQUIVO_INTERMEDIARIO)) {
	var arquivoEnviar = fso.GetFile(NOME_ARQUIVO_INTERMEDIARIO);
	WScript.echo("Data do arquivo original: " + arquivoOriginal.DateLastModified);
	WScript.echo("Data do ultimo arquivo enviado: " + arquivoEnviar.DateLastModified);
	if (String(arquivoEnviar.dateLastModified) != String(arquivoOriginal.dateLastModified))
		converte()
	else
		WScript.Echo("Nenhuma atualizacao necessaria");
} else {
	WScript.echo("Primeira vez");
	converte();
}
WScript.echo("Fim do javascprit");

//       ==================              FIM DO FLUXO PRINCIPAL DO SCRIPT          ====================

//       ==================              Definicoes das funcoes          ====================

function converte() {
	
	WScript.Echo("Copiando de " + CAMINHO_ARQUIVO_ORIGINAL + " para " + NOME_ARQUIVO_INTERMEDIARIO);
	fso.copyFile(CAMINHO_ARQUIVO_ORIGINAL, NOME_ARQUIVO_INTERMEDIARIO, true);
	var caminhoArquivoConvertido = fso.GetFile(NOME_ARQUIVO_INTERMEDIARIO).Path.replace(".xls", ".xlsx");
	
	var xlapp = new ActiveXObject("Excel.Application");
	try {
		WScript.Echo("Convertendo para "+caminhoArquivoConvertido);
//   		var mybook = xlapp.Workbooks.Open(arquivoEnviar.Path)
		var mybook = xlapp.Workbooks.Open(fso.GetFile(NOME_ARQUIVO_INTERMEDIARIO).Path);
		mybook.Application.DisplayAlerts = false;
	
		//Mantem apenas a primeira aba da planilha
		while (mybook.workSheets.count > 1) 
			mybook.Sheets(2).Delete();
		
		//Referencia: https://msdn.microsoft.com/pt-br/library/office/ff841185.aspx
		mybook.saveAs(caminhoArquivoConvertido, 51 /*.xlsx*/,  null, null, false, false, 
			2 /*XlSaveAsAccessMode.xlShared*/, 1 /*XlSaveConflictResolution.xlUserResolution*/, false, null, null, true);
		mybook.close();
		WScript.Echo("Convertido");
	} finally {
		xlapp.quit()
	}

//	curl();
        WScript.Quit(500)

}

function parseINIString(data){
    var regex = {
        section: /^\s*\[\s*([^\]]*)\s*\]\s*$/,
        param: /^\s*([\w\.\-\_]+)\s*=\s*(.*?)\s*$/,
        comment: /^\s*;.*$/
    };
    var value = {};
    var lines = data.split(/\r\n|\r|\n/);
    var section = null;

    for(x=0;x<lines.length;x++)
    {

        if(regex.comment.test(lines[x])){
            return;
        }else if(regex.param.test(lines[x])){
            var match = lines[x].match(regex.param);
            if(section){
                value[section][match[1]] = match[2];
            }else{
                value[match[1]] = match[2];
            }
        }else if(regex.section.test(lines[x])){
            var match = lines[x].match(regex.section);
            value[match[1]] = {};
            section = match[1];
        }else if(line.length == 0 && section){
            section = null;
        };

    }

    return value;
}

function getText(strURL)
{
    var strResult;

    try
    {
        // Create the WinHTTPRequest ActiveX Object.
        var WinHttpReq = new ActiveXObject(
            "WinHttp.WinHttpRequest.5.1");

        //  Create an HTTP request.
        var temp = WinHttpReq.Open("GET", strURL, false);

        //  Send the HTTP request.
        WinHttpReq.Send();

        //  Retrieve the response text.
        strResult = WinHttpReq.ResponseText;
    }
    catch (objError)
    {
        strResult = objError + "\n"
        strResult += "WinHTTP returned error: " +
            (objError.number & 0xFFFF).toString() + "\n\n";
        strResult += objError.description;
    }

    //  Return the response text.
    return strResult;
}
