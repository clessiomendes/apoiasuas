package org.apoiasuas

import grails.transaction.Transactional
import org.apoiasuas.ProgramasPreDefinidos
import org.apoiasuas.acao.Acao
import org.apoiasuas.programa.Programa
import org.apoiasuas.seguranca.UsuarioSistema

class AcaoService {

    @Transactional
    public void inicializaAcoes(UsuarioSistema admin) {
        if (Acao.count() == 0) {
            new Acao(descricao: "Endaminhar para SCFV 0 a 6").save();
            new Acao(descricao: "Endaminhar para SCFV idoso").save();
            new Acao(descricao: "Endaminhar para jovem aprendiz").save();
        }

//        ProgramasPreDefinidos.values().each { enumPrograma ->
//            try {
//                Programa programa = getProgramaPreDefinido(enumPrograma)
//                if (programa)
//                    enumPrograma.instanciaPersistida = programa
//                else //Criar novos quando necessario
//                    enumPrograma.instanciaPersistida = new Programa(nome: enumPrograma.nome, sigla: enumPrograma.sigla, programaPreDefinido: enumPrograma).save()
//            } catch (Throwable t) {
//                throw new RuntimeException("Erro inicializando programa ${enumPrograma}. Abortando", t)
//            }
//        }
    }

//    @Transactional
//    public Programa grava(Programa programa) {
//        return programa.save()
//    }
//
//    @Transactional
//    public boolean apaga(Programa programa) {
//        programa.delete()
//        return true
//    }

}
