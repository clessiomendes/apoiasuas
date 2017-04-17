package org.apoiasuas.cidadao

import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao

class Monitoramento implements Comparable<Monitoramento> {

    public enum Situacao {
        ATRASADO("atrasado", "_ref_.efetivado = false and _ref_.data_prevista < ${AmbienteExecucao.SqlProprietaria.currentDate()}"),
        SEM_PRAZO("sem prazo definido", "_ref_.efetivado = false and _ref_.data_prevista is null"),
        PENDENTE_NO_PRAZO("pendente, dentro do prazo", "_ref_.efetivado = false and _ref_.data_prevista >= ${AmbienteExecucao.SqlProprietaria.currentDate()}"),
        SUSPENSO("suspenso", "_ref_.suspenso = true and _ref_.efetivado = false"),
        EFETIVADO("efetivado", "_ref_.efetivado = true")

        Situacao(String label, String sqlWhere) {
            this.label = label
            this.sqlWhere = sqlWhere
        }
        /**
         * Label a ser exibido na tela para traduzir a situação do monitoramento para o operador
         */
        private final String label
        /**
         * Expressão a ser inserida em uma sql para representar a situação no banco de dados
         * Usar _ref_ como tag de substituição para o alias da tabela 'monitoramento', a ser definido na clausula from da sql
         */
        private final String sqlWhere

        String getSqlWhere(String alias = "") {
            if (! alias?.trim()?.isEmpty())
                alias += "\\."
            return sqlWhere?.replaceAll("_ref_.", "$alias");
        }

        String getLabel() {
            return label
        }

    }

    private static final int TAMANHO_DESCRICAO_CORTADA = 150

    UsuarioSistema responsavel;
    Date dataCriacao, dataPrevista, dataEfetivada;
    Boolean efetivado;
    Boolean prioritario;
    static belongsTo = [familia: Familia]
    String memo;
    boolean suspenso;

    static transients = ['memoCortado', 'situacao'];

    static mapping = {
        familia column:'familia', index:'Monitoramento_Familia_Idx'
        id generator: 'native', params: [sequence: 'sq_monitoramento']
        efetivado(defaultValue: AmbienteExecucao.SqlProprietaria.getBoolean(false))
    }

    static constraints = {
        responsavel(nullable: false);
        memo(nullable: false, maxSize: 1000);
        familia(nullable: false);
        dataCriacao(nullable: false);
    }

    public String getMemoCortado() {
        if (! memo)
            return memo;
        String result = memo.substring(0, Math.min(memo.size(), TAMANHO_DESCRICAO_CORTADA));
        //acrescenta "..." sinalizando que o texto foi cortado nesta posicao
        if (! memo.endsWith(result))
            result = result + " ...";
        return result;
    }

    public int compareTo(Monitoramento outro) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (outro == null)
            return BEFORE;

        //suspensos por ultimo
        if (this.suspenso && ! outro.suspenso )
            return AFTER;
        if (! this.suspenso && outro.suspenso )
            return BEFORE;

        //efetivados por ultimo
        if (this.efetivado && ! outro.efetivado )
            return AFTER;
        if (! this.efetivado && outro.efetivado )
            return BEFORE;

        //atrasados primeiro
        if (this.atrasado() && ! outro.atrasado())
            return BEFORE;
        if (! this.atrasado() && outro.atrasado())
            return AFTER;

        //MAIS atrasados primeiro
        if (this.atrasado() && outro.atrasado())
            return this.dataPrevista.compareTo(outro.dataPrevista) //se atrasados, sempre tem data prevista

        //depois ordenar pela data de criacao
        if (this.dataCriacao && outro.dataCriacao)
            return this.dataCriacao.compareTo(outro.dataCriacao)

        //em ultimo caso, ordenar pelo id
        if (this.id && outro.id)
            return this.id.compareTo(outro.id);

        return EQUAL;
    }

    public boolean atrasado() {
        if (! efetivado && ! suspenso) //so consieramos atrasado se ainda nao tiver sido efetivado e nao estiver suspenso
            if (this.dataPrevista) //so consideramos atrasado se tiver uma data prevista
                return this.dataPrevista < new Date().clearTime();
        return false;
    }

    public String getSituacao() {
        String result = "";
        if (suspenso) {
            result += "monitoramento suspenso";
        } else if (efetivado) {
            result += "ação efetivada";
            if (dataEfetivada) {
                result += " em "+dataEfetivada.format("dd/MM/yyyy");
            }
        } else {
            if (dataPrevista) {
                result += "próximo monitoramento previsto para "+dataPrevista.format("dd/MM/yyyy");
            } else {
                result += "a monitorar";
            }
        }
        return result;
    }
}
