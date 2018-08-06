package org.apoiasuas

import com.gc.iotools.stream.is.InputStreamFromOutputStream
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import groovy.sql.Sql
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.xmlbeans.XmlOptions
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.PostgreSQL81Dialect
import org.hibernate.tool.hbm2ddl.DatabaseMetadata
import org.hibernate.transform.AliasToEntityMapResultTransformer
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver

@Transactional(readOnly = true)
class ApoiaSuasService {

    static transactional = false;
    def grailsApplication
//    SessionFactory sessionFactory

    //FIXME sintaxe dependente do Postgree* (levar para AmbienteExecucao)
    static final String MISSING_SEQUENCES_SQL =
            "SELECT table_name\n" +
                    "  FROM information_schema.columns\n" +
                    " WHERE table_schema='public'\n" +
                    "   and column_name='id'   \n" +
                    "   and not table_name = 'ambiente'   \n" +
                    "   and not table_name = 'servicoSistema'   \n" +
                    //padrao de nome de sequencias criadas automaticamente para campos do tipo "serial" no postgresql:
                    "   and table_name || '_id_seq' not IN ( SELECT a.sequence_name FROM information_schema.sequences a )\n" +
                    //padrao de nome de sequencias criadas explicitamente para o projeto (compatibilidade):
                    "   and 'sq_' || table_name not IN ( SELECT a.sequence_name FROM information_schema.sequences a )"

    @Transactional(readOnly = true)
    public String[] getAtualizacoesPendentes(javax.sql.DataSource dataSource, String sessionFactoryName) {
//        Configuration conf = sessionFactory.configuration
        def sessionFactoryBean = grailsApplication.mainContext.getBean("&"+sessionFactoryName)
        SessionFactory sessionFactory = sessionFactoryBean.sessionFactory
        Configuration conf = sessionFactoryBean.configuration
        //FIXME as classes Postgree* não podem estar acopladas (levar para AmbienteExecucao)
        DatabaseMetadata metadata = new DatabaseMetadata(new Sql(dataSource).dataSource.connection, AmbienteExecucao.CURRENT2.getDialect());
        String[] result = conf.generateSchemaUpdateScriptList(new PostgreSQL81Dialect(), metadata).collect { it.script };

        sessionFactory.currentSession.createSQLQuery(MISSING_SEQUENCES_SQL).with{
            resultTransformer = AliasToEntityMapResultTransformer.INSTANCE
            list().each { Map row ->
                result = result + ("Faltando sequencia para a tabela " + row.values().toArray()[0])
            }
        }

        return result
    }

    @Transactional(readOnly = true)
    public long ocupacaoBD() {
//        def result = groovySql.firstRow("select 0").get(0)
//        def result = groovySql.firstRow("select pg_database_size('bcck9gsbpzsnf7y')").getAt(0)
//        return (result instanceof Number) ? result.longValue() : null;
        return 0
//        select pg_size_pretty(pg_database_size('bcck9gsbpzsnf7y'));
    }

    @NotTransactional
    public Resource obtemArquivo(String nomeArquivo) throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
//        Resource[] resources = resolver.getResources("classpath*:/**/teste-multi.docx");
        Resource[] resources = resolver.getResources("classpath*:/**/$nomeArquivo");
        if (! resources)
            throw new RuntimeException("Arquivo não encontrado: ($nomeArquivo)")
        else {
            if (resources.size() > 1)
                //TODO: listar os diferentes locais do resource no classpath usando resources[*].getDescription()
                log.warn("Atenção! Encontradas mais de uma referência a $nomeArquivo no classpath")
            return resources[0];
        }
    }

    public void appendReports(OutputStream dest, List<ReportDTO> reports) throws Exception {
        List<InputStream> documentos = reports.collect {
            new InputStreamFromOutputStream<Void>() {
                public Void produce(final OutputStream dataSink) throws Exception {
                    it.report.process(it.context, dataSink);
                }
            };
        };
        appendOfficeStreams(dest, documentos);
    }

    @NotTransactional
    public void appendOfficeStreams(OutputStream dest, List<InputStream> documentos) throws Exception {

        InputStream primeiroInputStream = documentos[0]
        documentos.remove(0);
        OPCPackage primeiroPackage = OPCPackage.open(primeiroInputStream);
        XWPFDocument primeiroDocument = new XWPFDocument(primeiroPackage);
        CTBody primeiroBody = primeiroDocument.getDocument().getBody();
        documentos.each { InputStream proximoInputStream ->
            OPCPackage proximoPackage = OPCPackage.open(proximoInputStream);
            /*
                    XWPFParagraph paragraph = src1Document.createParagraph();
                    paragraph.setPageBreak(true);
            */
            XWPFDocument proximoDocument = new XWPFDocument(proximoPackage);

            List<XWPFParagraph> paragraphs = proximoDocument.getParagraphs();
            paragraphs[0].setPageBreak(true);

            CTBody proximoBody = proximoDocument.getDocument().getBody();

            //adiciona o proximo no primeiro. o primeiro passa a conter a soma dos dois.
            appendBody(primeiroBody, proximoBody);
        }
        primeiroDocument.write(dest);
    }

    private void appendBody(CTBody src, CTBody append) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String srcString = src.xmlText();
        String prefix = srcString.substring(0,srcString.indexOf(">")+1);
        String mainPart = srcString.substring(srcString.indexOf(">")+1,srcString.lastIndexOf("<"));
        String sufix = srcString.substring( srcString.lastIndexOf("<") );
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix+mainPart+addPart+sufix);
        src.set(makeBody);
    }



}
