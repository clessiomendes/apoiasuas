package org.apoiasuas.importacao

import groovy.time.TimeCategory
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.ApoiaSuasDateUtils

class TentativaImportacao {

    public static final String ABORTADA = '[ABORTADA]'
    public static final String EM_ANDAMENTO = '[EM ANDAMENTO]'
    public static final int MAX_TAMANHO_CAMPO_INFORMACOES = 100000
    Date dateCreated, lastUpdated;
    UsuarioSistema criador
    StatusImportacao status
    Integer linhasPreProcessadas
    Integer linhasProcessadasConclusao
    Integer totalErros
    String informacoesDoProcessamento

    ServicoSistema servicoSistemaSeguranca

/*   ----------   ATRIBUTOS  TRANSIENTES    -----------  */
    static transients = [ "linhas" ]
    List<LinhaTentativaImportacao> linhas //transiente por questoes de performance. alimentar manualmente quando necessario

    static constraints = {
        criador(nullable: true)
        informacoesDoProcessamento(nullable: true)
        informacoesDoProcessamento(maxSize: MAX_TAMANHO_CAMPO_INFORMACOES);
    }

    String toString() { "Tentativa de importacao em " + dateCreated + " por " + criador?.username }

    static mapping = {
        linhasPreProcessadas(defaultValue: AmbienteExecucao.getLiteralInteiro(0))
        linhasProcessadasConclusao(defaultValue: AmbienteExecucao.getLiteralInteiro(0))
        totalErros(defaultValue: AmbienteExecucao.getLiteralInteiro(0))
        id generator: 'native', params: [sequence: 'sq_tentativa_importacao']
    }

    void addLinhasEncontradas() {
        if (linhasPreProcessadas == null)
            linhasPreProcessadas = 0
        this.setLinhasPreProcessadas(linhasPreProcessadas + 1)
    }

    public boolean cancelada() {
        return status != null && status in [StatusImportacao.CANCELADA_ERRO, StatusImportacao.CANCELADA_OPERADOR]
    }

    public boolean concluida() {
        return status != null && status in [StatusImportacao.CONCLUIDA]
    }

    public boolean getEmAndamento() {
        use(TimeCategory) {
            return lastUpdated.after(new Date() - 1.minutes) && (! cancelada()) && (! concluida())
        }
    }

    public boolean getProblemaDetectado() {
        if (! getEmAndamento() && ! concluida())
            return true
        if (concluida() && totalErros>0)
            return true
        if (StatusImportacao.CANCELADA_ERRO == status)
            return true

        return false
    }

    public String getStatusDetalhado() {
        //Respostas personalizadas que dependem do status e da importação estar em atividade
        if (emAndamento)
            switch (status) {
                case StatusImportacao.ENVIANDO_ARQUIVO: return "Recebendo planilha"
                case [StatusImportacao.PROCESSANDO_ARQUIVO, StatusImportacao.ARQUIVO_PROCESSADO]: return "Lendo planilha. ${linhasPreProcessadas} linhas lidas até agora"
//                case StatusImportacao.ARQUIVO_PROCESSADO: return "Planilha processada. Total de ${linhasPreProcessadas} linhas"
                case StatusImportacao.INCLUINDO_FAMILIAS: return "Incluindo famílias. ${percentualProcessamentoFinal()}% até agora"
            }
        else
            switch (status) {
                case StatusImportacao.ENVIANDO_ARQUIVO: return "Abortada enquanto planilha era recebida"
                case StatusImportacao.PROCESSANDO_ARQUIVO: return "Abortada enquanto processava planilha. ${linhasPreProcessadas} linhas lidas"
                case StatusImportacao.ARQUIVO_PROCESSADO: return "Abortada após processar planilha. Total de ${linhasPreProcessadas} linhas"
                case StatusImportacao.INCLUINDO_FAMILIAS: return "Abortada enquanto incluia famílias. ${percentualProcessamentoFinal()}% incluidas"
            }

        //Respostas fixas (importação efetivamente parada/terminada)
        switch (status) {
            case StatusImportacao.CONCLUIDA: return "Importação concluída em ${formataComHora(lastUpdated)}" + (totalErros ? " COM ERROS " : "")
            case StatusImportacao.CANCELADA_ERRO: return "Importação cancelada por erro no processamento"
            case StatusImportacao.CANCELADA_OPERADOR: return "Importação cancelada pelo operador do sistema"
        }
    }

    static String formataComHora(Date date) {
        return date.format(ApoiaSuasDateUtils.FORMATO_DATA_HORA)
    }

    private int percentualProcessamentoFinal() {
        if (! linhasPreProcessadas || ! linhasProcessadasConclusao)
            return 0
        return (100 * linhasProcessadasConclusao / linhasPreProcessadas).intValue()
    }
}
