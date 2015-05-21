package org.apoiasuas.services;

import com.myjeeva.poi.ExcelReader;
import com.myjeeva.poi.ExcelRowContentCallback;
import com.myjeeva.poi.ExcelWorkSheetRowCallbackHandler;
import grails.converters.JSON;
import grails.transaction.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apoiasuas.DaoForJavaService;
import org.apoiasuas.importacao.StatusImportacao;
import org.apoiasuas.seguranca.SegurancaService;
import org.apoiasuas.importacao.LinhaTentativaImportacao;
import org.apoiasuas.importacao.TentativaImportacao;
import org.springframework.transaction.annotation.Propagation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Optamos por implementar o processamento das planilhas em um servico Java (e nao groovy). Para isso, ele teve que ser
 * declarado em resources.groovy e ter injetado os servicoes de que depende (DaoForJavaService e SegurancaService). O metodo
 * upload foi marcado como transacional (org.springframework.transaction.annotation.Transactional) para que pudesse
 * usufruir do mecanismo automatizado de gerenciamento de transacoes via AOP do Spring.
 *
 */
public class ImportarFamiliasJavaService {

    private final static Log log = LogFactory.getLog(ImportarFamiliasJavaService.class);
    private DaoForJavaService daoForJavaService;
    private ImportarFamiliasJavaService importarFamiliasJava;
    private SegurancaService segurancaService;

    public void setSegurancaService(SegurancaService serviceInstance) {
        this.segurancaService = serviceInstance;
    }

    public void setDaoForJavaService(DaoForJavaService daoInstance) {
        this.daoForJavaService = daoInstance;
    }

    public void setImportarFamiliasJava(ImportarFamiliasJavaService importarFamiliasJava) {
        this.importarFamiliasJava = importarFamiliasJava;
    }

    //nao transacional(do ponto de vista de Banco de dados)
    public TentativaImportacao upload(InputStream inputStream, int linhaDoCabecalho, int abaDaPlanilha/*worksheet*/) throws Exception {

        //Registra uma nova tentativa de importacao no BD (um registro pai para os filhos do tipo LinhaTentativaImportacao inseridos a seguir).
        final TentativaImportacao novaImportacao = importarFamiliasJava.novaImportacao(linhaDoCabecalho, abaDaPlanilha);
        final ArrayList<LinhaTentativaImportacao> bufferImportacao = new ArrayList<LinhaTentativaImportacao>();

        OPCPackage pkg = null;

        try {
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

            pkg = OPCPackage.open(inputStream);

            ExcelWorkSheetRowCallbackHandler sheetRowCallbackHandler = new ExcelWorkSheetRowCallbackHandler(novaImportacao, linhaDoCabecalho - 1 /*headerRow*/,
                    new ExcelRowContentCallback() {

                        @Override
                        /**
                         * Metodo de callback chamado para cada linha processada na planilha. Obs: linhas vazias no final
                         * tambem podem ser processadas.
                         */
                        public void processRow(TentativaImportacao importacao, int rowNum, Map<String, Object> map) {

                            if (bufferImportacao.size() == 20) {
                                descarregaBuffer(bufferImportacao);
                            }

                            //Obtem novo registro JÁ ASSOCIADO À CAMADA DE PERSISTENCIA
                            LinhaTentativaImportacao linhaTentativaImportacao = novaLinhaTentativaImportacao(novaImportacao, rowNum + 0L);
                            //Logo, não será preciso reassociar chamando save()

                            importacao.addLinhasEncontradas();

                            List tempList = new ArrayList();
                            for (String key : map.keySet()) {
                                Map tempMap = new HashMap();
                                tempMap.put(key, map.get(key));
                                tempList.add(tempMap);
                            }
                            linhaTentativaImportacao.setJSON(new JSON(tempList).toString());

//                            if ((rowNum+1) % 50 == 0) {
//                                daoForJavaService.clearGorm();
//                            }
                        }

                    });

            //Instancia o processador e inicia o processamento
            ExcelReader excelReader = new ExcelReader(pkg, sheetRowCallbackHandler, null);
            excelReader.process(abaDaPlanilha - 1);

            novaImportacao.setStatus(StatusImportacao.CONCLUIDA);
            daoForJavaService.gravaTentativaImportacao(novaImportacao);
        } catch (Throwable t) {
            cancelaImportacao(novaImportacao, t.getMessage());
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

        return novaImportacao;
    }

    @Transactional
    private void cancelaImportacao(TentativaImportacao novaImportacao, String mensagemErro) {
        novaImportacao.setStatus(StatusImportacao.CANCELADA_ERRO);
        novaImportacao.setInformacoesDoProcessamento(mensagemErro);
        daoForJavaService.gravaTentativaImportacao(novaImportacao);
    }

    @Transactional
    private void descarregaBuffer(ArrayList<LinhaTentativaImportacao> bufferImportacao) {
        for (int i = 0; i < bufferImportacao.size(); i++) {
            daoForJavaService.gravaNovaLinhaTentativaImportacao(bufferImportacao.get(i));
        }
        bufferImportacao.clear();
        //Para evitar estouros de memoria:
        daoForJavaService.clearGorm();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TentativaImportacao novaImportacao(int linhaDoCabecalho, int abaDaPlanilha) {
        daoForJavaService.atualizaDefinicoes(linhaDoCabecalho, abaDaPlanilha);
        return daoForJavaService.novaTentativaImportacao();
    }

    //Nao transacional
    private LinhaTentativaImportacao novaLinhaTentativaImportacao(TentativaImportacao importacao, long ordem) {
/*
        LinhaTentativaImportacao linha = new LinhaTentativaImportacao();
        linha.setTentativaImportacao(importacao);
        return linha;
         */
        System.out.println(10 / 0);
        return null;
    }


}
