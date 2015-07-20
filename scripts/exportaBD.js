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

//var CAMINHO_BASE = "Y:\\BANCO DE DADOS GPSOB\\"
//var CAMINHO_CURL = CAMINHO_BASE + "apoiasuas\\curl.exe";

var CAMINHO_BASE = "c:\\temp\\"
var CAMINHO_CURL = CAMINHO_BASE + "apoiasuas\\curl.exe";

//var CAMINHO_ARQUIVO_ORIGINAL = "c:\\temp\\cad-teste.xls";
var CAMINHO_ARQUIVO_ORIGINAL = CAMINHO_BASE + "Cadastro de Fam�lias CRAS Hava� Ventosa.xls";
var CAMINHO_ARQUIVO_INTERMEDIARIO = CAMINHO_BASE + "Y:\\BANCO DE DADOS GPSOB\\apoiasuas\\ultimo_enviado.xls";
var CAMINHO_ARQUIVO_CONVERTIDO = CAMINHO_ARQUIVO_INTERMEDIARIO.replace(".xls", ".xlsx");
var COMANDO_CURL = '"'+CAMINHO_CURL+'" -k http://apoiasuas-valid.aws.af.cm/importacaoFamilias/restUpload -F "qqfile=@' + CAMINHO_ARQUIVO_CONVERTIDO+'"';
//var COMANDO_CURL = '"Y:\\BANCO DE DADOS GPSOB\\apoiasuas\\curl.exe" -k http://apoiasuas.aws.af.cm/importacaoFamilias/restUpload -F "qqfile=@' + CAMINHO_ARQUIVO_CONVERTIDO+'"';

var arquivoOriginal = fso.GetFile(CAMINHO_ARQUIVO_ORIGINAL);
if (fso.FileExists(CAMINHO_ARQUIVO_INTERMEDIARIO)) {
	var arquivoEnviar = fso.GetFile(CAMINHO_ARQUIVO_INTERMEDIARIO);
	WScript.echo("Data do arquivo original: " + arquivoOriginal.DateLastModified);
	WScript.echo("Data do ultimo arquivo enviado: " + arquivoEnviar.DateLastModified);
	if (String(arquivoEnviar.dateLastModified) != String(arquivoOriginal.dateLastModified))
		envia()
	else
		WScript.Echo("Nenhuma atualizacao necessaria");

} else {
	WScript.echo("Primeira vez");
	envia();
}
WScript.echo("FIM");

//       ==================              FIM DO FLUXO PRINCIPAL DO SCRIPT          ====================

//       ==================              Definicoes das funcoes          ====================

function envia() {
	
	WScript.Echo("Copiando de " + CAMINHO_ARQUIVO_ORIGINAL + " para " + CAMINHO_ARQUIVO_INTERMEDIARIO);
	fso.copyFile(CAMINHO_ARQUIVO_ORIGINAL, CAMINHO_ARQUIVO_INTERMEDIARIO, true);
//	var arquivoEnviar = fso.GetFile(CAMINHO_ARQUIVO_INTERMEDIARIO)
	var xlapp = new ActiveXObject("Excel.Application");
	try {
		WScript.Echo("Convertendo para "+CAMINHO_ARQUIVO_CONVERTIDO);
//   		var mybook = xlapp.Workbooks.Open(arquivoEnviar.Path)
		var mybook = xlapp.Workbooks.Open(CAMINHO_ARQUIVO_INTERMEDIARIO);
		mybook.Application.DisplayAlerts = false;
	
		//Mantem apenas a primeira aba da planilha
		while (mybook.workSheets.count > 1) 
			mybook.Sheets(2).Delete();
		
		//Referencia: https://msdn.microsoft.com/pt-br/library/office/ff841185.aspx
		mybook.saveAs(CAMINHO_ARQUIVO_CONVERTIDO, 51 /*.xlsx*/,  null, null, false, false, 
			2 /*XlSaveAsAccessMode.xlShared*/, 1 /*XlSaveConflictResolution.xlUserResolution*/, false, null, null, true);
		mybook.close();
		WScript.Echo("Convertido");
	} finally {
		xlapp.quit(1)
	}
    xlapp.quit(0)
	//curl();
}
/*
function curl() {
	var comando = COMANDO_CURL;
	WScript.Echo(comando);
	//Refer�ncia: https://msdn.microsoft.com/en-us/library/ateytk4a(v=vs.84).aspx
	var resultShell = shell.exec(comando);
	WScript.Echo(resultShell.stdOut.ReadAll())

	while (resultShell.Status == 0) {
		WScript.Sleep(3000);
		WScript.StdErr.Write(resultShell.stdErr.ReadAll());
		WScript.StdOut.Write(resultShell.stdOut.ReadAll());
		WScript.StdOut.Write(".");
	}

	if (resultShell.ExitCode != 0) {
		fso.DeleteFile(CAMINHO_ARQUIVO_INTERMEDIARIO);
		WScript.Echo("Erro enviando arquivo para o servidor. Codigo "+resultShell.ExitCode);
	} else {
//		fso.DeleteFile(CAMINHO_ARQUIVO_CONVERTIDO);
		WScript.Echo("Arquivo enviado com sucesso para o servidor.");
	}
}
*/