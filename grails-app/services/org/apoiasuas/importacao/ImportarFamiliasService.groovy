package org.apoiasuas.importacao

import com.myjeeva.poi.ExcelReader
import com.myjeeva.poi.ExcelRowContentCallback
import com.myjeeva.poi.ExcelWorkSheetRowCallbackHandler
import grails.async.Promise
import grails.async.Promises
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.Environment
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.SituacaoFamilia
import org.apoiasuas.cidadao.Telefone
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.SafeMap
import org.codehaus.groovy.grails.support.SoftThreadLocalMap
import org.springframework.transaction.annotation.Isolation

import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.util.IOUtils
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.regex.Pattern

//TODO Transformar processamento em uma JOB com processamento síncrono.
class ImportarFamiliasService {

    static final Pattern PATTERN_TEM_NUMEROS = Pattern.compile("(.)*(\\d)(.)*");
    static final Pattern PATTERN_TEM_LETRAS = Pattern.compile("(.)*[a-zA-Z]+(.)*");
    static transactional = false

    def sessionFactory //fabrica de sessoes hibernate
    def segurancaService

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED /*para conseguir ler o primeiro cabecalho*/)
    List<ColunaImportadaCommand> obtemColunasImportadas(long idImportacao) {

        log.debug(["Obtendo colunas da preImportacao", idImportacao])

        TentativaImportacao importacao = TentativaImportacao.get(idImportacao)

        //Obtém os cabeçalhos da última importação
        LinhaTentativaImportacao linha = null
        final int MAX_TEMPO_ESPERA = 3 /*minutos*/ * 60 * 1000
        final int INTERVALO_ESPERA = 2 /*segundos*/ * 1000
        long tempoEsperaTotal = 0
        while (!importacao?.cancelada() && linha == null && tempoEsperaTotal < MAX_TEMPO_ESPERA) {
            log.debug("Colunas ainda nao disponiveis apos ${tempoEsperaTotal} milisegundos")
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

        paraCadaDefinicaoCampoBD { fieldName, fieldValue ->
            def selectedValue = null;
            //2.1º Compara os nomes das colunas(planilha) com os conteúdos dos campos(BD)
            result.each() { colunaImportada ->
                if (colunaImportada.nome == fieldValue)
                    colunaImportada.campoBDSelecionado = fieldName
            }
        }
        log.debug(["Colunas identificadas", idImportacao])
        return result
    }

    @Transactional(readOnly = true)
    List<String> obtemCamposBDDisponiveis() {
        List<String> result = []
        paraCadaDefinicaoCampoBD { fieldName, fieldValue ->
            result << fieldName
        }
        return result
    }

    @Transactional
    def atualizaDefinicoesImportacaoFamilia(Map camposPreenchidos) {
        log.debug(["Atualizando definicoes de importacao", camposPreenchidos]);
        DefinicoesImportacaoFamilias definicoes = getDefinicoes();

        paraCadaDefinicaoCampoBD { fieldName, fieldValue ->
//            if (camposPreenchidos.containsKey(fieldName))
            log.debug("camposPreenchidos.get(fieldName)": camposPreenchidos.get(fieldName))
            definicoes.setProperty("coluna" + fieldName, camposPreenchidos.get(fieldName))
        }
//        log.debug([colunaBairro: definicoes.colunaBairro])
        definicoes.save()
    }

    //NÃO TRANSACIONAL
    void concluiImportacao(Map camposPreenchidos, long idImportacao) {

//Inicializações de variáveis locais:
        ResumoImportacaoDTO resultadoImportacao = new ResumoImportacaoDTO()

        Map camposPreenchidosInvertido = [:]
        camposPreenchidos.each { camposPreenchidosInvertido.put(it.value, it.key) }
        List camposBDDisponiveis = obtemCamposBDDisponiveis();
        log.debug(["Concluindo importacao id ", idImportacao])

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

        try {

            tentativaImportacao = obtemTentativaImportacaoComLinhas(idImportacao)
            atualizaProgressoImportacao(tentativaImportacao, StatusImportacao.INCLUINDO_FAMILIAS, null, 0, 0)

            UsuarioSistema usuarioLogado = segurancaService.getUsuarioLogado();


            tentativaImportacao.linhas.each { it ->
                linha = it //a variavel linha teve que ser definida fora do closure para que possa ser acessada pelo bloco try catch
                resultadoImportacao.linhasProcessadas++

                //cria um mapa adequado às nossas necessidades, ou seja, nomeCampoBD -> valorASerAtualizado
                SafeMap mapaDeCampos = converteListaParaMapa(grails.converters.JSON.parse(linha.JSON), camposPreenchidosInvertido, camposBDDisponiveis);

//                AmbienteExecucao.sabota("Erro global")

                //Despreza linhas em branco, usando como critério de campo obrigatório o codigoPropriedade familiar
                if (trim(mapaDeCampos.get("CodigoFamilia"))) {

//                  ===> Não faz mais sentido, porque cada linha sera importada em uma transacao diferente
//                    if (linhaProcessada % 50 == 0) {
//                        log.debug("Importação em andamento, linha " + linhaProcessada);
//                        cleanUpGorm() //Otimiza o cache hibernate e agiliza as parada
//                    }

                    //TODO: ao envés de obter os campos de DefinicoesImportacaoFamilias como strings, buscar nomes dos campos da própria classe ( em todos mapaDeCampos.get() )
                    //Detectando a mudança do codigoFamilia o que sinaliza o processamento de uma nova familia
//                  log.debug(mapaDeCampos.get("CodigoFamilia") + " (mapa) != "+ ultimaFamilia +" (ultima)")
                    if (!ultimaFamilia.equals(trim(mapaDeCampos.get("CodigoFamilia")))) {
                        ultimaFamilia = trim(mapaDeCampos.get("CodigoFamilia"))

                        //Assumindo primeiro registro como referencia
                        referencia = true;
                        erroNaFamilia = false

                        try {
                            familiaPersistida?.discard() //Tira do cache a ultima familia para agilizar a importacao

                            //Importa familia, endereco e telefones
                            familiaPersistida = importaFamilia(mapaDeCampos, resultadoImportacao, usuarioLogado)
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
                            importaCidadaos(nomeReferencia, mapaDeCampos, nisReferencia, familiaPersistida, resultadoImportacao, referencia, usuarioLogado, tentativaImportacao)
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
            log.debug(["Concluindo importacao id ", idImportacao])

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
    private TentativaImportacao obtemTentativaImportacaoComLinhas(long idImportacao) {
//Objetos persistentes:
        TentativaImportacao result = TentativaImportacao.get(idImportacao);
        //Buscando as linhas pré-inseridas em formato RAW (conteudoLinhaPlanilha)
        result.linhas = LinhaTentativaImportacao.findAll(sort: "ordem") {
            tentativaImportacao == result
        }
        log.debug(["Otendo linhas preImportadas [id, quantidade de linhas] ", [idImportacao, result?.linhas?.size()]])
        return result
    }

    @Transactional
    private Familia importaFamilia(SafeMap mapaDeCampos, ResumoImportacaoDTO resumoImportacaoDTO, UsuarioSistema usuarioLogado) {

        boolean novaFamilia = false
        boolean familiaGravada = false
        boolean podeAtualizar = false

//busca familia na tabela Familia
        Familia result = Familia.findByCodigoLegado(trim(mapaDeCampos.get("CodigoFamilia")))
        if (!result) {
            //se nao encontrar, insere nova
            novaFamilia = true
            result = Familia.novaInstacia()
            result.codigoLegado = trim(mapaDeCampos.get("CodigoFamilia"))
            result.criador = usuarioLogado
//                        familiaPersistida.dateCreated = new Date()
        }

        if (novaFamilia || !result.alteradoAposImportacao()) {
            //se a familia nao for nova, atualiza apenas se nao houve alteracao apos a importacao

            result.ultimoAlterador = usuarioLogado
            result.situacaoFamilia = SituacaoFamilia.CADASTRADA
            result.endereco.tipoLogradouro = trim(mapaDeCampos.get("TipoLogradouro"))
            result.endereco.nomeLogradouro = trim(mapaDeCampos.get("NomeLogradouro"))
            result.endereco.numero = tentaConverterInt(mapaDeCampos.get("Numero"))
            result.endereco.complemento = trim(mapaDeCampos.get("Complemento"))
            result.endereco.bairro = trim(mapaDeCampos.get("Bairro"))
            result.endereco.CEP = trim(mapaDeCampos.get("CEP"))
            result.endereco.municipio = trim(mapaDeCampos.get("Municipio")) ?: segurancaService.getMunicipio()
            result.endereco.UF = trim(mapaDeCampos.get("UF")) ?: segurancaService.getUF()
            result.dataUltimaImportacao = new Date()

//        if (AmbienteExecucao.SABOTAGEM)
//            assert result.codigoLegado != "4", "Erro na familia"

            result.save(flush: true) //Forca a ida ao BD para geracao automatica da data/hora de criacao e atualizacao
            result.dateCreated = convertExcelDate(mapaDeCampos.get("DataCadastroFamilia"))
            //Sobrescrever a data de criacao automatica com a data do cadastro presente na planilha importada
            result.save()

            familiaGravada = true
        }

        //Gravando telefone(s)
        importarTelefones(mapaDeCampos, result, usuarioLogado)

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
            log.debug("tentaConverterInt", t)
            return null
        }
    }

    private void importarTelefones(SafeMap mapaDeCampos, Familia result, UsuarioSistema usuarioLogado) {
        String numeroTelefone = trim(mapaDeCampos.get("Telefones"))

        //Desconsiderar telefones sem numeros (ex: "-")
        if (PATTERN_TEM_NUMEROS.matcher(numeroTelefone ?: "").matches()) {
            def telefones = Telefone.findAll {  //TODO: TESTARRRRRR
                familia == result && dataUltimaImportacao != null
            }
            Telefone telefone = telefones.size() > 0 ? telefones[0] : null

            if (!telefone) {
                telefone = new Telefone()
                telefone.familia = result
                telefone.criador = usuarioLogado
//                            telefone.dateCreated = convertExcelDate(mapaDeCampos.get("DataCadastroFamilia"));
            }
            telefone.ultimoAlterador = usuarioLogado
            telefone.numero = numeroTelefone
            telefone.dataUltimaImportacao = new Date()

            telefone.save()
            //FIXME Descartar alteracao no telefone caso haja algum mensagem neste passo da importacao
        }
    }

    @Transactional
    private void importaCidadaos(nomeReferencia, SafeMap mapaDeCampos, nisReferencia, Familia familiaPersistida, ResumoImportacaoDTO resumoImportacaoDTO, boolean referencia, UsuarioSistema usuarioLogado, TentativaImportacao importacao) {

        String nomeCidadao = referencia ? nomeReferencia : trim(mapaDeCampos.get("NomeCidadao"))
        String nis = referencia ? nisReferencia : trim(mapaDeCampos.get("NIS"))
        Cidadao cidadaoPersistido = null
        boolean novoCidadao = false
        boolean cidadaoGravado = false

        try {
            if (!getPATTERN_TEM_LETRAS().matcher(nomeCidadao ?: "").matches())
                throw new RuntimeException("Ignorando cidadao durante importacao. Nome e um campo obrigatorio")

//        Parentesco parentesco = idetificaParentesco(mapaDeCampos.get("Parentesco"))

            //busca cidadao pelo nome (case insensitive) e pela familia
            cidadaoPersistido = Cidadao.find { nomeCompleto =~ nomeCidadao && familia == familiaPersistida }
            if (!cidadaoPersistido) {
                novoCidadao = true
                //se nao encontrar, insere novo
                cidadaoPersistido = Cidadao.novaInstancia()
                cidadaoPersistido.nomeCompleto = nomeCidadao
                cidadaoPersistido.familia = familiaPersistida
                cidadaoPersistido.criador = usuarioLogado
            }

            if (novoCidadao || !cidadaoPersistido.alteradoAposImportacao()) {
                //se o cidadao nao for novo, atualiza apenas se nao houve alteracao apos a importacao

                cidadaoPersistido.referencia = referencia
                cidadaoPersistido.parentescoReferencia = trim(mapaDeCampos.get("Parentesco"))
                cidadaoPersistido.ultimoAlterador = usuarioLogado
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
            log.debug("importaCidadao dentro " + resumoImportacaoDTO);
            throw t
        }

    }

/*
    Parentesco idetificaParentesco(String valorImportado) {
        switch (valorImportado.toLowerCase().trim()) {
            case ["SOGRO", "SOGRA"]: return Parentesco.SOGRO
            case ["NORA", "GENRO"]: return Parentesco.GENRO
            case ["IRMA", "IRMAO"]: return Parentesco.IRMAO
            case ["SOBRINHO", "SOBRINHA"]: return Parentesco.SOBRINHO
            case ["MAE", "PAI"]: return Parentesco.PAI
            case ["NETO", "NETA", "BISNETO", "BISNETA"]: return Parentesco.NETO
            case ["FILHO", "FILHA"]: return Parentesco.FILHO
            case ["ENTEADO", "ENTEADA"]: return Parentesco.ENTEADO
            case ["AGREGADO", "AGREGADA"]: return Parentesco.NAO_PARENTE
            case "AVO": return Parentesco.AVO
            case "REFERENCIA": return Parentesco.RF
            case ["MARIDO", "COMPANHEIRO", "COMPANHEIRA", "ESPOSA", "ESPOSO"]: return Parentesco.COMPANHEIRO
            default: return Parentesco.OUTRO
        }
    }
*/

    private Date convertExcelDate(def data) {
        if (data instanceof Number)
            return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(data);
        else
            return null;
    }

    private SafeMap converteListaParaMapa(List lista, Map camposPreenchidosInvertido, List camposBDDisponiveis) {
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
    def private paraCadaDefinicaoCampoBD(Closure c) {

        //2º Para cada título de cabeçalho, verifica se ele já está definido nas configurações do sistema
        DefinicoesImportacaoFamilias definicoes = getDefinicoes()
        //TODO Listar campos na ordem em que estes sao declarados, e nao em ordem alfabetica como parece ser o padrao
        definicoes.domainClass.persistentProperties.each { persistentProperty ->
            String fieldName = persistentProperty.name;
            String fieldValue = definicoes.getPersistentValue(fieldName);
            if (fieldName.toLowerCase().startsWith("coluna")) {
                //Remove o inicio do nome do campo (coluna) antes de passa-lo para o closure
                c.call(fieldName.replaceFirst("coluna", ""), fieldValue);
            }
        }
    }

    private void cleanUpGorm() {
        sessionFactory.currentSession.flush()
        sessionFactory.currentSession.clear()
        //Limpando informacoes desnecessarias relativas a validacao de dominios do Grails (http://burtbeckwith.com/blog/?p=73)
        ((SoftThreadLocalMap) org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP).get().clear()
        log.debug("Limpando caches")
    }

    @Transactional(readOnly = true)
    DefinicoesImportacaoFamilias getDefinicoes() {
        return DefinicoesImportacaoFamilias.findAll().first();
    }

    @Transactional
    void inicializaDefinicoes(UsuarioSistema admin) {
        if (DefinicoesImportacaoFamilias.count() == 0) {
            def definicao = new DefinicoesImportacaoFamilias()
            definicao.linhaDoCabecalho = 1
            definicao.abaDaPlanilha = 1
            definicao.ultimoAlterador = admin
            definicao.lastUpdated = new Date()
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
            }
            definicao.save()
        }
    }

    //nao transacional(do ponto de vista de Banco de dados)
    public TentativaImportacao preImportacao(InputStream inputStream, TentativaImportacao importacao, int linhaDoCabecalho, int abaDaPlanilha) throws Exception {

        final ArrayList<LinhaTentativaImportacao> bufferImportacao = new ArrayList<LinhaTentativaImportacao>();
        final int LINHAS_POR_TRANSACAO = 20

        log.debug(["Iniciando preImportacao ", importacao.id])

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

                            if (bufferImportacao.size() == LINHAS_POR_TRANSACAO) {
                                descarregaBufferPreImportacao(bufferImportacao, importacao);
                            }

//                            if (AmbienteExecucao.SABOTAGEM && rowNum == 10)
//                                throw new RuntimeException("Teste de mensagem na preImportacao")

                            LinhaTentativaImportacao linhaTentativaImportacao = new LinhaTentativaImportacao();
                            linhaTentativaImportacao.setOrdem(rowNum - linhaDoCabecalho + 1L)

                            //Contador persistente de linhas nesta importacao
                            importacao.addLinhasEncontradas();

                            List tempList = new ArrayList();
                            for (String key : map.keySet()) {
                                Map tempMap = new HashMap();
                                tempMap.put(key, map.get(key));
                                tempList.add(tempMap);
                            }
                            linhaTentativaImportacao.setJSON(new JSON(tempList).toString());
                            bufferImportacao << linhaTentativaImportacao
                        }

                    });

            Promise p = Promises.task {
                try {
//                    if (linhaDoCabecalho == 2) {
                        //Instancia o processador e inicia o processamento
                        ExcelReader excelReader = new ExcelReader(pkg, sheetRowCallbackHandler, null);
                        log.debug("iniciando pre processamento de importacao em thread separado (id ${importacao?.id}")
                        excelReader.process(abaDaPlanilha - 1);
/*
                    } else {      TESTE DE IMPORTACAO DA VERSAO ANTIGA DO EXCEL (.XLS)

                        HSSFWorkbook wb = new HSSFWorkbook(inputStream);
                        HSSFSheet sheet = wb.getSheetAt(0);
                        HSSFRow row;
                        HSSFCell cell;
                        Iterator rows = sheet.rowIterator();
                        long totalLinhas = 0
                        long totalCelulas = 0
                        while (rows.hasNext()) {
                            row = (HSSFRow) rows.next();
                            Iterator cells = row.cellIterator();
                            totalLinhas++;
                            while (cells.hasNext()) {
                                cell=(HSSFCell) cells.next();
                                totalCelulas++
                            }
                        }
                        log.debug("total ${totalLinhas} linhas e ${totalCelulas} celulas")
                    }
*/
                    if (bufferImportacao.size() > 0)
                        descarregaBufferPreImportacao(bufferImportacao, importacao);
                    atualizaProgressoImportacao(importacao, StatusImportacao.ARQUIVO_PROCESSADO)
                } catch (Throwable t) {
                    essecaoPreImportacao(t, importacao)
                }
            }
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

    @Transactional
    private void atualizaProgressoImportacao(TentativaImportacao importacao, StatusImportacao status, String mensagem = null, Integer cidadaosProcessados = null, Integer errosDetectados = null) {
        importacao.setStatus(status);
        if (mensagem) {
            log.debug("Tamanho informacoes importacao " + mensagem.size())
            log.debug("Informacoes: " + mensagem)
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
    private void descarregaBufferPreImportacao(ArrayList<LinhaTentativaImportacao> bufferImportacao, TentativaImportacao importacao) {
        log.debug(['"descarregando" buffer de linhas da preImportacao ', importacao?.id])

        atualizaProgressoImportacao(importacao, StatusImportacao.PROCESSANDO_ARQUIVO)

        bufferImportacao.each { linhaTentativaImportacao ->
            linhaTentativaImportacao.tentativaImportacao = importacao
            linhaTentativaImportacao.save()
        }
        bufferImportacao.clear();

        sessionFactory.currentSession.flush()
        sessionFactory.currentSession.clear()
        //Limpando informacoes desnecessarias relativas a validacao de dominios do Grails (http://burtbeckwith.com/blog/?p=73)
        ((SoftThreadLocalMap) org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP).get().clear()
    }

    @Transactional
    /**
     * Registra uma nova tentativa de importacao no BD (um registro pai para os filhos do tipo LinhaTentativaImportacao inseridos na sequencia).
     */
    public TentativaImportacao registraNovaImportacao(int linhaDoCabecalho, int abaDaPlanilha) {
        DefinicoesImportacaoFamilias definicoes = DefinicoesImportacaoFamilias.findAll().first();
        definicoes.linhaDoCabecalho = linhaDoCabecalho
        definicoes.abaDaPlanilha = abaDaPlanilha
        definicoes.save()

        TentativaImportacao result = new TentativaImportacao()
        result.criador = segurancaService.usuarioLogado
        result.dateCreated = new Date()
        atualizaProgressoImportacao(result, StatusImportacao.ENVIANDO_ARQUIVO)
        result.save(failOnError: true)
        log.debug("Iniciada nova importacao com id ${result?.id}")
        return result
    }


}

