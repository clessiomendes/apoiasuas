/** Script para enviar automaticamente um arquivo em Excel (formato .xls) para ser importado pelo servidor.
  * Deve ser executado a partir da linha de comando: cscript exportaBancoDados.js
  * Utiliza arquivos intermediarios para 1) verificar se houve alteracao no arquivo desde a ultima exportacao
  * (e ignora a exportacao arquivo caso nao tenha havido) e 2) Converter do formato .xls para .xlsx
  * E necessario que haja uma versao 2007 ou maior do Excel instalado localmente. Tambem faz uso do aplicativo
  * curl, que tem diferentes versoes dependendo da versao do windows (XP, 7, 32bits, 64bits). Podem ser baixados
  * de http://curl.haxx.se/download.html.
  */

WScript.Echo("Iniciando");

var fso = new ActiveXObject("Scripting.filesystemobject");
var shell = new ActiveXObject( "WScript.Shell" );
//var BASE_DIR = "Y:\\Consolidado Eletrônico\\Planilhas Monitoramento e Informação 2016 - CRAS Havaí Ventosa"
//var BASE_DIR = "g:\\workspaces\\CRAS"
//var args:String[] = System.Environment.GetCommandLineArgs();
//var BASE_DIR = WScript.Arguments(0);
var CAMINHO_ARQUIVO_ORIGINAL = "Y:\\Consolidado Eletrônico\\Planilhas Monitoramento e Informação 2016 - CRAS Havaí Ventosa\\Cadastro de Famílias 2.0 - 2016 CRAS.xls"
var NOME_ARQUIVO_INTERMEDIARIO = "intermediario.xls";
//var COMANDO_CURL = '"Y:\\BANCO DE DADOS GPSOB\\apoiasuas\\curl.exe" -k http://apoiacras.cleverapps.io/importacaoFamilias/restUpload -F "qqfile=@' + caminhoArquivoConvertido+'"';


/* TESTE:  arquivo presente?
var TESTE = ".\\teste.xlsx"
WScript.Echo(TESTE);
if (fso.FileExists(TESTE)) {
	WScript.echo("Arquivo encontrado: " + fso.GetFile(TESTE).Path);
} else {
	WScript.echo("Arquivo nao encontrado: " + TESTE);
}	
*/

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
WScript.echo("FIM");

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
/*
function curl() {
	var comando = COMANDO_CURL;
	WScript.Echo(comando);
	//Referência: https://msdn.microsoft.com/en-us/library/ateytk4a(v=vs.84).aspx
	var resultShell = shell.exec(comando);
	WScript.Echo(resultShell.stdOut.ReadAll())

	while (resultShell.Status == 0) {
		WScript.Sleep(3000);
		WScript.StdErr.Write(resultShell.stdErr.ReadAll());
		WScript.StdOut.Write(resultShell.stdOut.ReadAll());
		WScript.StdOut.Write(".");
	}

	if (resultShell.ExitCode != 0) {
		fso.DeleteFile(NOME_ARQUIVO_INTERMEDIARIO);
		WScript.Echo("Erro enviando arquivo para o servidor. Codigo "+resultShell.ExitCode);
	} else {
//		fso.DeleteFile(caminhoArquivoConvertido);
		WScript.Echo("Arquivo enviado com sucesso para o servidor.");
	}
}
*/