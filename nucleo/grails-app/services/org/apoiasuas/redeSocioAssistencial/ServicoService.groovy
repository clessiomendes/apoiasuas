package org.apoiasuas.redeSocioAssistencial

import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import grails.transaction.Transactional
import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang.StringEscapeUtils
import org.apoiasuas.ambienteExecucao.SqlProprietaria
import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.HqlPagedResultList
import org.apoiasuas.util.LogHelper
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.SQLQuery
import org.hibernate.Session
import sun.misc.BASE64Encoder

import java.util.regex.Pattern

@Transactional(readOnly = true)
class ServicoService {

    private static final int TAMANHO_DESCRICAO_CORTADA = 150
    static final String BUCKET = "imagemServico";
    static final String TEMPLATE_FICHA_SERVICO = "templateFichaServico.docx"

    def sessionFactory
    SegurancaService segurancaService
    FileStorageService fileStorageService;

    @Transactional
    public Servico grava(Servico servico, String urlImagem ) {
        //tentar gravar antes de atualizar o repositorio (que esta fora do contexto da transacao)
        servico.save(flush: true);
        log.debug('grava servico');

        if (servico.fileAction == FileStorageDTO.FileActions.ATUALIZAR) {
            log.debug('atualiza imagem do servico');
            //Se ja existir um arquivo gravado, apaga antes de substituir
            if (servico.imagemFileStorage)
                fileStorageService.remove(BUCKET, servico.imagemFileStorage)
            //atualiza o repositorio com a imagem do servico
            byte[] base64 = Base64.decodeBase64(urlImagem.split(",")[1]);
            FileStorageDTO file = new FileStorageDTO("servico"+servico.id+".png", base64);
            servico.imagemFileStorage = fileStorageService.add(BUCKET, file);
        } else if (servico.fileAction == FileStorageDTO.FileActions.ANULAR) {
            log.debug('limpa imagem do servico');
            //Se ja existir um arquivo gravado, apaga antes de substituir
            if (servico.imagemFileStorage) {
                fileStorageService.remove(BUCKET, servico.imagemFileStorage);
                servico.imagemFileStorage = null;
            }
        }

        //grava novamente o servico para alimentar o apontador para o repositorio
        return servico.save();
    }

    @Transactional
    public boolean apaga(Servico servico) {
        //removendo associacoes que não são colecoes do servico (e que seriam cascateadas numa remocao)
        EstatisticaConsultaServico.findAllByServico(servico).each { it.delete(); }
        EstatisticaEncaminhamento.findAllByServico(servico).each { it.delete(); }
        servico.delete()
        return true
    }

    @Transactional(readOnly = true)
    public Servico getServico(Long idServico) {
        return Servico.get(idServico)
    }

