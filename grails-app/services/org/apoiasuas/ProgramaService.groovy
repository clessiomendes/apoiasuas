package org.apoiasuas

import grails.transaction.Transactional
import org.apoiasuas.marcador.Programa
import org.apoiasuas.seguranca.UsuarioSistema

class ProgramaService {

    @Transactional
    /**
     * Cria programas pré-defindos, caso ainda não existam no banco de dados
     */
    public void inicializaProgramas(UsuarioSistema admin) {
        ProgramasPreDefinidos.values().each { enumPrograma ->
            try {
                Programa programa = getProgramaPreDefinido(enumPrograma)
                if (programa)
                    enumPrograma.instanciaPersistida = programa
                else //Criar novos quando necessario
                    enumPrograma.instanciaPersistida = new Programa(nome: enumPrograma.nome, sigla: enumPrograma.sigla, programaPreDefinido: enumPrograma).save()
            } catch (Throwable t) {
                throw new RuntimeException("Erro inicializando programa ${enumPrograma}. Abortando", t)
            }
        }

    }

    @Transactional(readOnly = true)
    public Programa getProgramaPreDefinido(ProgramasPreDefinidos enumPrograma) {
        return Programa.findByProgramaPreDefinido(enumPrograma)
    }

    @Transactional
    public Programa grava(Programa programa) {
        return programa.save()
    }

    @Transactional
    public boolean apaga(Programa programa) {
        programa.delete()
        return true
    }

}
