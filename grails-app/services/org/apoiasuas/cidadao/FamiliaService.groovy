package org.apoiasuas.cidadao

import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.AcaoFamilia
import org.apoiasuas.marcador.AssociacaoMarcador
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.OutroMarcadorFamilia
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.marcador.VulnerabilidadeFamilia
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.marcador.Programa
import org.apoiasuas.marcador.ProgramaFamilia
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.CollectionUtils

@Transactional(readOnly = true)
class FamiliaService {

    public static final int MAX_AUTOCOMPLETE_LOGRADOUROS = 10
    public static final String TEMPLATE_PLANO_ACOMPANHAMENTO = "templatePlanoAcompanhamento.docx";

    def segurancaService
    def pedidoCertidaoProcessoService
    def messageSource
    def marcadorService

    @Transactional
    public Familia grava(Familia familia, MarcadoresCommand programasCommand, MarcadoresCommand acoesCommand,
                         MarcadoresCommand vulnerabilidadesCommand, MarcadoresCommand outrosMarcadoresCommand)
    {
        marcadorService.gravaMarcadoresFamilia(programasCommand, familia.programas, familia, Programa.class, ProgramaFamilia.class);
        marcadorService.gravaMarcadoresFamilia(vulnerabilidadesCommand, familia.vulnerabilidades, familia, Vulnerabilidade.class, VulnerabilidadeFamilia.class);
        marcadorService.gravaMarcadoresFamilia(acoesCommand, familia.acoes, familia, Acao.class, AcaoFamilia.class);
        marcadorService.gravaMarcadoresFamilia(outrosMarcadoresCommand, familia.outrosMarcadores, familia, OutroMarcador.class, OutroMarcadorFamilia.class);

        return familia.save()
    }

    @Transactional
    public boolean apaga(Familia familia) {
        familia.delete()
        return true
    }

    @Transactional(readOnly = true)
    List procurarLogradouros(String logradouro) {
        if (!logradouro)
            return []
        //HQL Busca todos os logradouros, primeiro os mais usados
        String hql = 'select a.endereco.nomeLogradouro from Familia a ' +
                'where lower(remove_acento(a.endereco.nomeLogradouro)) like remove_acento(:logradouro) ' +
                'group by a.endereco.nomeLogradouro ' +
                'order by count(*) desc ';
        String logradouroInicia = logradouro.toLowerCase() + '%'
        String logradouroContem = '%' + logradouro.toLowerCase() + '%'

        //Procura logradouros INICIANDO com o texto digitado
        //Usar um LinkedHashSet garante que os resultados nao se repitam
        Set logradouros = new LinkedHashSet(Familia.executeQuery(hql, [logradouro: logradouroInicia]))

        //se existem menos de 10, procura logradouros CONTENDO o texto digitado
        if (logradouros.size() < MAX_AUTOCOMPLETE_LOGRADOUROS) {
            Iterator<String> logradourosContem = Familia.executeQuery(hql, [logradouro: logradouroContem]).iterator()
            while (logradouros.size() < MAX_AUTOCOMPLETE_LOGRADOUROS && logradourosContem.hasNext())
                logradouros << logradourosContem.next()
        }

        return new ArrayList(logradouros)

    }

    @Transactional(readOnly = true)
    public boolean testaAcessoDominio(Familia familia) {
        //Restringir acesso apenas ao servicoSistema que criou a familia
        if (familia.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                familia.servicoSistemaSeguranca.id != segurancaService.getServicoLogado().id)
            return false
        return true;
    }

    public Set<String> getNotificacoes(Long idFamilia, Locale locale) {
        if (!idFamilia)
            return []
        Set<String> result = []
        Familia familia = Familia.get(idFamilia);

        //testa se a familia eh acompanhada por algum tecnico
        if (familia.tecnicoReferencia)
            result << messageSource.getMessage("notificacao.familia.acompanhada", [familia.tecnicoReferencia.username].toArray(), locale);

        //testa idades voltadas ao SCFV
        familia.membros.each { Cidadao cidadao ->
            if (cidadao.idade && cidadao.idade < 7)
                result << messageSource.getMessage("notificacao.familia.SCFV.0a6", null, locale);
            if (cidadao.idade && cidadao.idade >= 60)
                result << messageSource.getMessage("notificacao.familia.SCFV.idosos", null, locale);
        }

        List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes = pedidoCertidaoProcessoService.pedidosCertidaoPendentes(familia.id)
        if (pedidosCertidaoPendentes)
            result << messageSource.getMessage("notificacao.familia.pedidosCertidao", null, locale);

        return result
    }