    /**
     * Pesquisa servicos com as palavras chaves passadas no apelido, na descricao ou no publico cadastrados.
     * Resultado ordenado primeiro pela frequencia de acessos de consulta ao servico (EstatisticaConsultaServico) e depois por ordem alfabetica
     * @return
     */
    public grails.gorm.PagedResultList procurarServico(String palavraChave, GrailsParameterMap params) {

/*
        1) FALTA FILTRAR O TERRITORIO DE ATENDIMENTO
            1.1) SE USUARIO ADMIN, IGNORAR O TERRITORIO
        2) FALTA IMPLEMETAR UMA CHAMADA NO SERVIO PARA OBTER AS OPCOES A EXIBIR NA TELA DE ENCAMINHAMENTO, USANDO A ESTATISTICA DE ENCAMINHAMENTOS
        3) ANALISAR AS PESQUISAS INDEXADAS PELO ELASTIC SEARCH (TEM COMO FILTRAR TERRITORIO? TEM COMO ORDENAR PELA FREQUENCIA DE CONSULTAS?)
*/

        def filtrosSql = [:];

        String sqlSelectCount = "select count(*) ";
        String sqlSelectList = "select distinct {a.*}, coalesce(b.total, 0) ";
        String sqlFrom = " FROM servico a left JOIN (" +
                "    SELECT\n" +
                "        sum(quantidade) AS total,\n" +
                "        servico_id,\n" +
                "        servico_sistema_seguranca_id\n" +
                "    FROM estatistica_consulta_servico\n" +
                "    WHERE mes > current_date - INTERVAL '13 months'\n" +
                "        and servico_sistema_seguranca_id = :servico_sistema_seguranca_id\n" +
                "    GROUP BY servico_id, servico_sistema_seguranca_id\n" +
                "    ) b on a.id = b.servico_id ";
        String sqlOrder = " ORDER BY coalesce(b.total, 0) DESC, a.apelido asc ";

        //Territorios
        String sqlTerritorios = " with RECURSIVE sub_territorios as (\n" +
                "    select a.id, a.mae_id from abrangencia_territorial a where a.id = :id_territorio \n" +
                "  UNION\n" +
                "    select b.id, b.mae_id from abrangencia_territorial b\n" +
                "     JOIN sub_territorios s on b.id = s.mae_id\n" +
                ") ";
        String filtroTerritorio = " a.abrangencia_territorial_id is null\n" +
                "    or a.abrangencia_territorial_id in (select id from sub_territorios) "
        filtrosSql << [id_territorio: ServicoSistema.get(segurancaService.servicoLogado.id).abrangenciaTerritorial.id];



        filtrosSql << [servico_sistema_seguranca_id: segurancaService.servicoLogado.id]
        String sqlWhere = ' where 1=1 '
        List<Servico> servicos
        Integer count

        String[] palavrasChaves = null
        //remove espacos indesejados, converte para lowercase e divide as palavras
        if (palavraChave) {
            palavraChave = palavraChave.toLowerCase().trim()
            while (palavraChave.indexOf("  ") > -1)
                palavraChave = palavraChave.replaceAll("  "," ")
            palavrasChaves = palavraChave == "" ? null : palavraChave.split(" ")
            sqlWhere = " where ( 1=0 ";
            palavrasChaves.eachWithIndex { cadaPalavra, i ->
                String label = 'cadaPalavra'+i
                String novaClausula = " or lower(remove_acento(a.apelido)) like remove_acento(:"+label+") ";
                novaClausula += novaClausula.replaceAll("apelido", "descricao") + novaClausula.replaceAll("apelido", "publico");
                sqlWhere += novaClausula;
                filtrosSql.put(label, '%'+cadaPalavra?.toLowerCase()+'%')
            }
            sqlWhere += " ) ";
        }
        if (! params.incluirDesabilitados)
            sqlWhere += " and a.habilitado = " + AmbienteExecucao.SQL_FACADE.getBoolean(true);

        boolean restringirTerritorio = true;

        String sqlCount = restringirTerritorio ? "\n"+sqlTerritorios + sqlSelectCount + sqlFrom + sqlWhere+" and ($filtroTerritorio) \n" :
                          "\n"+sqlSelectCount + sqlFrom + sqlWhere+"\n";
        String sqlList = restringirTerritorio ? "\n"+sqlTerritorios + sqlSelectList + sqlFrom + sqlWhere+" and ($filtroTerritorio) \n"+ sqlOrder + "\n" :
                          "\n"+sqlTerritorios + sqlSelectList + sqlFrom  + sqlWhere + sqlOrder+"\n";

        Session sess = sessionFactory.getCurrentSession();
        SQLQuery queryCount = sess.createSQLQuery(sqlCount);
        SQLQuery queryList = sess.createSQLQuery(sqlList)
            .addEntity("a", Servico.class);
        log.debug(LogHelper.fillParameters("\n"+sqlTerritorios + sqlSelectList + sqlFrom  + sqlWhere + sqlOrder+"\n", filtrosSql));
        queryList.setFirstResult(params.offset ? new Integer(params.offset) : 0);
        queryList.setMaxResults(params.max ? new Integer(params.max) : 20);

        filtrosSql.each { key, value ->
            queryCount.setParameter(key, value);
            queryList.setParameter(key, value);
        }

        count = queryCount.uniqueResult();
        servicos = queryList.list();

        //Formata apelido e descricao para serem exibidos na tela
        Iterator<Servico> iterator = servicos.iterator()
        while (iterator.hasNext()) {
            Servico servico = iterator.next()
            servico.discard() //NAO gravar alteracoes
            servico.descricaoCortada = cortaDescricao(servico.descricao, palavrasChaves)
            //Escapa caracteres html por questoes de seguranca
            servico.apelido = StringEscapeUtils.escapeHtml(servico.apelido)
            servico.descricaoCortada = StringEscapeUtils.escapeHtml(servico.descricaoCortada)
            palavrasChaves?.each { cadaPalavra ->
                //Poe em negrito ocorrencias de cada palavra chave
                servico.apelido = servico.apelido?.replaceAll("(?i)" + Pattern.quote(StringEscapeUtils.escapeHtml(cadaPalavra)), '<b>$0</b>');
                servico.descricaoCortada = servico.descricaoCortada?.replaceAll("(?i)" + Pattern.quote(StringEscapeUtils.escapeHtml(cadaPalavra)), '<b>$0</b>');
            }
        }

        return new HqlPagedResultList(servicos, count)
    }

/*
    public grails.gorm.PagedResultList procurarServico(String palavraChave, GrailsParameterMap params, Boolean habilitado = null, Boolean permiteEncaminhamento = null) {
        String[] palavrasChaves = null
        //remove espacos indesejados, converte para lowercase e divide as palavras
        if (palavraChave) {
            palavraChave = palavraChave.toLowerCase().trim()
            while (palavraChave.indexOf("  ") > -1)
                palavraChave = palavraChave.replaceAll("  "," ")
            palavrasChaves = palavraChave == "" ? null : palavraChave.split(" ")
        }

        String hqlFrom = 'from Servico a '
        String hqlOrder = ' order by a.apelido'
        List servicos
        int count

        if (! palavrasChaves) {
            //Pesquisa sem palavra chave
            count = Servico.executeQuery("select count(*) " + hqlFrom, [:])[0]
            servicos = Servico.executeQuery(hqlFrom + hqlOrder, [:], params)
        } else {
            //Pesquisa com palavra chave
            def filtros = [:]

            String hqlFiltro = ""
            palavrasChaves.eachWithIndex { cadaPalavra, i ->
                String label = 'cadaPalavra'+i
                hqlFiltro += " or lower(remove_acento(a.apelido)) like remove_acento(:"+label+") "
                filtros.put(label, '%'+cadaPalavra?.toLowerCase()+'%')
            }
            //Usar um LinkedHashSet garante que os resultados nao se repitam
            LinkedHashSet<Servico> servicosTemp = new LinkedHashSet(Servico.executeQuery(hqlFrom + "where 1=0 " + hqlFiltro + hqlOrder, filtros, params))

            //se existem menos de 20 servicos com a palavra chave no apelido, procura servicos com a palavra chave na descricao
            if (servicosTemp.size() < params.max) {
                hqlFiltro = hqlFiltro.replaceAll("apelido", "descricao") + hqlFiltro.replaceAll("apelido", "publico")
                Iterator<Servico> servicosDescricaoPublico = Servico.executeQuery(hqlFrom + "where 1=0 " + hqlFiltro + hqlOrder, filtros, params).iterator()
                while (servicosTemp.size() < params.max && servicosDescricaoPublico.hasNext())
                    servicosTemp << servicosDescricaoPublico.next()
            }

            servicos = new ArrayList(servicosTemp)
            count = servicos.size()
        }

        //Formata apelido e descricao para serem exibidos na tela
        Iterator<Servico> iterator = servicos.iterator()
        while (iterator.hasNext()) {
            Servico servico = iterator.next()
            servico.discard() //NAO gravar alteracoes
            servico.descricaoCortada = cortaDescricao(servico.descricao, palavrasChaves)
            //Escapa caracteres html por questoes de seguranca
            servico.apelido = StringEscapeUtils.escapeHtml(servico.apelido)
            servico.descricaoCortada = StringEscapeUtils.escapeHtml(servico.descricaoCortada)
            palavrasChaves?.each { cadaPalavra ->
                //Poe em negrito ocorrencias de cada palavra chave
                servico.apelido = servico.apelido?.replaceAll("(?i)" + Pattern.quote(cadaPalavra), '<b>$0</b>')
                servico.descricaoCortada = servico.descricaoCortada?.replaceAll("(?i)" + Pattern.quote(cadaPalavra), '<b>$0</b>')
            }
        }

        return new HqlPagedResultList(servicos, count)
    }
*/

