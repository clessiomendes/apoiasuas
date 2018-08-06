package org.apoiasuas.importacao

import com.myjeeva.poi.ExcelReader
import com.myjeeva.poi.ExcelRowContentCallback
import com.myjeeva.poi.ExcelWorkSheetRowCallbackHandler
import grails.async.Promise
import grails.async.Promises
import grails.converters.JSON
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.util.IOUtils
import org.apoiasuas.ProgramasPreDefinidos
import org.apoiasuas.cidadao.*
import org.apoiasuas.marcador.ProgramaFamilia
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.ApplicationContextHolder
import org.apoiasuas.util.SafeMap
import org.apoiasuas.util.StringUtils
import org.apoiasuas.util.SystemUtils
import org.codehaus.groovy.grails.support.SoftThreadLocalMap
import org.springframework.transaction.annotation.Isolation

import java.sql.SQLException

@Transactional(readOnly = true)
class ImportarFamiliasService {

    static transactional = false
    private static final int ALARME_IMPORTACAO_ATRASADA = 7
    public static final String NOME_PLANILHA_IMPORTACAO = "importacao.xlsx"
    public static final String BUCKET = "planilhasImportacao"
    private static final int LINHAS_POR_TRANSACAO = 50
    private static final int TEMPO_SUSPIRO = 300 //0.3s
    //dias

