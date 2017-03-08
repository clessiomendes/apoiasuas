package org.apoiasuas.relatorio

import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.Programa
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.ListaLigada
import org.joda.time.LocalDate

@Transactional(readOnly = true)
class RelatorioService {

    public static final String LABEL_CAD = 'cad CRAS'
    public static final String LABEL_PARENTESCO = 'parentesco'
    public static final String LABEL_NOME = 'nome'
    public static final String LABEL_REFERENCIA = 'referencia'
    public static final String LABEL_NASCIMENTO = 'nascimento'
    public static final String LABEL_IDADE = 'idade'
    public static final String LABEL_ENDERECO = 'endereço'
    public static final String LABEL_BAIRRO = 'bairro'
    public static final String LABEL_CEP = 'cep'
    public static final String LABEL_TECNICO = 'técnico de referência'
    public static final String LABEL_DDD_TELEFONE = "ddd"
    public static final String LABEL_NUMERO_TELEFONE = "numero"
    public static final String LABEL_SIGLA_PROGRAMA = "sigla"

    def groovySql
    def segurancaService

    public void geraListagemFinal(OutputStream outputStream, boolean planilhaParaDownload, List<GroovyRowResult> registrosEncontrados) {
        ListaLigada<GroovyRowResult> resultadoTelefones, resultadoProgramas
        //Busca a listagem completa de telefones e programas e armazena numa lista ligada com ponteiro de avanço (padrão recordset do JDBC)
        resultadoTelefones = new ListaLigada<GroovyRowResult>(telefonesTodasFamilias());
        resultadoTelefones.pula()
        resultadoProgramas = new ListaLigada<GroovyRowResult>(programasTodasFamilias());
        resultadoProgramas.pula()
        log.debug("Encontrados ${resultadoTelefones.size()} telefones e ${resultadoProgramas.size()} programas no cadastro completo")
//            programas = programasFamilias();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, planilhaParaDownload ? "windows-1250" : "utf-8"));
        if (registrosEncontrados) {
            if (planilhaParaDownload) {
                //Indica o separador de campos no arquivo (tab)
                writer.append("sep=\t")
                writer.newLine()
                //Imprime cabecalhos
                writer.append(montaAppendCSV(registrosEncontrados[0].collect { it.key } + ["telefones","programas"] ))
                writer.newLine()
            } else {
                writer.append('<table style="width:100%;border: 1px solid black;">')
                writer.append(montaAppendHTML(registrosEncontrados[0].collect { it.key } + ["telefones","programas"] ))
            }

            registrosEncontrados.each { row ->
                String telefones = telefonesFamilia(getInt(row.get(LABEL_CAD)), resultadoTelefones)
                String programas = programasFamilia(getInt(row.get(LABEL_CAD)), resultadoProgramas)
                if (planilhaParaDownload) {
                    writer.append(montaAppendCSV(row.collect { it.value } + [telefones, programas]))
                    writer.newLine()
                } else {
                    writer.append(montaAppendHTML(row.collect { it.value } + [telefones, programas]))
                }
            }
        } else {
            writer.append("Nenhuma informação encontrada para as opções escolhidas")
        }
        writer.flush()
    }