    /**
     * Corta a descricao para um maximo de 150 caracteres. Usa a primeira palavra chave presente na descricao como
     * criterio do corte, ou seja, o corte deve conter pelo menos uma das palavras.
     */
    private String cortaDescricao(String descricao, String[] palavrasChaves) {
        if (! descricao)
            return descricao
        String descricaoCortada
        int tamanho = descricao.size()
        int primeiraOcorrencia = -1

        //procura pela primeira ocorrencia de qualquer das palavras chaves
        palavrasChaves?.each { cadaPalavra ->
            int pos = cadaPalavra ? descricao.toLowerCase().indexOf(cadaPalavra.toLowerCase()) : -1
            if (pos >= 0) {
                if (primeiraOcorrencia == -1)
                    primeiraOcorrencia = pos
                else
                    primeiraOcorrencia = Math.min(primeiraOcorrencia, pos)
            }
        }

        if (primeiraOcorrencia == -1) {
            //nenhuma palavra chave na descricao
            descricaoCortada = descricao.substring(0, Math.min(tamanho, TAMANHO_DESCRICAO_CORTADA))
        } else {
            int inicio = Math.max(0, Math.round(primeiraOcorrencia - TAMANHO_DESCRICAO_CORTADA / 2) )
            int fim = Math.min(descricao.size(), Math.round(primeiraOcorrencia + TAMANHO_DESCRICAO_CORTADA / 2 ))
            if (inicio == 0)
                fim = Math.min(descricao.size(), TAMANHO_DESCRICAO_CORTADA)
            if (fim == descricao.size())
                inicio = Math.max(0, descricao.size() - TAMANHO_DESCRICAO_CORTADA)
            descricaoCortada = descricao.substring(inicio, fim)
        }

        //acrescenta "..." sinalizando que o texto foi cortado nesta posicao
        String result = descricaoCortada
        if (! descricao.startsWith(descricaoCortada))
            result = "... " + result
        if (! descricao.endsWith(descricaoCortada))
            result = result + " ..."
        return result
    }