    SegurancaService segurancaService
    def groovySql

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED /*para conseguir ler o primeiro cabecalho*/)
    public List<ColunaImportadaCommand> obtemColunasImportadas(long idImportacao) {

        log.info(["Obtendo colunas da preImportacao", idImportacao])

        TentativaImportacao importacao = TentativaImportacao.get(idImportacao)

        //Obtém os cabeçalhos da última importação
        LinhaTentativaImportacao linha = null
        final int MAX_TEMPO_ESPERA = AmbienteExecucao.SABOTAGEM ? 5000 : 3 /*minutos*/ * 60 * 1000
        final int INTERVALO_ESPERA = 2 /*segundos*/ * 1000
        long tempoEsperaTotal = 0
        while (!importacao?.cancelada() && linha == null && tempoEsperaTotal < MAX_TEMPO_ESPERA) {
            log.info("Colunas ainda nao disponiveis apos ${tempoEsperaTotal} milisegundos")
            linha = LinhaTentativaImportacao.findByTentativaImportacao(TentativaImportacao.get(idImportacao));
            if (linha)
                break //obtido o resultado desejado. nao precisa tentar de novo
            sleep(INTERVALO_ESPERA)
            tempoEsperaTotal += INTERVALO_ESPERA
            importacao.refresh() //re-verifica se a importacao nao foi cancelada nesse meio tempo
        }

        if (!linha) //Erro! Nao foi possivel obter detalhes da importacao (ela foi cancelada ou nao gerou resultados)
            return null

        List<ColunaImportadaCommand> result = []

        List tempList = grails.converters.JSON.parse(linha?.JSON);
        tempList.each {
            it.each {
                //Transporta APENAS as definições de colunas para a resposta a ser enviada para a gsp
                result << new ColunaImportadaCommand(it.key, null, "" + it.value)
            }
        }

        paraCadaDefinicaoCampoBD(importacao.servicoSistemaSeguranca) { fieldName, fieldValue ->
            def selectedValue = null;
            //2.1º Compara os nomes das colunas(planilha) com os conteúdos dos campos(BD)
            result.each() { colunaImportada ->
                if (colunaImportada.nome == fieldValue)
                    colunaImportada.campoBDSelecionado = fieldName
            }
        }
        log.info(["Colunas identificadas", idImportacao])
        return result
    }

    @Transactional(readOnly = true)
    List<String> obtemCamposBDDisponiveis(ServicoSistema servicoSistema) {
        List<String> result = []
        paraCadaDefinicaoCampoBD(servicoSistema) { fieldName, fieldValue ->
            result << fieldName
        }
        return result
    }

    @Transactional
    def atualizaDefinicoesImportacaoFamilia(Map camposPreenchidos, ServicoSistema servicoSistema) {
        log.info(["Atualizando definicoes de importacao", camposPreenchidos]);
        DefinicoesImportacaoFamilias definicoes = getDefinicoes(servicoSistema);

        paraCadaDefinicaoCampoBD(servicoSistema) { fieldName, fieldValue ->
            log.info("camposPreenchidos.get(fieldName)": camposPreenchidos.get(fieldName))
            definicoes.setProperty("coluna" + fieldName, camposPreenchidos.get(fieldName))
        }
        definicoes.save()
    }

    @Transactional(readOnly = true)
    public Map getDefinicoesImportacaoFamilia(ServicoSistema servicoSistema) {
        Map result = [:]
        paraCadaDefinicaoCampoBD(servicoSistema) { fieldName, fieldValue ->
            if (fieldValue) //despreza campos sem definicao de importacao
                result.put(fieldName, fieldValue)
        }
        return result
    }

    @NotTransactional
    public void concluiImportacao(Map camposPreenchidos, long idImportacao, UsuarioSistema usuarioSistema, ServicoSistema servicoSistema) {

        log.info(["Concluindo importacao id ", idImportacao])

//Inicializações de variáveis locais:
        ResumoImportacaoDTO resultadoImportacao = new ResumoImportacaoDTO()

        Map camposPreenchidosInvertido = [:]
        //Inverte mapa para "campo na planilha -> campo no BD"
        camposPreenchidos.each {
            if (it.value)
                camposPreenchidosInvertido.put(it.value, it.key)
        }
        List camposBDDisponiveis = obtemCamposBDDisponiveis(servicoSistema);

        String ultimaFamilia = "-1"
        String nomeReferencia = null
        String nisReferencia = null
        Familia familiaPersistida = null
        boolean referencia = false
        boolean erroNaFamilia = false
        LinhaTentativaImportacao linha = null
        resultadoImportacao.linhasProcessadas = 1; //Comeca da linha 1 (linha 0 = cabecalho)
        Map criticaFamilias = [:]
        Map criticaCidadaos = [:]
        TentativaImportacao tentativaImportacao
        //Associa o servico obtido da sessao http com a sessao hibernate, para evitar erros de instancias duplicadas (NonUniqueObjectException)
        servicoSistema = servicoSistema.merge(validate: false, flush: false);

        aguardaPreImportacao(idImportacao)

        try {
            log.info("antes obtemTentativaImportacaoComLinhas")
            tentativaImportacao = obtemTentativaImportacaoComLinhas(idImportacao)
            atualizaProgressoImportacao(tentativaImportacao, StatusImportacao.INCLUINDO_FAMILIAS, null, 0, 0)
            log.info("depois obtemTentativaImportacaoComLinhas")

            if (! usuarioSistema)
                throw new RuntimeException("Nenhum operador do sistema definido como autor da importação")

            tentativaImportacao.linhas.each { it ->
                linha = it //a variavel linha teve que ser definida fora do closure para que possa ser acessada pelo bloco try catch
                resultadoImportacao.linhasProcessadas++

                if ((resultadoImportacao.linhasProcessadas % LINHAS_POR_TRANSACAO) == 0) {
                        log.info("fase conclusao, ${resultadoImportacao.linhasProcessadas} linhas processadas")
                        log.info(SystemUtils.systemStatistics());
                        def session = ApplicationContextHolder.grailsApplication.mainContext.sessionFactory.currentSession
                        session.flush();
                        session.clear();
                        //Limpando informacoes desnecessarias relativas a validacao de dominios do Grails (http://burtbeckwith.com/blog/?p=73)
                        ((SoftThreadLocalMap) org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP).get().clear()
                        System.sleep(TEMPO_SUSPIRO)
                }

                //cria um mapa adequado às nossas necessidades, ou seja, nomeCampoBD -> valorASerAtualizado
                SafeMap mapaDeCampos = converteListaParaMapa(grails.converters.JSON.parse(linha.JSON), camposPreenchidosInvertido, camposBDDisponiveis);

                //Verifica se todos os campos esperados (em DefinicoesImportacaoFamilia) estão presentes na planilha
                camposPreenchidosInvertido.each {
                    if (! mapaDeCampos.containsKey("coluna"+it.value))
                        throw new RuntimeException("Campo ${it.key} esperado mas ausente da planilha importada.")
                }

                //Despreza linhas em branco, usando como critério de campo obrigatório o codigo familiar
                if (trim(mapaDeCampos.get("CodigoFamilia"))) {

                    //TODO: ao envés de obter os campos de DefinicoesImportacaoFamilias como strings, buscar nomes dos campos da própria classe ( em todos mapaDeCampos.get() )
                    //Detectando a mudança do codigoFamilia o que sinaliza o processamento de uma nova familia
                    if (!ultimaFamilia.equals(trim(mapaDeCampos.get("CodigoFamilia")))) {
                        ultimaFamilia = trim(mapaDeCampos.get("CodigoFamilia"))

                        //Assumindo primeiro registro como referencia
                        referencia = true;
                        erroNaFamilia = false

                        try {
                            familiaPersistida?.discard() //Tira do cache a ultima familia para agilizar a importacao

                            //Importa familia, endereco e telefones
                            familiaPersistida = importaFamilia(mapaDeCampos, resultadoImportacao, usuarioSistema, servicoSistema)
                            nomeReferencia = trim(mapaDeCampos.get("NomeReferencia"))
                            nisReferencia = trim(mapaDeCampos.get("NISReferencia"))
                        } catch (Throwable t) {
                            log.warn("importarFamilia", t)
                            erroNaFamilia = true
                            familiaPersistida?.discard() //Nao gravar as alteracoes na familia
                            resultadoImportacao.adicionaInconsistencia(new ResumoImportacaoDTO.Erro(resultadoImportacao.linhasProcessadas, linha.JSON, t, true/*familia*/, ultimaFamilia/*codFamilia*/))
                            log.warn("importaFamilia fora " + resultadoImportacao);
                        }
                    }

                    //Importa cada cidadão referente à última família importada, considerando o primeiro cidadão como a referência familiar
                    if (!erroNaFamilia) {
                        try {
                            importaCidadaos(nomeReferencia, mapaDeCampos, nisReferencia, familiaPersistida, resultadoImportacao, referencia, usuarioSistema, servicoSistema, tentativaImportacao)
                        } catch (Throwable t) {
                            log.warn("Erro importando cidadao especifico", t)
                            //o objeto persistente cidadao ja foi descartado dentro de importaCidadao
                            resultadoImportacao.adicionaInconsistencia(new ResumoImportacaoDTO.Erro(resultadoImportacao.linhasProcessadas, linha.JSON, t, false/*familia*/, ultimaFamilia/*codFamilia*/))
                        } finally {
                            referencia = false
                        }
                    }
                }
            }
            log.info(["Concluida importacao id ", idImportacao])

            //Restringe tamanho do resultado da importacao a ser armazenado para nao estourar o campo do BD
            String JSONResultadoImportacao = new JSON(resultadoImportacao).toString()
            while (JSONResultadoImportacao.size() > TentativaImportacao.MAX_TAMANHO_CAMPO_INFORMACOES) {
                resultadoImportacao.inconsistencias.remove(resultadoImportacao.inconsistencias.last())
                JSONResultadoImportacao = new JSON(resultadoImportacao).toString()
            }
            atualizaProgressoImportacao(tentativaImportacao, StatusImportacao.CONCLUIDA, JSONResultadoImportacao, null, resultadoImportacao.totalErros)

        } catch (Throwable t) {
            try {
                log.warn("Erro. Importacao ${idImportacao} sendo cancelada...", t)
                String mensagemErro = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)
                atualizaProgressoImportacao(tentativaImportacao, StatusImportacao.CANCELADA_ERRO, mensagemErro);
            } catch (Throwable t2) {
                log.error("Erro tentando registrar cancelamento da importacao ${idImportacao} ", t2)
                /*ignora tentativa de registrar cancelamento no bd (propositalmente) */
            }
            throw t
        }
    }

    @Transactional(readOnly = true)
    private aguardaPreImportacao(long idImportacao) {
        log.info("verificando andamento da pre importacao")
        TentativaImportacao tentativaImportacao = TentativaImportacao.get(idImportacao)
        while (StatusImportacao.ARQUIVO_PROCESSADO != tentativaImportacao.status) {
            log.info("esperando conclusao da pre importacao ${idImportacao} ... ");
            sleep(TEMPO_SUSPIRO*10);
            tentativaImportacao.discard();
            tentativaImportacao = TentativaImportacao.get(idImportacao);
            log.info("tentativaImportacao atualizado na sessao");
        }
    }

    @Transactional(readOnly = true)
    private TentativaImportacao obtemTentativaImportacaoComLinhas(long idImportacao) {
//Objetos persistentes:
        TentativaImportacao result = TentativaImportacao.get(idImportacao);
        //Buscando as linhas pré-inseridas em formato RAW (conteudoLinhaPlanilha)
        result.linhas = LinhaTentativaImportacao.findAll(sort: "ordem") {
            tentativaImportacao == result
        }
        log.info(["Otendo linhas preImportadas [id, quantidade de linhas] ", [idImportacao, result?.linhas?.size()]])
        return result
    }

    @Transactional
    private Familia importaFamilia(SafeMap mapaDeCampos, ResumoImportacaoDTO resumoImportacaoDTO, UsuarioSistema usuarioSistema, ServicoSistema servicoSistema) {

        boolean novaFamilia = false
        boolean familiaGravada = false
        boolean podeAtualizar = false

//busca familia na tabela Familia
        Familia result = Familia.findByCodigoLegadoAndServicoSistemaSeguranca(trim(mapaDeCampos.get("CodigoFamilia")), servicoSistema)
        if (!result) {
            //se nao encontrar, insere nova
            novaFamilia = true
            result = Familia.novaInstacia()
            result.codigoLegado = trim(mapaDeCampos.get("CodigoFamilia"))
            result.criador = usuarioSistema
//                        familiaPersistida.dateCreated = new Date()
        }

        if (novaFamilia || !result.alteradoAposImportacao()) {
            //se a familia nao for nova, atualiza apenas se nao houve alteracao apos a importacao

            result.ultimoAlterador = usuarioSistema
            result.situacaoFamilia = SituacaoFamilia.CADASTRADA
            result.endereco.tipoLogradouro = trim(mapaDeCampos.get("TipoLogradouro"))
            result.endereco.nomeLogradouro = trim(mapaDeCampos.get("NomeLogradouro"))
            result.endereco.numero = tentaConverterInt(mapaDeCampos.get("Numero"))
            result.endereco.complemento = trim(mapaDeCampos.get("Complemento"))
            result.endereco.bairro = trim(mapaDeCampos.get("Bairro"))
            result.endereco.CEP = trim(mapaDeCampos.get("CEP"))
            //OBS: Se não houver informação de município e UF na planilha, assumiremos o município e UF do serviçoSistema
            result.endereco.municipio = trim(mapaDeCampos.get("Municipio")) ?: servicoSistema.endereco.municipio;
            result.endereco.UF = trim(mapaDeCampos.get("UF")) ?: servicoSistema.endereco.UF;
            result.dataUltimaImportacao = new Date()
            result.servicoSistemaSeguranca = servicoSistema

//        if (AmbienteExecucao.SABOTAGEM)
//            assert result.codigoLegado != "4", "Erro na familia"


            result.servicoSistemaSeguranca = result.servicoSistemaSeguranca.merge(validate: false, flush: false);
            result.save(flush: true) //Forca a ida ao BD para geracao automatica da data/hora de criacao e atualizacao
            result.dateCreated = convertExcelDate(mapaDeCampos.get("DataCadastroFamilia"))
            //Sobrescrever a data de criacao automatica com a data do cadastro presente na planilha importada
            result.save()

            importarPrograma(ProgramasPreDefinidos.BOLSA_FAMILIA, result, trim(mapaDeCampos.get("PBF")), usuarioSistema)
            importarPrograma(ProgramasPreDefinidos.BPC, result, trim(mapaDeCampos.get("BPC")), usuarioSistema)

            familiaGravada = true
        }

        //Gravando telefone(s)
        importarTelefones(mapaDeCampos, result, usuarioSistema)

        if (novaFamilia)
            resumoImportacaoDTO.novasFamilias++
        else if (familiaGravada)
            resumoImportacaoDTO.familiasAtualizadas++
        else
            resumoImportacaoDTO.familiasIgnoradas++

        return result
    }

    private String trim(Object valor) {
        if (valor?.toString()?.trim() && (valor?.toString()?.trim() != "-"))
            return valor?.toString()?.trim()
        else
            return null
    }

    private Integer tentaConverterInt(Object s) {
        try {
            return Integer.parseInt(s?.toString()?.trim())
        } catch (Throwable t) {
            log.warn("tentaConverterInt", t)
            return null
        }
    }

    private void importarTelefones(SafeMap mapaDeCampos, Familia familiaPersistida, UsuarioSistema usuarioSistema) {
        String numeroTelefone = trim(mapaDeCampos.get("Telefones"))

        //Desconsiderar telefones sem numeros (ex: "-")
        if (StringUtils.PATTERN_TEM_NUMEROS.matcher(numeroTelefone ?: "").matches()) {
            def telefones = Telefone.findAll {  //TODO: TESTARRRRRR
                familia == familiaPersistida && dataUltimaImportacao != null
            }
            Telefone telefone = telefones.size() > 0 ? telefones[0] : null

            if (!telefone) {
                telefone = new Telefone()
                telefone.familia = familiaPersistida
//                telefone.criador = usuarioSistema
//                            telefone.dateCreated = convertExcelDate(mapaDeCampos.get("DataCadastroFamilia"));
            }
//            telefone.ultimoAlterador = usuarioSistema
            telefone.numero = numeroTelefone
            telefone.dataUltimaImportacao = new Date()

            telefone.save()
            //FIXME Descartar alteracao no telefone caso haja algum mensagem neste passo da importacao
        }
    }

    /**
     * Importa informação relativa a um programa específico (PBF ou BPC)
     */
    private void importarPrograma(ProgramasPreDefinidos tipoPrograma, Familia familiaPersistida, String conteudoPrograma, UsuarioSistema usuarioSistema) {
        ProgramaFamilia associacao = ProgramaFamilia.findByFamiliaAndPrograma(familiaPersistida, tipoPrograma.instanciaPersistida)
        if (StringUtils.removeAcentos(conteudoPrograma?.toUpperCase()) != "SIM" && associacao) {
            associacao.delete();
            familiaPersistida.programas.remove(associacao)
        }
        if (StringUtils.removeAcentos(conteudoPrograma?.toUpperCase()) == "SIM" && ! associacao) {
            ProgramaFamilia pf = new ProgramaFamilia()
            pf.familia = familiaPersistida
            pf.habilitado = true
            pf.programa = tipoPrograma.instanciaPersistida
            pf.tecnico = usuarioSistema;
            if (! familiaPersistida.programas)
                familiaPersistida.programas = []
            familiaPersistida.programas.add(pf.save())
        }
    }

    @Transactional
    private void importaCidadaos(nomeReferencia, SafeMap mapaDeCampos, nisReferencia, Familia familiaPersistida, ResumoImportacaoDTO resumoImportacaoDTO, boolean referencia, UsuarioSistema usuarioSistema, ServicoSistema servicoSistema, TentativaImportacao importacao) {

        String nomeCidadao = referencia ? nomeReferencia : trim(mapaDeCampos.get("NomeCidadao"))
        String nis = referencia ? nisReferencia : trim(mapaDeCampos.get("NIS"))
        Cidadao cidadaoPersistido = null
        boolean novoCidadao = false
        boolean cidadaoGravado = false

        try {
            if (!StringUtils.PATTERN_TEM_LETRAS.matcher(nomeCidadao ?: "").matches())
                throw new RuntimeException("Ignorando cidadao durante importacao. Nome e um campo obrigatorio")

//        Parentesco parentesco = idetificaParentesco(mapaDeCampos.get("Parentesco"))

            //busca cidadao pelo nome (case insensitive) e pela familia
            cidadaoPersistido = Cidadao.find { nomeCompleto =~ nomeCidadao && familia == familiaPersistida }
            if (!cidadaoPersistido) {
                novoCidadao = true
                //se nao encontrar, insere novo
                cidadaoPersistido = Cidadao.novaInstancia()
                cidadaoPersistido.familia = familiaPersistida
                cidadaoPersistido.criador = usuarioSistema
                cidadaoPersistido.ultimoAlterador = usuarioSistema
                cidadaoPersistido.servicoSistemaSeguranca = servicoSistema
                cidadaoPersistido.habilitado = true

                cidadaoPersistido.nomeCompleto = nomeCidadao
            }

            if (novoCidadao || !cidadaoPersistido.alteradoAposImportacao()) {
                //se o cidadao nao for novo, atualiza apenas se nao houve alteracao apos a importacao

                cidadaoPersistido.referencia = referencia
                cidadaoPersistido.parentescoReferencia = referencia ? CidadaoService.PARENTESCO_REFERENCIA : trim(mapaDeCampos.get("Parentesco"))
                cidadaoPersistido.ultimoAlterador = usuarioSistema
                cidadaoPersistido.dataUltimaImportacao = new Date()
                cidadaoPersistido.dataNascimento = convertExcelDate(mapaDeCampos.get("DataNascimento"))
                cidadaoPersistido.nis = trim(nis)

//            if (AmbienteExecucao.SABOTAGEM)
//                assert nomeCidadao != "ALAN DE ALMEIDA LOPES", "Erro no cidadao"

//            log.debug(["cidadao ": cidadaoPersistido])
                cidadaoPersistido.save()
                cidadaoGravado = true
                cidadaoPersistido.discard() //Tira do cache para agilizar a importacao
            }

            //TODO verificar se o cidadao foi de fato atualizado (sugestao: criar metodo para atribuir cada valor a cidadaoPersistido, verificando se houve alteracao em relacao ao valor anterior)
            if (novoCidadao)
                resumoImportacaoDTO.novosCidadaos++
            else if (cidadaoGravado)
                resumoImportacaoDTO.cidadaosAtualizados++
            else
                resumoImportacaoDTO.cidadaosIgnorados++

            atualizaProgressoImportacao(importacao, StatusImportacao.INCLUINDO_FAMILIAS, null, resumoImportacaoDTO.linhasProcessadas)
        } catch (Throwable t) {
            cidadaoPersistido?.discard() //Nao gravar as alteracoes no cidadao
            log.warn("importaCidadao dentro " + resumoImportacaoDTO);
            throw t
        }

    }

    private Date convertExcelDate(def data) {
        if (data instanceof Number)
            return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(data);
        else
            return null;
    }

    protected SafeMap converteListaParaMapa(List<Map> lista, Map camposPreenchidosInvertido, List camposBDDisponiveis) {
        SafeMap result = new SafeMap(true, camposBDDisponiveis);
        //Converte uma lista de mapas em um mapa
        lista.each {
            it.each {
                if (camposPreenchidosInvertido.get(it.key))
                    result.put("coluna" + camposPreenchidosInvertido.get(it.key), it.value)
            }
        }
        return result
    }

    /**
     * Closure que percorre todos os campos definidos para importação (registro único) permitindo acessar o nome do campo
     * no BD (primeiro parametro) e o valor atualmente definido para correspondência com a coluna da planilha
     * (segundo parametro).
     */
    def private paraCadaDefinicaoCampoBD(ServicoSistema servicoSistema, Closure c) {

        //2º Para cada título de cabeçalho, verifica se ele já está definido nas configurações do sistema
        DefinicoesImportacaoFamilias definicoes = getDefinicoes(servicoSistema)
        //TODO Listar campos na ordem em que estes sao declarados, e nao em ordem alfabetica como parece ser o padrao
        definicoes.domainClass.persistentProperties.each { persistentProperty ->
            String fieldName = persistentProperty.name;
            String fieldValue = definicoes.getAt(fieldName);
            if (fieldName.toLowerCase().startsWith("coluna")) {
                //Remove o inicio do nome do campo (coluna) antes de passa-lo para o closure
                c.call(fieldName.replaceFirst("coluna", ""), fieldValue);
            }
        }
    }