    /**
     * Executa uma consulta SQL (Ansi92) com os parametros escolhidos e disponibiliza uma planilha csv em outputStream
     */
    public List<GroovyRowResult> processaConsulta(LocalDate dataNascimentoInicial, LocalDate dataNascimentoFinal,
                             String membros, Long idTecnicoReferencia, List<Programa> programasSelecionados,
                             List<Vulnerabilidade> vulnerabilidadesSelecionadas, List<Acao> acoesSelecionadas,
                             List<OutroMarcador> outrosMarcadoresSelecionados, ServicoSistema servicoSistema) {

        String sqlPrincipalSelect = "select distinct "
        String sqlPrincipalFrom = "from "
        String sqlPrincipalWhere = "where 1=1 "
        String sqlPrincipalOrder = "order by "

        String sqlTelefonesSelect = "select "
        String sqlTelefonesFrom = "from familia f join telefone t "

        if (segurancaService.acessoRecursoServico(RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO)) {
            sqlPrincipalSelect += AmbienteExecucao.SqlProprietaria.StringToNumber('f.codigo_legado') + ' as "' + LABEL_CAD + '"'
            sqlPrincipalOrder +=  AmbienteExecucao.SqlProprietaria.StringToNumber('f.codigo_legado');
        } else {
            sqlPrincipalSelect += 'f.id as "' + LABEL_CAD + '"';
            sqlPrincipalOrder +=  'f.id';
        }

        sqlPrincipalFrom += 'familia f ' +
                ' left join usuario_sistema u on f.tecnico_referencia_id = u.id '

        if (membros) {
            sqlPrincipalSelect += ', c.nome_completo as "'+LABEL_NOME+'", c.parentesco_referencia as "'+ LABEL_PARENTESCO+'"';
            sqlPrincipalFrom += " left join cidadao c on f.id = c.familia_id and c.habilitado = ${AmbienteExecucao.SqlProprietaria.getBoolean(true)} ";
            sqlPrincipalOrder += ', c.nome_completo';
        } else {
            sqlPrincipalSelect += ', c.nome_completo as "'+LABEL_REFERENCIA+'"';

            sqlPrincipalFrom += ' left join vw_referencias r on f.id = r.familia_id ' +
                    ' left join cidadao c on r.referencia_id = c.id ';
        }

        sqlPrincipalSelect += ', c.nis, c.identidade, c.cpf, ' +
                AmbienteExecucao.SqlProprietaria.dateToString('c.data_nascimento')+ ' as "' + LABEL_NASCIMENTO + '", ' +
                AmbienteExecucao.SqlProprietaria.idade('c.data_nascimento')+ ' as "' + LABEL_IDADE + '", ' +
                AmbienteExecucao.SqlProprietaria.concat("f.endereco_tipo_logradouro", "' '", "f.endereco_nome_logradouro", "', '",
                        "f.endereco_numero", "' '", "f.endereco_complemento")+ ' as "' + LABEL_ENDERECO + '", ' +
                ' f.endereco_bairro as "' + LABEL_BAIRRO + '", f.endereco_cep as "' + LABEL_CEP + '", ' +
                ' u.username as "' + LABEL_TECNICO + '"';

        def filtros = []

        if (servicoSistema) {
            sqlPrincipalWhere += ' and f.servico_sistema_seguranca_id = ? '
            filtros << servicoSistema.id
        }

        if (dataNascimentoInicial) {
            sqlPrincipalWhere += ' and c.data_nascimento >= ? '
            filtros << new java.sql.Date(dataNascimentoInicial.toDate().getTime())
        }

        if (dataNascimentoFinal) {
            sqlPrincipalWhere += ' and c.data_nascimento <= ? '
            filtros << new java.sql.Date(dataNascimentoFinal.toDate().getTime())
        }

        if (idTecnicoReferencia && idTecnicoReferencia != UsuarioSistema.SEM_SELECAO) {
            switch (idTecnicoReferencia) {
                case UsuarioSistema.SELECAO_ALGUM_TECNICO:
                    sqlPrincipalWhere += " and f.tecnico_referencia_id is not null "
                    break;
                case UsuarioSistema.SELECAO_NENHUM_TECNICO:
                    sqlPrincipalWhere += " and f.tecnico_referencia_id is null "
                    break;
                default:
                    sqlPrincipalWhere += " and f.tecnico_referencia_id = ? "
                    filtros << idTecnicoReferencia
            }
        }

        if (programasSelecionados) {
            sqlPrincipalFrom += '  left join programa_familia pf on pf.familia_id = f.id '
            String strMarcadores = ""
            programasSelecionados.eachWithIndex { p, i -> strMarcadores += (i==0 ? "":",") + p.id }
            sqlPrincipalWhere += " and pf.programa_id in ($strMarcadores)";
        }

        if (vulnerabilidadesSelecionadas) {
            sqlPrincipalFrom += '  left join vulnerabilidade_familia vf on vf.familia_id = f.id '
            String strMarcadores = ""
            vulnerabilidadesSelecionadas.eachWithIndex { p, i -> strMarcadores += (i==0 ? "":",") + p.id }
            sqlPrincipalWhere += " and vf.vulnerabilidade_id in ($strMarcadores)";
        }

        if (acoesSelecionadas) {
            sqlPrincipalFrom += '  left join acao_familia af on af.familia_id = f.id '
            String strMarcadores = ""
            acoesSelecionadas.eachWithIndex { p, i -> strMarcadores += (i==0 ? "":",") + p.id }
            sqlPrincipalWhere += " and af.acao_id in ($strMarcadores)";
        }

        if (outrosMarcadoresSelecionados) {
            sqlPrincipalFrom += '  left join outro_marcador_familia of on of.familia_id = f.id '
            String strMarcadores = ""
            outrosMarcadoresSelecionados.eachWithIndex { p, i -> strMarcadores += (i==0 ? "":",") + p.id }
            sqlPrincipalWhere += " and of.outro_marcador_id in ($strMarcadores)";
        }

        try {
            log.debug("SQL listagem (filtros - $filtros):" +"\n" + sqlPrincipalSelect + '\n' + sqlPrincipalFrom + '\n ' + sqlPrincipalWhere + '\n' + sqlPrincipalOrder)
            return groovySql.rows(sqlPrincipalSelect + ' ' + sqlPrincipalFrom + ' ' + sqlPrincipalWhere + ' ' + sqlPrincipalOrder, filtros)
        } finally {
            groovySql.close()
        }
    }

