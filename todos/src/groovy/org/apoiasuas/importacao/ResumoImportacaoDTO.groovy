package org.apoiasuas.importacao

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Created by home64 on 21/01/2015.
 */
//FIXME: Migrar esta classe para uma estrutura interna aa classe TentativaImportacao
class ResumoImportacaoDTO {
    boolean importacaoAbortada = false
    long novasFamilias = 0
    long familiasAtualizadas = 0
    long familiasIgnoradas = 0
    long familiasComErros = 0
    long novosCidadaos = 0
    long cidadaosAtualizados = 0
    long cidadaosIgnorados = 0
    long cidadaosComErros = 0
    static final int MAX_ERROS_EXIBIDOS = 20
    int totalErros = 0
    int linhasProcessadas = 0
    private ArrayList<Erro> inconsistencias = []

    static final Log log = LogFactory.getLog(this)

    public void adicionaInconsistencia(Erro erro) {
        totalErros++

        if (erro.familia)
            familiasComErros++
        else
            cidadaosComErros++
/*
        //Restringir o tamanho das inconsistencias para nao estourar o campo no banco de dados
        int tamanhoInconsistencias = new conteudoLinhaPlanilha(this).toString().size()
        tamanhoInconsistencias += new conteudoLinhaPlanilha(erro).toString().size()
//        inconsistencias.each { inconsistencia ->
//            tamanhoInconsistencias += inconsistencia.conteudoLinhaPlanilha.size()
//            tamanhoInconsistencias += org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(inconsistencia.excecao).size()
//        }
        log.debug("${inconsistencias?.size()} inconsistencias ate agora. tamanho ocupado: ${tamanhoInconsistencias} ")
        log.debug(erro.conteudoLinhaPlanilha)
*/
        if (totalErros <= MAX_ERROS_EXIBIDOS /*&& tamanhoInconsistencias < TentativaImportacao.MAX_TAMANHO_CAMPO_INFORMACOES - 2000*/)
            inconsistencias << erro

    }

    public ArrayList<Erro> getInconsistencias() {
        return inconsistencias
    }

    static class Erro {
        Long numeroLinhaPlanilha
        String conteudoLinhaPlanilha
        List<String> excecoes = []
        Boolean familia
        String codFamilia = null

        Erro(Long numeroLinhaPlanilha, String conteudoLinhaPlanilha, Throwable excecao, Boolean familia, String codFamilia) {
            this.numeroLinhaPlanilha = numeroLinhaPlanilha
            this.conteudoLinhaPlanilha = conteudoLinhaPlanilha
            this.excecoes = mensagensErroEmpilhadas(excecao)
            this.familia = familia
            this.codFamilia = codFamilia
        }

        private List<String> mensagensErroEmpilhadas(Throwable excecao) {
            List result = []
            Throwable lExcecao = excecao
            while (lExcecao != null) {
                result << lExcecao.getClass().name + "( " + lExcecao.getMessage()+ " )"
                lExcecao = lExcecao.getCause()
            }
            return result
        }

        /**
         * Chamado diretamente da gsp para exibir um resumo das mensagens de erro.
         */
//        public List<String> getExcecoes() {
//            List result = []
//            Throwable lExcecao = excecao
//            while (lExcecao != null) {
//                result << lExcecao.getMessage() + "(" + lExcecao.getClass().name+ ")"
//                lExcecao = lExcecao.getCause()
//            }
//            return result
//        }

    }

}

