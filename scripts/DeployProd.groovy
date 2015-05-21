includeTargets << grailsScript("_GrailsClean")

target(deployProd: "Deploy em produção") {
    grailsConsole.updateStatus "Iniciando Deploy"
    grailsConsole.updateStatus "Opções "+org.apoiasuas.DeployOptionsProd.getEnvSetting()
}

setDefaultTarget(deployProd)

def deployAppFog() {
    def conf = grailsSettings.config

//        println warName
    grailsConsole.updateStatus "Atualizando aplicacao appfog: nome à partir de "

    ant.exec(outputproperty: "cmdOut",
            errorproperty: "cmdErr",
            resultproperty: "cmdExit",
            failonerror: "true",
            executable: "cmd") {
        arg(value: "/c")
        arg(value: "af")
        arg(value: "update")
        arg(value: "teste6")
        arg(value: "--path")
        arg(value: "C:/workspaces/appfog/apoiasuas")
    }
    if (ant.project.properties.cmdErr)
        println "Erros:\n${ant.project.properties.cmdErr}"
    if (ant.project.properties.cmdOut)
        println "Resultado:\n${ant.project.properties.cmdOut}"

    grailsConsole.updateStatus "Fim do deploy appfog"

}