    @Transactional(readOnly = true)
    public ReportDTO emitePlanoAcompanhamento(Familia familia) {
        ReportDTO result = new ReportDTO();
        result.nomeArquivo = "PlanoAcompanhamentoCad${familia.codigoLegado}.docx"

// 1) Load doc file and set Velocity template engine and cache it to the registry
        InputStream stream = this.class.getResourceAsStream(TEMPLATE_PLANO_ACOMPANHAMENTO)
//        InputStream stream = new ByteArrayInputStream(template2);
        result.report = XDocReportRegistry.getRegistry().loadReport(stream, TemplateEngineKind.Velocity);

// 2) Create Java model context
        result.context = result.report.createContext();
        result.fieldsMetadata = result.report.createFieldsMetadata();

        UsuarioSistema tecnico = familia.tecnicoReferencia;
        String tecnicoReferencia = "";
        if (tecnico)
            tecnicoReferencia = tecnico.nomeCompleto + (tecnico.matricula ? " ($tecnico.matricula)" : "")
        //define um mapa de pares chave/conteudo cujas CHAVES são buscadas como FIELDS no template do word e substiuídas
        // pelo conteúdo correspondente. Esse mapa é transferido na sequência para o CONTEXTO do mecanismo de geração do .doc
        [
                cras               : familia.servicoSistemaSeguranca?.nome,
                codigo_legado      : familia.codigoLegado,
                referencia_familiar: familia.getReferencia()?.nomeCompleto,
                nis_referencia     : familia.getReferencia()?.nis,
                tecnico_referencia : tecnicoReferencia,
                composicao_familiar: membrosToString(familia),
                endereco           : familia.endereco?.enderecoCompleto,
                ingresso           : familia.acompanhamentoFamiliar?.dataInicio?.format("dd/MM/yyyy"),
                encerramento       : familia.acompanhamentoFamiliar?.dataFim?.format("dd/MM/yyyy"),
                analise_tecnica    : familia.acompanhamentoFamiliar?.analiseTecnica,
                resultados         : familia.acompanhamentoFamiliar?.resultados,
                telefones          : familia.telefonesToString,
                vulnerabilidades   : marcadoresToString(familia.vulnerabilidadesHabilitadas, "\n", "- "),
                programas          : marcadoresToString(familia.programasHabilitados, "\n", "- "),
                acoes              : marcadoresToString(familia.acoesHabilitadas, "\n", "- "),
                monitoramentos     : monitoramentosToString(familia.monitoramentos)
        ].each { chave, conteudo ->
            result.context.put(chave, conteudo);
        }
        return result;
    }

    /**
     * Devolve uma lista de membros familiares, com o separador de campos e de linhas especificado
     */
    private String membrosToString(Familia familia, String separadorCampos = ", ", String separadorLinhas = "\n") {
        List<String> dadosCidadao = [];
        familia.membrosOrdemAlfabetica.each { cidadao ->
            if (cidadao.nomeCompleto)
                dadosCidadao << CollectionUtils.join(["- " + cidadao.nomeCompleto,
                                                      cidadao.parentescoReferencia,
                                                      cidadao.dataNascimento ? "nascimento: " + cidadao.dataNascimento.format("dd/MM/YYYY") : null
                ], separadorCampos);
        };
        return CollectionUtils.join(dadosCidadao, separadorLinhas);
    }

    private String marcadoresToString(Set<AssociacaoMarcador> associacaoMarcadores, String separador = ", ", String bulletList = "") {
        return CollectionUtils.join(associacaoMarcadores
                .sort { it.marcador.descricao?.toLowerCase() }
                .collect {
                    bulletList + it.marcador.descricao + (it.observacao ? ' - obs: '+it.observacao : '')
                }, separador)
    }

    private String monitoramentosToString(Set<Monitoramento> monitoramentos) {
        return CollectionUtils.join(monitoramentos
                .sort { it.dataCriacao }
                .collect {
            it.memo + "\n-> " + it.situacao
        }, "\n----------------\n")
    }

}