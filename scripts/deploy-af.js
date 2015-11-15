/**
 * Script para deploy de aplicacoes no servidor AppFog
 */

//configuracoes gerais ==>>
var TEMP_DIR="C:\\temp\\deploy-af"
var GRAILS_PATH="C:\\java\\grails-2.4.3\\bin";
var AF_PATH="C:\\Ruby21\\bin\\af.bat";
//<<== fim das configuracoes

WScript.Echo("Iniciando");

var fso = new ActiveXObject("Scripting.filesystemobject");
var shell = new ActiveXObject( "WScript.Shell" );
var tagOrigem;
var aplicacaoAppfog;
var dataSource;
var ambienteGrails
var currentDir=shell.CurrentDirectory
var pulaCompilacao=false

//Obtendo argumentos passados por parametro
if (WScript.Arguments.length  < 2) {
    WScript.Echo("Sintaxe: cscript deploy-af.js tag aplicacao [pulaCompilacao]");
    WScript.Echo("tag\t tag ou branch do controle de versao (current para o codigo corrente)");
    WScript.Echo("destino\t aplicacao AppFog");
    WScript.Echo("Branches e tags disponiveis:");
    WScript.Echo(shell.exec("git branch").stdOut.ReadAll())
    WScript.Echo(shell.exec("git tag").stdOut.ReadAll())
    exit("");
} else {
    tagOrigem = WScript.Arguments.Item(0);
    aplicacaoAppfog = WScript.Arguments.Item(1);
    for (i = 0; i < WScript.Arguments.length; i++) {
        if (WScript.Arguments.Item(i) == "pulaCompilacao")
            pulaCompilacao = true
    }
}

//Configurações a serem passadas para o ambiente
var permSize;
var heapSize;
var timeZone;
if (aplicacaoAppfog.indexOf("prod") === 0) {
    permSize="200m"
    heapSize="750m"
    timeZone="America/Sao_Paulo"
    dataSource="appfog_postgres_prod"
    ambienteGrails="prod"
} else if (aplicacaoAppfog.indexOf("valid") === 0) {
    permSize="200m"
    heapSize="750m"
    timeZone="America/Sao_Paulo"
    dataSource="appfog_postgres_valid"
    ambienteGrails="dev"
} else
    exit("Opcoes validas para o destino do deploy: prod* ou valid*");
var javaOpts="-XX:MaxPermSize="+permSize+" -XX:PermSize="+permSize+" -Xms"+heapSize+" -Xmx"+heapSize+" -Duser.timezone="+timeZone+" -Dorg.apoiasuas.datasource="+dataSource+" -Dorg.apoiasuas.sabotagem=false"
WScript.Echo("JAVA_OPTS: "+javaOpts);

//montando/compilando aplicacao
if (! pulaCompilacao) {
    if (tagOrigem == "current") {
        var caminhoDeployTemporario = fso.GetFolder("..\\target\\work\\stage");
        WScript.Echo("caminhoDeployTemporario: " + caminhoDeployTemporario);
        fso.DeleteFile(caminhoDeployTemporario + "\\*", true);
        fso.DeleteFolder(caminhoDeployTemporario + "\\*", true);
        cd(shell.CurrentDirectory + "\\..")
    } else { //tag especifica
        if (fso.FolderExists(TEMP_DIR)) {
            WScript.Echo("caminhoDeployTemporario: " + TEMP_DIR);
            var deletefolder = fso.GetFolder(TEMP_DIR)
            deletefolder.Attributes = 0
            fso.DeleteFile(TEMP_DIR + "\\*", true);
            fso.DeleteFolder(TEMP_DIR + "\\*", true);
        } else
            fso.CreateFolder(TEMP_DIR);
        executa("git --work-tree=" + TEMP_DIR + " checkout " + tagOrigem + " -- .");
        cd(TEMP_DIR);
        fso.DeleteFile(".\\grails-app\\views\\inicio\\_versao.gsp");
        var arquivoVersao = fso.createTextFile(".\\grails-app\\views\\inicio\\_versao.gsp", true)
        arquivoVersao.write(tagOrigem);
        arquivoVersao.close();
    }
    if (!executa(GRAILS_PATH + "\\grails.bat " + ambienteGrails + " war --stacktrace -Dorg.apoiasuas.datasource=" + dataSource))
        exit("compilacao mal sucedida");
} else {
    if (tagOrigem == "current")
        cd(shell.CurrentDirectory + "\\..")
    else
        cd(TEMP_DIR);
    WScript.Echo("pulando compilacao");
}

//Subindo aplicacao para o servidor AppFog
cd(shell.CurrentDirectory + "\\target\\work\\stage");
if (! executaAf("stop "+aplicacaoAppfog))
    exit("erro em af stop")
if (! executaAf("env-del "+aplicacaoAppfog+" JAVA_OPTS"))
    exit("erro em af env-del")
if (! executaAf(' env-add '+aplicacaoAppfog+' JAVA_OPTS="'+javaOpts+'"'))
    exit("erro em af env-add")
if (! executaAf("update "+aplicacaoAppfog+" --no-start"))
    exit("erro em af update")
if (! executaAf("start "+aplicacaoAppfog))
    exit("erro em af start")
if (! executaAf("stats "+aplicacaoAppfog))
    exit("erro em af stats")

WScript.Echo("Deploy concluido");
WScript.quit(0);

function cd(dir) {
    WScript.Echo("Mudando para o diretorio "+dir);
    shell.CurrentDirectory = dir
}

function executaAf(parametros) {
    return executa(AF_PATH + " " + parametros);
    //return executa('"C:\\Ruby21\\bin\\ruby.exe" "C:/Ruby21/bin/af" '+parametros);
}

function executa(comando) {
    WScript.Echo(comando);
    //Refer�ncia: https://msdn.microsoft.com/en-us/library/ateytk4a(v=vs.84).aspx
    var resultShell = shell.exec(comando);
    //WScript.Echo(resultShell.stdOut.ReadAll())

    var oStdOut = resultShell.StdOut

    while (! oStdOut.AtEndOfStream)
        WScript.Echo(oStdOut.ReadLine());

    return resultShell.ExitCode == 0 //sucesso
}

function exit(mensagem) {
    WScript.Echo(mensagem);
    WScript.quit(1);
}