    public Servico getServicoParaAnuncio() {
        String idsAbrangenciaTerritorial = segurancaService.getAbrangenciasTerritoriaisAcessiveis().collect {it.id}.join(",")
        if (! idsAbrangenciaTerritorial)
            return null;
        //filtrando apenas serviços que tenham um mínimo de informação a ser exibida (mais de 10 caracteres na descrição)
        String hql = "from Servico where length(descricao) > 10 and " +
                "((abrangenciaTerritorial is null) or abrangenciaTerritorial in(${idsAbrangenciaTerritorial}))"
        int count = Servico.executeQuery("select count(*) " + hql)[0]
        int index = new Random().nextInt(count);
        if (count)
            return Servico.executeQuery(hql, [offset: index, max:1]).find()
        else
            return null
    }

    public String getImagemAsString(Servico servico) {
        if (! servico.imagemFileStorage)
            return null;
        FileStorageDTO file = fileStorageService.get(BUCKET, servico.imagemFileStorage);
        return new BASE64Encoder().encode(file.bytes);
    }

    public byte[] getImagemAsBytes(String imagemFileStorage) {
        if (! imagemFileStorage)
            return null;
        FileStorageDTO file = fileStorageService.get(BUCKET, imagemFileStorage);
        return file.bytes;
    }

