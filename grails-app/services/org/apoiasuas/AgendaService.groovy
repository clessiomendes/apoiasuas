package org.apoiasuas

import com.gc.iotools.stream.is.InputStreamFromOutputStream
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.TemplateEngineKind
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonSlurper
import org.apache.commons.lang.time.DateUtils
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.xmlbeans.XmlOptions
import org.apoiasuas.agenda.Compromisso
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody

@Transactional(readOnly = true)
class AgendaService {

    private static final String FILTRO_COMPROMISSOS_PERIODO = " where a.servicoSistemaSeguranca = :servicoSistema "+
            " and ((a.inicio >= :periodoInicio and a.inicio < :periodoFim) or (a.fim > :periodoInicio and a.fim < :periodoFim)) ";

    def segurancaService
    def usuarioSistemaService

    @Transactional
    public Compromisso gravaCompromisso(Compromisso compromisso) {
        if (! compromisso.id)
            compromisso.servicoSistemaSeguranca = segurancaService.getServicoLogado()
        compromisso.save();

        //Sempre que gravar um compromisso, atualizar o atendimento correspondente
        if (compromisso.tipo.atendimento)
            compromisso.atendimentoParticularizado.with {
                if (compromisso.inicio && compromisso.inicio != dataHora)
                    dataHora = compromisso.inicio;
//                if (compromisso.responsavel && compromisso.responsavel.id != tecnico?.id)
//                    tecnico = compromisso.responsavel;
                save();
            }

        return compromisso;
    }

    /**
     * Retorna uma lista de compromissos entre a data inicioInclusive (inclusive) e a data fimExclusive (ultimo dia +1),
     * opcionalmente filtrados pelo id do operador para o qual o compromisso foi designado.
     * Exemplo: para obter todos os dias da semana iniciada em 12/06/2017, passar os parametros (12/06/2017, 19/06/2017)
     */
    public List<Compromisso> listarCompromissos(Date inicioInclusive, Date fimExclusive, Long idUsuarioSistema,
                                                Boolean mostrarAtendimentos, Boolean mostrarOutrosCompromissos) {
        //OTIMIZAÇÃO: "left join fecth" nas associacoes atendimentoParicularizado e participantes
        String hqlFrom = "select distinct a from Compromisso a left join fetch a.atendimentoParticularizado left join fetch a.participantes ";
        String hqlWhere = FILTRO_COMPROMISSOS_PERIODO;
        Map filtros = [servicoSistema: segurancaService.servicoLogado, periodoInicio: inicioInclusive, periodoFim: fimExclusive]
        if (idUsuarioSistema) {
            hqlWhere += ' and (size(a.participantes) = 0 or :responsavel in elements(a.participantes) ) ';
            filtros.put("responsavel", UsuarioSistema.get(idUsuarioSistema))
        }
        if (! mostrarAtendimentos) {
            hqlWhere += " and a.tipo != :tipoAcompanahemnto ";
            filtros.put("tipoAcompanahemnto", Compromisso.Tipo.ATENDIMENTO_PARTICULARIZADO)
        }
        if (! mostrarOutrosCompromissos) {
            hqlWhere += " and a.tipo !=  :tipoOutros ";
            filtros.put("tipoOutros", Compromisso.Tipo.OUTROS)
        }
        return Compromisso.executeQuery(hqlFrom + hqlWhere, filtros);
/*
 from Compromisso a left join fetch a.atendimentoParticularizado left join fetch a.participantes  where a.servicoSistemaSeguranca = :servicoSistema  and ((a.inicio >= :periodoInicio and a.inicio < :periodoFim) or (a.fim >= :periodoInicio and a.fim < :periodoFim))
*/
    }

    public Compromisso getCompromisso(Long idCompromisso) {
        return Compromisso.findById(idCompromisso, [fetch:[atentimentoParticularizado:"eager"]]);
    }


    public void imprimirAgendaAtendimentos(OutputStream out, Date inicio, Date fim) {
        inicio = DateUtils.truncate(inicio, Calendar.DATE);
        fim = DateUtils.truncate(fim, Calendar.DATE);
        List<InputStream> agendasPreenchidas = []
        for (Date i = inicio; i <= fim; i = i + 1) {
            List<AtendimentoParticularizado> atendimentos = AtendimentoParticularizado.
                    findAllByDataHoraGreaterThanEqualsAndDataHoraLessThanAndServicoSistemaSeguranca(i, i+1, segurancaService.servicoLogado, [sort: 'dataHora']);
            if (atendimentos)
                agendasPreenchidas.add(preencheAgendaComPipe(atendimentos, i));
        }

        //FIXME try catch e close para todos os streams criados

        append(out, agendasPreenchidas)
//        out.close();
    }

