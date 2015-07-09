import org.apoiasuas.ProgramaService
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.codehaus.groovy.grails.commons.ApplicationAttributes

class BootStrap {

    def segurancaService
    def importarFamiliasService
    def formularioService
    def roleHierarchy
    def apoiaSuasService
    def programaService

    def init = { servletContext ->

        AmbienteExecucao.inicioAplicacao

        //Criando um novo metodo "update" em todos os objetos groovy da aplicacao
        Object.metaClass.update = {
            updateAttributesFromMap delegate, it
        }

        //Validando esquema de banco de dados
        String[] atualizacoesPendentes = apoiaSuasService.atualizacoesPendentes
        if (atualizacoesPendentes) {
            String erro = "Detectadas atualizacoes pendentes no banco de dados:"
            atualizacoesPendentes.each { erro += "\n"+it+";" }
            log.error(erro);
            throw new RuntimeException("Banco de dados fora de sincronia com a aplicação (ver mensagens anteriores). Startup interrompido.")
        }

        //sobrescrevendo a configuracao de seguranca (hierarquia de papeis)
        roleHierarchy.setHierarchy(DefinicaoPapeis.hierarquiaFormatada)

        UsuarioSistema.withTransaction { status ->
            try {
                UsuarioSistema admin = segurancaService.inicializaSeguranca()

                importarFamiliasService.inicializaDefinicoes(admin)
                programaService.inicializaProgramas(admin)

                try {
                    //Se ambiente de desenvolvimento, descarta eventuais alteracoes em formularios e reinicializa tudo
                    formularioService.inicializaFormularios(admin, AmbienteExecucao.desenvolvimento)

                } catch (Throwable t) {
                    if (AmbienteExecucao.desenvolvimento)
                        t.printStackTrace()
                    else
                        throw t
                }

            } catch (Throwable t) {
                status.setRollbackOnly()
                throw t;
            }
        }
    }

    def destroy = {
    }

    /**
     * Metodo a ser injetado em todos os objetos groovy da aplicacao para atualizar atributos quaisquer aa partir de um mapa:
     * Exemplo de uso:
     * meuObjeto.update([campo1: 'valor1', campo2: 'valor2']
     */
    static Object updateAttributesFromMap(Object instanciaAAtualizar, Map<String, Object> propriedadesASubstituir) {
        propriedadesASubstituir.each { key, value ->
            if (instanciaAAtualizar.hasProperty(key))
                instanciaAAtualizar."${key}" = value
        }
        return instanciaAAtualizar
    }

}