/*
    private void cleanUpGorm() {
        def session = ApplicationContextHolder.grailsApplication.mainContext.sessionFactory.currentSession
//        TentativaImportacao.withSession { HibernateSession session ->
        session.flush();
        session.clear();
//        }
        //Limpando informacoes desnecessarias relativas a validacao de dominios do Grails (http://burtbeckwith.com/blog/?p=73)
        ((SoftThreadLocalMap) org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP).get().clear()
        log.debug("Limpando caches")
    }
*/
    @Transactional
    public DefinicoesImportacaoFamilias getDefinicoes(ServicoSistema servicoSistema) {
        DefinicoesImportacaoFamilias result = DefinicoesImportacaoFamilias.findByServicoSistemaSeguranca(servicoSistema);
        if (! result)
            result = inicializaDefinicoes(servicoSistema)
        return result
    }

    @Transactional
    private DefinicoesImportacaoFamilias inicializaDefinicoes(ServicoSistema servicoSistema) {
        DefinicoesImportacaoFamilias definicao = new DefinicoesImportacaoFamilias()
        definicao.linhaDoCabecalho = 1
        definicao.abaDaPlanilha = 1
        definicao.ultimoAlterador = segurancaService.admin
        definicao.lastUpdated = new Date()
        definicao.servicoSistemaSeguranca = servicoSistema
        /*
        if (AmbienteExecucao.isDesenvolvimento()) {
            definicao.linhaDoCabecalho = 2
            definicao.colunaDataCadastroFamilia = 'Data do Cadastro'
            definicao.colunaDataNascimento = 'Data de nascimento'
            definicao.colunaMunicipio = null
            definicao.colunaNIS = null
            definicao.colunaNISReferencia = 'NIS da referência'
            definicao.colunaNomeCidadao = 'Nome do integrante'
            definicao.colunaNomeLogradouro = 'Nome do logradouro'
            definicao.colunaNomeReferencia = 'Nome da referência'
            definicao.colunaNumero = 'Nº'
            definicao.colunaParentesco = 'Grau de parentesco'
            definicao.colunaTelefones = 'Telefone'
            definicao.colunaTipoLogradouro = 'Logradouro'
            definicao.colunaUF = null
            definicao.colunaBairro = 'Bairro'
            definicao.colunaCEP = 'CEP'
            definicao.colunaCodigoFamilia = 'Nº do Cadastro'
            definicao.colunaComplemento = 'Complemento'
            definicao.colunaPBF = 'B.F.'
            definicao.colunaBPC = 'B.P.C.'
        }
        */
        return definicao.save()
    }

    @Transactional
    private void limpaTabelasTemporarias() {
        try {
            log.debug("truncate table linha_tentativa_importacao")
            groovySql.execute("truncate table linha_tentativa_importacao")
        } catch (SQLException e) {
            log.error("Erro limpando tabela temporaria de importação")
            e.printStackTrace();
        }
    }


    //transacional para poder truncar tabela de importacao(do ponto de vista de Banco de dados)
    @Transactional
    public TentativaImportacao preImportacao(InputStream inputStream, TentativaImportacao importacao, int linhaDoCabecalho, int abaDaPlanilha, boolean assincrono) throws Exception {

        final ArrayList<LinhaTentativaImportacao> bufferImportacao = new ArrayList<LinhaTentativaImportacao>();
        long linhasProcessadas = 0;

        log.info(["Iniciando preImportacao ", importacao.id])
        //Limpando tabelas temporarias
        limpaTabelasTemporarias()

        OPCPackage pkg = null;

        try {

            pkg = OPCPackage.open(inputStream);

            /**
             * Processamento da planilha excel feito com o framework POI (http://poi.apache.org)
             * Por questoes de performance e consumo de recursos, optamos por fazer o processamento da planilha utilizando
             * o recurso "XSSF SAX event model", (http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api)
             * que percorre as celulas da planilha e delega seu processamento a um evento do tipo call back handler.
             *
             * Aproveitamos ainda um wrapper disponivel em https://github.com/jeevatkm/excelReader que facilita o uso do
             * referido recurso. Instrucoes a respeito do uso desta biblioteca:
             * http://myjeeva.com/read-excel-through-java-using-xssf-and-sax-apache-poi.html
             *
             * Ao inves de usar a biblioteca excelReader como dependencia do projeto, optamos por baixar seu codigoPropriedade
             * fonte e incorpora-lo ao nosso projeto afim de que pudessemos adapta-lo as nossas necessidades. Os
             * fontes estao em \sources\java\com.myjeeva.poi
             */

            ExcelWorkSheetRowCallbackHandler sheetRowCallbackHandler = new ExcelWorkSheetRowCallbackHandler(importacao, linhaDoCabecalho - 1 /*headerRow*/,
                    new ExcelRowContentCallback() {

                        @Override
                        /**
                         * Metodo de callback chamado para cada linha processada na planilha. Obs: linhas vazias no final
                         * tambem podem ser processadas.
                         */
                        //FIXME: Retirar parametro removaMe abaixo - metodo processRow()
                        public void processRow(TentativaImportacao removaMe, int rowNum, Map<String, Object> map) {

                            List tempList = new ArrayList();
                            boolean linhaVazia = true;
                            for (String key : map.keySet()) {
                                def value = map.get(key)
                                Map tempMap = new HashMap();
                                tempMap.put(key, value);
                                tempList.add(tempMap);
                                if (value != null && value.toString() != "")
                                    linhaVazia = false;
                            }

                            //ignora linhas completamente vazias
                            if (linhaVazia)
                                return;

                            linhasProcessadas++
                            if (bufferImportacao.size() == 5) {
                                descarregaBufferPreImportacao(bufferImportacao, importacao, linhasProcessadas);
                            }

                            LinhaTentativaImportacao linhaTentativaImportacao = new LinhaTentativaImportacao();
                            linhaTentativaImportacao.setOrdem(rowNum - linhaDoCabecalho + 1L)

                            //Contador persistente de linhas nesta importacao
                            importacao.addLinhasEncontradas();

                            linhaTentativaImportacao.setJSON(new JSON(tempList).toString());
                            bufferImportacao << linhaTentativaImportacao
                        }

                    });

            if (assincrono) {
                Promise p = Promises.task {
                    processExcelReader(pkg, sheetRowCallbackHandler, importacao, abaDaPlanilha, bufferImportacao)
                }
            } else {
                processExcelReader(pkg, sheetRowCallbackHandler, importacao, abaDaPlanilha, bufferImportacao)
            }
            log.info("encerrando pre importacao")
        } finally {
            IOUtils.closeQuietly(inputStream);
            try {
                if (null != pkg) {
                    pkg.close();
                }
            } catch (IOException e) {
                // just ignore IO exception
            }
        }
        return importacao;
    }

    private void processExcelReader(OPCPackage pkg, ExcelWorkSheetRowCallbackHandler sheetRowCallbackHandler, TentativaImportacao importacao, int abaDaPlanilha, ArrayList<LinhaTentativaImportacao> bufferImportacao) {
        try {
            ExcelReader excelReader = new ExcelReader(pkg, sheetRowCallbackHandler, null);
            log.info("iniciando pre processamento de importacao em thread separado (id ${importacao?.id}")
            excelReader.process(abaDaPlanilha - 1);
            if (bufferImportacao.size() > 0)
                descarregaBufferPreImportacao(bufferImportacao, importacao, null);
            atualizaProgressoImportacao(importacao, StatusImportacao.ARQUIVO_PROCESSADO)
        } catch (Throwable t) {
            essecaoPreImportacao(t, importacao)
        }
    }

    private void essecaoPreImportacao(Throwable t, TentativaImportacao importacao) {
        try {
            log.warn("Erro na preImportacao ${importacao?.id}. Cancelando...", t)
            //Permite armazenar excessoes levantadas pelo codigoPropriedade como mensagem a ser exibida para o usuario futuramente
            String mensagemErro = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)
            atualizaProgressoImportacao(importacao, StatusImportacao.CANCELADA_ERRO, mensagemErro);
        } catch (Throwable t2) {
            log.error("Erro tentando registrar essecao da preImportacao", t2)
            //ignora apenas a excecao de registro do cancelamento da importacao no BD
        }
    }

    @Transactional //(propagation = Propagation.REQUIRES_NEW)
    private void atualizaProgressoImportacao(TentativaImportacao importacao, StatusImportacao status, String mensagem = null, Integer cidadaosProcessados = null, Integer errosDetectados = null) {
        importacao.setStatus(status);
        if (mensagem) {
//            log.info("Tamanho informacoes importacao " + mensagem.size())
            log.info("Informacoes: " + mensagem)
            System.out.println(mensagem)
            if (mensagem.size() >= TentativaImportacao.MAX_TAMANHO_CAMPO_INFORMACOES - 1)
                mensagem = "Os detalhes da importação são muito grandes para serem gravados"
            importacao.setInformacoesDoProcessamento(mensagem);
        }
        if (cidadaosProcessados)
            importacao.linhasProcessadasConclusao = cidadaosProcessados
        if (errosDetectados)
            importacao.totalErros = errosDetectados
        importacao.save(failOnError: true)
    }

    @Transactional
    private void descarregaBufferPreImportacao(ArrayList<LinhaTentativaImportacao> bufferImportacao, TentativaImportacao importacao, Long linhasProcessadas) {
        System.sleep(TEMPO_SUSPIRO)
        log.info("descarregando buffer de linhas da preImportacao id ${importacao?.id}, ${linhasProcessadas} linhas processadas ate agora")
        log.info(SystemUtils.systemStatistics())

        atualizaProgressoImportacao(importacao, StatusImportacao.PROCESSANDO_ARQUIVO)

        bufferImportacao.each { linhaTentativaImportacao ->
            linhaTentativaImportacao.tentativaImportacao = importacao
            linhaTentativaImportacao.save()
        }
        bufferImportacao.clear();

        def session = ApplicationContextHolder.grailsApplication.mainContext.sessionFactory.currentSession
//        TentativaImportacao.withSession { HibernateSession session ->
            session.flush();
            session.clear();
//        }
        //Limpando informacoes desnecessarias relativas a validacao de dominios do Grails (http://burtbeckwith.com/blog/?p=73)
        ((SoftThreadLocalMap) org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP).get().clear()
    }

    @Transactional
    /**
     * Registra uma nova tentativa de importacao no BD (um registro pai para os filhos do tipo LinhaTentativaImportacao inseridos na sequencia).
     */
    public TentativaImportacao registraNovaImportacao(int linhaDoCabecalho, int abaDaPlanilha, UsuarioSistema usuarioSistema, ServicoSistema servicoSistema) {
        DefinicoesImportacaoFamilias definicoes = getDefinicoes(servicoSistema);
        definicoes.linhaDoCabecalho = linhaDoCabecalho
        definicoes.abaDaPlanilha = abaDaPlanilha
        definicoes.save()

        TentativaImportacao result = new TentativaImportacao()
        result.criador = usuarioSistema
        result.servicoSistemaSeguranca = servicoSistema;
        result.dateCreated = new Date()
        atualizaProgressoImportacao(result, StatusImportacao.ENVIANDO_ARQUIVO)
        result.save(failOnError: true)
        log.info("Iniciada nova importacao com id ${result?.id}")
        return result
    }

    @Transactional(readOnly = true)
    public DataUltimaImportacaoDTO getDataUltimaImportacaoBD(ServicoSistema servicoSistema) {
        DataUltimaImportacaoDTO result = new DataUltimaImportacaoDTO()
        TentativaImportacao ultimaImportacao = TentativaImportacao.find(
                "from TentativaImportacao a where a.status = :status and a.servicoSistemaSeguranca = :servicoSistema " +
                        "order by a.id desc",
                [status: StatusImportacao.CONCLUIDA, servicoSistema: servicoSistema]
        )
        result.valor = ultimaImportacao?.lastUpdated ?: ultimaImportacao?.dateCreated
        result.atrasada = result.valor ? new Date() - result.valor > ALARME_IMPORTACAO_ATRASADA : null
        return result
    }

}