    @Transactional(readOnly = true)
    public ReportDTO imprimir(Servico servico) {
        ReportDTO result = new ReportDTO();
        result.nomeArquivo = "Ficha ${servico.apelido.replaceAll("[\\\\/:*?\"<>|]", "")  }.docx"

// 1) Load doc file and set Velocity template engine and cache it to the registry
        InputStream stream = this.class.getResourceAsStream("/org/apoiasuas/report/TemplateFichaServico.docx")
        result.report = XDocReportRegistry.getRegistry().loadReport(stream, TemplateEngineKind.Velocity);

// 2) Create Java model context
        result.context = result.report.createContext();
        result.fieldsMetadata = result.report.createFieldsMetadata();

        //define um mapa de pares chave/conteudo cujas CHAVES são buscadas como FIELDS no template do word e substiuídas
        // pelo conteúdo correspondente. Esse mapa é transferido na sequência para o CONTEXTO do mecanismo de geração do .doc
//        [
//                apelido                : servico.apelido,
//                nomeFormal             : servico.nomeFormal
//                cras               : familia.servicoSistemaSeguranca?.nome,
//                codigo_legado      : familia.cad,
//                referencia_familiar: familia.getReferencia()?.nomeCompleto,
//                nis_referencia     : familia.getReferencia()?.nis,
//                tecnico_referencia : tecnicoReferencia,
//                composicao_familiar: membrosToString(familia),
//                endereco           : familia.endereco.obtemEnderecoCompleto(),
//                ingresso           : familia.acompanhamentoFamiliar?.dataInicio?.format("dd/MM/yyyy"),
//                encerramento       : familia.acompanhamentoFamiliar?.dataFim?.format("dd/MM/yyyy"),
//                analise_tecnica    : familia.acompanhamentoFamiliar?.analiseTecnica,
//                resultados         : familia.acompanhamentoFamiliar?.resultados,
//                telefones          : familia.telefonesToString,
//                vulnerabilidades   : marcadoresToString(familia.vulnerabilidadesHabilitadas, "\n", "- "),
//                programas          : marcadoresToString(familia.programasHabilitados, "\n", "- "),
//                acoes              : marcadoresToString(familia.acoesHabilitadas, "\n", "- "),
//                monitoramentos     : monitoramentosToString(familia.monitoramentos)
//        ].each { chave, conteudo ->
//            result.context.put(chave, conteudo);
//        }
        result.context.put("servico", servico);
        return result;
    }

    @Transactional
    public registraEstatisticaConsultaServico(Servico servico) {
        //primeiro dia do mes
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.clearTime();
        Date mesCorrente = cal.getTime();

        EstatisticaConsultaServico estatistica = EstatisticaConsultaServico.
                findByMesAndServicoAndServicoSistemaSegurancaAndUsuarioSistema(
                        mesCorrente, servico, segurancaService.servicoLogado, segurancaService.usuarioLogado);
        if (! estatistica)
            estatistica = new EstatisticaConsultaServico(mes: mesCorrente, servico: servico,
                    servicoSistemaSeguranca: segurancaService.servicoLogado, usuarioSistema: segurancaService.usuarioLogado,
                    quantidade: 0);
        estatistica.quantidade++
        estatistica.save();
    }

    @Transactional
    public registraEstatisticaEncaminhamento(Servico servico) {
        //primeiro dia do mes
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.clearTime();
        Date mesCorrente = cal.getTime();

        EstatisticaEncaminhamento estatistica = EstatisticaEncaminhamento.
                findByMesAndServicoAndServicoSistemaSegurancaAndUsuarioSistema(
                        mesCorrente, servico, segurancaService.servicoLogado, segurancaService.usuarioLogado);
        if (! estatistica)
            estatistica = new EstatisticaEncaminhamento(mes: mesCorrente, servico: servico,
                    servicoSistemaSeguranca: segurancaService.servicoLogado, usuarioSistema: segurancaService.usuarioLogado,
                    quantidade: 0);
        estatistica.quantidade++
        estatistica.save();
    }

}