    private InputStream preencheAgendaComPipe(List<AtendimentoParticularizado> atendimentos, Date dia) {
        InputStreamFromOutputStream<Void> pipeStream = new InputStreamFromOutputStream<Void>() {
//                true, true, ExecutorServiceFactory.getExecutor(ExecutionModel.THREAD_PER_INSTANCE), 4096) {
            @Override
            public Void produce(final OutputStream dataSink) throws Exception {
                /*
                 * call the function who produces the data (writes to the OutputStream)
                 * WARNING: we're in another thread here, so this method shouldn't
                 * write any class field or make assumptions on the state of the class.
                 */
                preencheAgenda(atendimentos, dia, dataSink);
            }
        };
        //Se não houver compromissos nesta data - preencheAgenda() retornou false - nao produzir um pipe para entrar no arquivo final
        return pipeStream //.result ? pipeStream : null;
    }

    public static void append(OutputStream dest, List<InputStream> documentos) throws Exception {
//        documentos = documentos.removeAll { it == null };
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

    private static void appendBody(CTBody src, CTBody append) throws Exception {
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

    private void preencheAgenda(List<AtendimentoParticularizado> atendimentos, Date dia, OutputStream out) {

        //0) Inicia buscando todos os compromissos do dia e preenchendo um DTO do tipo Map para ser usado no componente de report
        boolean result = false;
        List<Map<String,String>> dtoAtendimentos = new ArrayList<Map<String,String>>();
        atendimentos.each { atendimento ->
            result = true; //pelo menos um compromisso definido nesta data
            Map<String,String> entry = new HashMap<String, String>();
            entry.put("horario", atendimento.dataHora.format("HH:mm") ?: "");
            entry.put("tecnico", atendimento.tecnico.username ?: "");
            entry.put("nome", atendimento.nomeCidadao ?: "");
            entry.put("codigo_legado", atendimento.familia?.cad ?: "");
            entry.put("telefone", atendimento.telefoneContato ?: "");
            dtoAtendimentos.add(entry);
        }

//        if (! result)
//            return;
//            return result; //se não houver compromissos nesta data, não gera a parada

        // 1) Load Docx file by filling Velocity template engine and cache it to the registry
        InputStream template = this.class.getResourceAsStream("templateAgendaAtendimentos.docx")
        if (! template)
            throw new ApoiaSuasException("template da agenda de atendimento provavelmente não encontrado ou não pode ser aberto.")
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(template, TemplateEngineKind.Velocity);

        // 2) Create fields metadata to manage lazy loop (#forech velocity) for table row.
        FieldsMetadata metadata = new FieldsMetadata();
        metadata.addFieldAsList("compromissos.horario");
        metadata.addFieldAsList("compromissos.tecnico");
        metadata.addFieldAsList("compromissos.nome");
        metadata.addFieldAsList("compromissos.codigo_legado");
        metadata.addFieldAsList("compromissos.telefone");
        report.setFieldsMetadata(metadata);

        // 3) Create context Java model
        IContext context = report.createContext();
        context.put("titulo", dia.format("EEEE, dd / MMMM / yyyy"));
        context.put("compromissos", dtoAtendimentos);

        // 4) Generate report by merging Java model with the Docx
//        OutputStream out = new FileOutputStream(new File("DocxProjectWithVelocityList_Out.docx"));
//        report.convert(context, options, out);
        report.process(context, out);
    }

    @Transactional
    public boolean deleteCompromisso(Long idCompromisso) {
        Compromisso compromisso = Compromisso.get(idCompromisso);
        compromisso.delete();
        if (compromisso.tipo.atendimento)
            compromisso.atendimentoParticularizado.delete();
        if (! compromisso)
            return false;
        return true;
    }

    @Transactional
    public Compromisso criaCompromissoAtendimento(UsuarioSistema parametroTecnico, Date parametroInicio, Date parametroFim) {
        Compromisso novoCompromisso = new Compromisso();
        novoCompromisso.with {
            participantes.add(parametroTecnico);
            inicio = parametroInicio;
            fim = parametroFim;
            tipo = Compromisso.Tipo.ATENDIMENTO_PARTICULARIZADO;
            descricao = "atendimento particularizado";
            servicoSistemaSeguranca = segurancaService.getServicoLogado()
            habilitado = true;

            atendimentoParticularizado = new AtendimentoParticularizado();
            atendimentoParticularizado.with {
                dataHora = parametroInicio;
                servicoSistemaSeguranca = segurancaService.getServicoLogado();
                tecnico = parametroTecnico;
                compareceu = null;
            }
            atendimentoParticularizado.save();
        }
        novoCompromisso.save();
        return novoCompromisso;
    }

    @Transactional
    public AtendimentoParticularizado gravaAtendimento(AtendimentoParticularizado atendimento) {
        if (! atendimento.id)
            atendimento.servicoSistemaSeguranca = segurancaService.getServicoLogado();

        if (atendimento.semTelefone == true)
            atendimento.telefoneContato = null;

        //Atualiza 1) o inicio, 2) o fim e 3) o responsavel do compromisso correspondente ao atendimento (caso exista)
        //para coincidir com as informações deste último
        atendimento.compromisso = Compromisso.findByAtendimentoParticularizado(atendimento);
        if (atendimento.compromisso) {
            if (atendimento.dataHora && atendimento.dataHora != atendimento.compromisso.inicio) {
                atendimento.compromisso.fim.setTime(atendimento.dataHora.time +
                        (atendimento.compromisso.fim.time - atendimento.compromisso.inicio.time))
                atendimento.compromisso.inicio = atendimento.dataHora;
            }
            if (atendimento.tecnico && ! atendimento.compromisso.participantes.contains(atendimento.tecnico)) {
                atendimento.compromisso.participantes.clear();
                atendimento.compromisso.participantes.add(atendimento.tecnico);
            }
            atendimento.compromisso.save();
        }
        atendimento.save();
    }

    @Transactional
    public void gravaConfiguracao(ConfiguracaoCommand configuracao) {
        UsuarioSistema usuarioSistema = UsuarioSistema.get(segurancaService.usuarioLogado.id)
        usuarioSistema.configuracaoAgenda = (configuracao as JSON).toString();
        if (! usuarioSistemaService.gravaUsuario(usuarioSistema, null, null))
            throw new RuntimeException("Erro gravando configurações: "+usuarioSistema.errors)
    }

    public Map getConfiguracao() {
        //Valores default para as configuracoes do calenario. Ao carregar os valores default e sobrescrever com os valores
        //gravados pelo usuario caso existam, permitimos a adicao de novos parametros dinamicamente sem "quebrar" a estrutura
        //anterior, além de tratar de forma transparente os casos de usuarios sem gravacao previa das configuracoes
        Map mapaConfiguracao = [minTime: "08:00:00", atendimentos: true, outrosCompromissos: true,
                                maxTime: "20:00:00", firstDay: 3/*quarta-feira*/,
                                weekends: false];
        if (segurancaService.usuarioLogado.configuracaoAgenda) //sobrescrever com as configuracoes gravadas pelo operador
            mapaConfiguracao << new JsonSlurper().parseText(segurancaService.usuarioLogado.configuracaoAgenda);

        return mapaConfiguracao;
    }

    public getAtendimentos(Familia familia) {
        List<AtendimentoParticularizado> result = [];
        if (familia)
            result = AtendimentoParticularizado.findAllByFamilia(familia, [max: 300, sort: "dataHora", order: "desc"]);
        return result;
    }

    /**
     * Verifica no banco de dados se um dado compromisso conflita com um ou mais outros compromissos
     * pré-existentes e sinaliza com uma mensagem em compromisso.mensagem.
     * O conflito se baseia nos participantes de cada compromisso e nos horários de início e fim
     */
    public void sinalizaConflitos(Compromisso compromissoAlterado) {
        if (! compromissoAlterado)
            return;
        String hqlFrom = "select distinct a from Compromisso a left join fetch a.participantes ";
        String hqlWhere = FILTRO_COMPROMISSOS_PERIODO + " and a.id != :idCompromisso ";
        Map filtros = [servicoSistema: segurancaService.servicoLogado, idCompromisso: compromissoAlterado.id,
                       periodoInicio: compromissoAlterado.inicio, periodoFim: compromissoAlterado.fim]

        //Compromissos do periodo - aqueles que tem alguma intercecao com o horario do compromisso sendo alterado
        List<Compromisso> compromissosPeriodo = Compromisso.executeQuery(hqlFrom + hqlWhere, filtros);

        //nomesConflito - Armazenara apenas os nomes de participantes que entraram em conflito, ou "todos", na situacao A) abaixo
        Set<String> nomesConflito = [];

        //Verifica cada compromisso da intercecao a fim de detectar algum conflito entre os respectivos participantes
        compromissosPeriodo.each { compromissoPeriodo ->

            //A) os dois compromissos estao previstos para todos participarem
            if (compromissoPeriodo.participantes.size() == 0 && compromissoAlterado.participantes.size() == 0)
                nomesConflito.add("todos")
            //B) o compromisso sendo alterado é para todos e o do periodo, so para alguns
            else if (compromissoAlterado.participantes.size() == 0)
                nomesConflito.addAll(compromissoPeriodo.participantes.collect {it.username})
            //C) o compromisso no periodo é para todos e o que esta sendo alterado, so para alguns
            else if (compromissoPeriodo.participantes.size() == 0) //todos participam desse compromisso
                nomesConflito.addAll(compromissoAlterado.participantes.collect {it.username})
            //D) os dois compromissos teem participantes especificos
            else {
                //Para cada participante no compromisso sendo alterado, verifica se coincide com algum participante do compromisso no periodo
                compromissoAlterado.participantes.each { participanteCompromisso ->
                    compromissoPeriodo.participantes.each { participanteCompromissoPeriodo ->
                        if (participanteCompromisso.id == participanteCompromissoPeriodo.id)
                            nomesConflito.add(participanteCompromisso.username)
                    }
                }
            }
        }

        if (nomesConflito.size() > 0) {
            if (nomesConflito.contains("todos"))
                compromissoAlterado.mensagem = "Conflito de agenda para todos.";
            else
                compromissoAlterado.mensagem = "Conflito de agenda para "+nomesConflito.join(", ")+".";
        }
    }
}
