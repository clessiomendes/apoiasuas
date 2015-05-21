import org.apache.tools.ant.Task
import org.apache.tools.ant.taskdefs.ExecTask
import org.apoiasuas.util.AmbienteExecucao

includeTargets << grailsScript("_GrailsClean")
includeTargets << grailsScript("_GrailsPackage")
includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsSettings")
includeTargets << grailsScript("_GrailsWar")

abstract class DeployOptionsBaseClass {
    static String permSize
    static String heapSize
    static String timeZone
    static Boolean simulaErros
    static Boolean recriarBD
    static String dataSource

    AppFog appfog = new AppFog()
    public static class AppFog {
        static String appname
        static String login = "clessiomendes@yahoo.com"
        static String senha
    }

    static String envKey = "JAVA_OPTS"

    static String getEnvValue() {
        "-XX:MaxPermSize=${permSize} " +
                "-XX:PermSize=${permSize} -Xms${heapSize} -Xmx${heapSize} " +
                "-Duser.timezone=${timeZone} " +
//              "-Dorg.apache.catalina.session.StandardSession.ACTIVITY_CHECK=true " +
//              "-Dorg.apache.catalina.session.StandardSession.LAST_ACCESS_AT_START=false " +
                "-Dorg.apoiasuas.datasource=${dataSource} " +
                "-Dorg.apoiasuas.sabotagem=${simulaErros} "
//              "-Dorg.apoiasuas.recriarBD=false "
    }

    static int antExecutions = 0
}

class DeployOptionsValidacao extends DeployOptionsBaseClass {
    static {
        permSize = "200m"
        heapSize = "750m"
        timeZone = "America/Sao_Paulo"
        simulaErros = false
        recriarBD = false
        AppFog.appname = "teste6"
        dataSource = "appfog_postgres_valid"
    }
}

//  *******************************************      T A R G E T S      ***********************************************

target(deployValidacao: "Deploy para ambiente de validação") {
    depends(war)
    def deployOptions = new DeployOptionsValidacao()
    executa(deployOptions)
}

setDefaultTarget(deployValidacao)

void executa(DeployOptionsBaseClass deployOptions) {
    log "Iniciando Deploy"
    log antExec("af stop '${deployOptions.appfog.appname}'")
    log antExec("af env-del '${deployOptions.appfog.appname}' '${deployOptions.envKey}' ")
    log antExec("af env-add '${deployOptions.appfog.appname}' '${deployOptions.envKey}'='${deployOptions.envValue}' ")
    log antExec("af update '${deployOptions.appfog.appname}' --path '${grailsSettings.config.grails.project.war.exploded.dir}' --no-start")
    log antExec("af start '${deployOptions.appfog.appname}'")
    log antExec("af stats '${deployOptions.appfog.appname}'")
    log "Deploy Terminado"
}

String antExec(String argumentos) {
    int count = DeployOptionsBaseClass.antExecutions++
    log argumentos
    ExecTask task = new ExecTask()
    task.project = ant.project
    task.outputproperty = "saida"+count
    task.executable = "cmd"
    task.createArg().value = "/c"
    argumentos.split(" ").each {
        task.createArg().value = it
    }
    task.execute()
    return task.project.properties.get("saida"+count)
}

void log(String s) {
    grailsConsole.updateStatus s
}