package org.apoiasuas.relatorio

import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.programa.Programa
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.joda.time.LocalDate

@Transactional(readOnly = true)
class RelatorioService {

//    static final List CABECALHOS_CIDADAO = ["nome completo", "parentesco referência", "data de nascimento", "NIS"]
//    static final List CABECALHOS_FAMILIA = ["cad CRAS", "telefones", "tecnico de referência", "endereço", "CEP"] //TODO: adicionar siglas dos programas (maior cuidado, pbf, etc)

    def groovySql

    /**
     * Executa uma consulta SQL (Ansi92) com os parametros escolhidos e disponibiliza uma planilha csv em outputStream
     */
    public void geraListagem(OutputStream outputStream, LocalDate dataNascimentoInicial, LocalDate dataNascimentoFinal, String membros, Long idTecnicoReferencia, ArrayList<Programa> programasSelecionados) {

        String sqlSelect = "select distinct "
        String sqlFrom = "from "
        String sqlWhere = "where 1=1 "
        String sqlOrder = "order by "

        sqlSelect += AmbienteExecucao.SqlProprietaria.StringToNumber('f.codigo_legado')+' as "cad CRAS"';

        sqlFrom += 'familia f ' +
                ' left join usuario_sistema u on f.tecnico_referencia_id = u.id '

        sqlOrder +=  AmbienteExecucao.SqlProprietaria.StringToNumber('f.codigo_legado');

        if (membros) {
            sqlSelect += ', c.nome_completo as "nome", c.parentesco_referencia as "parentesco"';
            sqlFrom += ' left join cidadao c on f.id = c.familia_id ';
            sqlOrder += ', c.nome_completo';
        } else {
            sqlSelect += ', c.nome_completo as "referencia"';

            sqlFrom += ' left join vw_referencias r on f.id = r.familia_id ' +
                    ' left join cidadao c on r.referencia_id = c.id ';
        }

        sqlSelect += ', c.nis, c.identidade, c.cpf, ' +
                AmbienteExecucao.SqlProprietaria.dateToString('c.data_nascimento')+' as "nascimento", '+
                AmbienteExecucao.SqlProprietaria.idade('c.data_nascimento')+' as "idade", '+
                AmbienteExecucao.SqlProprietaria.concat("f.endereco_tipo_logradouro", "' '", "f.endereco_nome_logradouro", "', '",
                        "f.endereco_numero", "' '", "f.endereco_complemento")+' as "endereço", '+
                ' f.endereco_bairro as "bairro", f.endereco_cep as "cep", ' +
                ' u.username as "técnico de referência"';

        def filtros = []

        if (dataNascimentoInicial) {
            sqlWhere += ' and c.data_nascimento >= ? '
            filtros << new java.sql.Date(dataNascimentoInicial.toDate().getTime())
        }

        if (dataNascimentoFinal) {
            sqlWhere += ' and c.data_nascimento <= ? '
            filtros << new java.sql.Date(dataNascimentoFinal.toDate().getTime())
        }

        if (idTecnicoReferencia && idTecnicoReferencia != UsuarioSistema.SEM_SELECAO) {
            switch (idTecnicoReferencia) {
                case UsuarioSistema.SELECAO_ALGUM_TECNICO:
                    sqlWhere += " and f.tecnico_referencia_id is not null "
                    break;
                case UsuarioSistema.SELECAO_NENHUM_TECNICO:
                    sqlWhere += " and f.tecnico_referencia_id is null "
                    break;
                default:
                    sqlWhere += " and f.tecnico_referencia_id = ? "
                    filtros << idTecnicoReferencia
            }
        }

        if (programasSelecionados) {
            sqlFrom += '  left join programa_familia pf on pf.familia_id = f.id '

            String strProgramas = ""
            programasSelecionados.eachWithIndex { p, i -> strProgramas += (i==0 ? "":",") + p.id }
            sqlWhere += " and pf.programa_id in ($strProgramas)";
        }

        try {
            List<GroovyRowResult> resultado
                log.debug("SQL listagem (filtros - $filtros):" +"\n" + sqlSelect + '\n' + sqlFrom + '\n ' + sqlWhere + '\n' + sqlOrder)
                resultado = groovySql.rows(sqlSelect + ' ' + sqlFrom + ' ' + sqlWhere + ' ' + sqlOrder, filtros)

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "Windows-1252"));
            if (resultado) {
                //Imprime cabecalhos
                writer.append(montaAppend(resultado[0].collect { it.key }))
                writer.newLine()

                resultado.each { row ->
                    writer.append(montaAppend(row.collect { it.value } ))
                    writer.newLine()
                }
            } else {
                writer.append("Nenhuma informação encontrada para as opções escolhidas")
            }
            writer.flush()
        } finally {
            groovySql.close()
        }
    }

    private CharSequence montaAppend(List lista) {
        String result = ""
        lista.eachWithIndex { elemento, i ->
            if (elemento)
                result += elemento
            if (i < lista.size())
                result += "\t"
        }
        return result;
    }
}