    private static int getInt(Object value) {
        return ((Number) value).intValue()
    }

    /**
     * Pressuposto: que ambas as queries (familias e telefones) estao ordenadas por cad.
     * Avança na query de telefones em busca do telefone correspondente ao cad.
     * Caso não seja encontrado nenhum telefone para este cad, mantém o cursor da lista resultadoTelefones no
     * cad seguinte.
     */
    private static String telefonesFamilia(int cad, ListaLigada<GroovyRowResult> resultadoTelefones) {
        String result = null, ddd = null

        //Avança até o próximo cad da lista (ou até o seu fim)
        while (! resultadoTelefones.fim() && getInt(resultadoTelefones.atual().get(LABEL_CAD)) <= cad) {
            if (getInt(resultadoTelefones.atual().get(LABEL_CAD)) == cad) {
                result = result ? result+"," : "" //inicializa quando necessario
                ddd = resultadoTelefones.atual().get(LABEL_DDD_TELEFONE)
                result += (ddd ? "($ddd)" : "") + resultadoTelefones.atual().get(LABEL_NUMERO_TELEFONE)
            }
            resultadoTelefones.pula();
        }
        return result
    }

    /**
     * Aglutina todos os telefones para cada família individualmente, para fins de exibição de múltiplos valores
     * como um único campo no resultado final
     */
    private List<GroovyRowResult> telefonesTodasFamilias() {
        final String campoCad = (segurancaService.acessoRecursoServico(RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO)) ?
                AmbienteExecucao.SqlProprietaria.StringToNumber('f.codigo_legado')
                : "f.id";
        String sql = "select distinct t.ddd, t.numero, " + campoCad + ' as "' + LABEL_CAD + '" \n' +
                " from familia f join telefone t on f.id = t.familia " +
                " where 1=1 and t.numero is not null and " + campoCad + " is not null \n" +
                " order by " + campoCad;
        return groovySql.rows(sql, [])
    }

    /**
     * Aglutina todos os programas para cada família individualmente, para fins de exibição de múltiplos valores
     * como um único campo no resultado final
     */
    private List<GroovyRowResult> programasTodasFamilias() {
        final String campoCad = (segurancaService.acessoRecursoServico(RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO)) ?
                AmbienteExecucao.SqlProprietaria.StringToNumber('f.codigo_legado')
                : "f.id";
        String sql = "select distinct "+ AmbienteExecucao.SqlProprietaria.valorNaoNulo("p.sigla","p.descricao")+" as sigla, " +
                campoCad + ' as "' + LABEL_CAD + '" \n' +
                " from familia f join programa_familia pf on f.id = pf.familia_id join programa p on pf.programa_id = p.id " +
                " where 1=1 and " + campoCad + " is not null \n" +
                " order by " + campoCad;
        return groovySql.rows(sql, [])
    }

    /**
     * Pressuposto: que ambas as queries (familias e telefones) estao ordenadas por cad.
     * Avança na query programas em busca do programa correspondente ao cad.
     * Caso não seja encontrado nenhum programa para este cad, mantém o cursor da lista resultadoProgramas no
     * cad seguinte.
     */
    private String programasFamilia(int cad, ListaLigada<GroovyRowResult> resultadoProgramas) {
        String result, ddd

        //Avança até o próximo cad da lista (ou até o seu fim)
        while (! resultadoProgramas.fim() && getInt(resultadoProgramas.atual().get(LABEL_CAD)) <= cad) {
            if (getInt(resultadoProgramas.atual().get(LABEL_CAD)) == cad) {
                result = result ? result+"," : "" //inicializa quando necessario
                result += resultadoProgramas.atual().get(LABEL_SIGLA_PROGRAMA)
            }
            resultadoProgramas.pula();
        }
        return result
    }

    private CharSequence montaAppendHTML(Collection lista) {
        String result = "<tr>"
        lista.eachWithIndex { elemento, i ->
                result +=  '<td style="border: 1px solid black;">' + (elemento != null ? elemento : "" ) + '</td>'
        }
        return result + "</tr>";
    }

    private CharSequence montaAppendCSV(Collection lista) {
        String result = ""
        lista.eachWithIndex { elemento, i ->
            if (elemento != null) {
                //FIXME: tirar eventuais caracteres \t (tab) da planilha para nao estragar a formatacao
                result += elemento instanceof String ? '="' + elemento + '"' : elemento
            }
            if (i < lista.size())
                result += "\t"
        }
        return result;
    }
}
