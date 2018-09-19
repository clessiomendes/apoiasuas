package org.apoiasuas.pedidocertidao

import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.CollectionUtils
import org.apoiasuas.util.HqlPagedResultList
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.SQLQuery
import org.hibernate.Session

@Transactional(readOnly = true)
class PedidoCertidaoService {

    def formularioService;
    def segurancaService;
    def sessionFactory

    @Transactional
    public PedidoCertidao grava(PedidoCertidao pedido) {
        if (! pedido.id) //nova
            pedido = novaSituacao(pedido, PedidoCertidao.Situacao.NOVO_PEDIDO)

        return pedido.save();
    }

    @Transactional
    public PedidoCertidao novaSituacao(PedidoCertidao pedido, PedidoCertidao.Situacao acao) {
        if (acao != PedidoCertidao.Situacao.NOVO_PEDIDO && ! pedido.situacao.acoesPossiveis.contains(acao))
            throw new ApoiaSuasException("Impossível mudar pedido de certidão id ${pedido.id} de ${pedido.situacao} para ${acao} ");

        pedido.situacao = acao;
        novoHistorico(acao, pedido, acao.descricao);
        return pedido;
    }

    @Transactional
    public PedidoCertidao desfazer(PedidoCertidao pedido) {
        PedidoCertidao.Situacao acao = PedidoCertidao.Situacao.DESFAZER;

        String descricao = acao.descricao;
        PedidoCertidao.Situacao novaSituacao;
        PedidoCertidao.Situacao situacaoAtual = pedido.situacao;

        novaSituacao = PedidoCertidao.Situacao.NOVO_PEDIDO;
        pedido.historico.sort { it.id }.each {
            if (it.acao
                    && it.acao != PedidoCertidao.Situacao.DESFAZER
                    //uma acao que preceda a acao atual
                    && it.acao.acoesPossiveis.contains(situacaoAtual))
                novaSituacao = it.acao;
        }
        descricao = acao.descricao + ": " + situacaoAtual.descricao;

        pedido.situacao = novaSituacao;
        novoHistorico(acao, pedido, descricao);
        return pedido;
    }

    @Transactional
    public PedidoCertidao inserirComentario(PedidoCertidao pedido, String descricao) {
        novoHistorico(null, pedido, descricao);
        return pedido;
    }

    private void novoHistorico(PedidoCertidao.Situacao acao, PedidoCertidao pedido, String descricao) {
        HistoricoPedidoCertidao novoHistorico = new HistoricoPedidoCertidao(
                operador: segurancaService.usuarioLogado,
                acao: acao,
                dataHora: new Date(),
                descricao: descricao,
                pedido: pedido
        )
        pedido.historico.add(novoHistorico);
        pedido.save();
        novoHistorico.save();
    }


    @Transactional(readOnly = true)
    public ReportDTO imprimirDeclaracaoPobreza(PedidoCertidao pedido) {
        ReportDTO result = preImprimir(PreDefinidos.CERTIDOES);
        preencheCampos(pedido, result);
        return result;
    }

    @Transactional(readOnly = true)
    public ReportDTO imprimirPedidoCartorio(PedidoCertidao pedido) {
        ReportDTO result = preImprimir(PreDefinidos.CERTIDOES_E_PEDIDO);
        preencheCampos(pedido, result);
        return result;
    }

    private void preencheCampos(PedidoCertidao pedido, ReportDTO report) {
        ServicoSistema servicoLogado = segurancaService.servicoLogado;
        UsuarioSistema tecnico = pedido.operadorResponsavel;
        String tecnicoReferencia = "";
        if (tecnico)
            tecnicoReferencia = tecnico.nomeCompleto + (tecnico.matricula ? " ($tecnico.matricula)" : "")

        //define um mapa de pares chave/conteudo cujas CHAVES são buscadas como FIELDS no template do word e substiuídas
        // pelo conteúdo correspondente. Esse mapa é transferido na sequência para o CONTEXTO do mecanismo de geração do .doc
        [
                ('Cidadao.nome_completo')    : pedido.nomeSolicitante,
                ('Avulso.nacionalidade')     : pedido.nacionalidadeSolicitante,
                ('Avulso.profissao')         : pedido.profissaoSolicitante,
                ('Cidadao.estado_civil')     : pedido.estadoCivilSolicitante,
                ('Avulso.uniao_estavel')     : pedido.uniaoEstavelSolicitante?.descricao,
                ('Avulso.nome_convivente')   : pedido.conviventeSolicitante,
                ('Cidadao.identidade')       : pedido.identidadeSolicitante,
                ('Cidadao.cpf')              : pedido.cpfSolicitante,

                //USANDO O CAMPO 'TIPO_LOGRADOURO' PARA RECEBER O ENDEREÇO INTEIRO
                ('Endereco.tipo_logradouro') : CollectionUtils.join([pedido.enderecoSolicitante,
                                                                     pedido.municipioSolicitante,
                                                                     pedido.ufSolicitante], ", "),

                ('Cidadao.nome_pai')         : pedido.paiSolicitante,
                ('Cidadao.nome_mae')         : pedido.maeSolicitante,
                ('Familia.telefone')         : servicoLogado?.telefone,
                ('Avulso.nome_registro')     : pedido.nomeRegistro,

                ('Avulso.nome_equipamento'): servicoLogado.nome,
                ('Avulso.endereco_equipamento'): servicoLogado?.endereco?.obtemEnderecoCompleto(),
                ('Avulso.cidade_equipamento'): servicoLogado?.endereco?.municipio,
                ('Avulso.uf_equipamento')    : servicoLogado?.endereco?.UF,
                ('Avulso.telefone_equipamento') : servicoLogado?.telefone,
                ('Avulso.email_equipamento') : servicoLogado?.email,

                //USANDO O CAMPO 'NOME_CARTORIO' PARA RECEBER NOME, BAIRRO E MUNICIPIO
//                ('Avulso.nome_cartorio')     : CollectionUtils.join([pedido.nomeCartorio,
//                                                                     pedido.bairroCartorio,
//                                                                     pedido.municipioCartorio,
//                                                                     pedido.ufCartorio], ", "),
                ('Avulso.nome_cartorio'): pedido.nomeCartorio,
                ('Avulso.endereco_cartorio'): pedido.enderecoCartorio,
                ('Avulso.bairro_distrito_cartorio'): pedido.bairroCartorio,
                ('Avulso.municipio_cartorio'): pedido.municipioCartorio,
                ('Avulso.uf_cartorio'): pedido.ufCartorio,
                ('Avulso.cep_cartorio'): pedido.cepCartorio,
                ('Avulso.observacoes'): pedido.observacoesCartorio,

                ('Avulso.tipo_certidao')     : pedido.tipoCertidao?.descricao,
                ('Avulso.data_registro')     : pedido.dataRegistro?.format("dd/MM/yyyy"),
                ('Avulso.livro')             : pedido.livro,
                ('Avulso.folha')             : pedido.folha,
                ('Avulso.termo')             : pedido.termo,

                ('Avulso.data_preenchimento'): new Date().format("dd/MM/yyyy"),
                ('Avulso.responsavel_preenchimento'): tecnicoReferencia,
                ('Avulso.matricula'): tecnico?.matricula,

        ].each { chave, conteudo ->
            report.context.put(chave, conteudo);
        }
    }

    private ReportDTO preImprimir(PreDefinidos preDefinido) {
        ReportDTO result = new ReportDTO();

// 1) Load doc file and set Velocity template engine and cache it to the registry
        Formulario formulario = formularioService.getFormularioPreDefinido(preDefinido);
        result.nomeArquivo = formulario.geraNomeArquivo();
        InputStream template = new ByteArrayInputStream(formulario.modeloPadrao.arquivo);
        result.report = XDocReportRegistry.getRegistry().loadReport(template, TemplateEngineKind.Velocity);

// 2) Create Java model context
        result.context = result.report.createContext();
        result.fieldsMetadata = result.report.createFieldsMetadata();

        return result;
    }

    public PagedResultList procurar(GrailsParameterMap params, FiltroPedidoCertidaoCommand filtrosOperador) {
        String filtroNome
        String filtroCad
        if (filtrosOperador?.nomeOuCad) {
            if (StringUtils.PATTERN_TEM_LETRAS.matcher(filtrosOperador.nomeOuCad))
                filtroNome = filtrosOperador.nomeOuCad
            else
                filtroCad = filtrosOperador.nomeOuCad
        }
        def filtrosSql = [:]

        String sqlSelectCount = "select count(*) ";
        String sqlSelectList = "select distinct {a.*}, {b.*}, {c.*} ";
        String sqlFrom = " FROM pedidocertidao.pedido_certidao a LEFT OUTER JOIN familia b ON a.familia_id=b.id " +
                " LEFT OUTER JOIN usuario_sistema c ON a.operador_responsavel_id = c.id ";

//================= FILTRAGEM ==============

        String sqlWhere = ' where 1=1 and a.servico_sistema_seguranca_id = :servicoSistema'
        filtrosSql << [servicoSistema: segurancaService.getServicoLogado()]

        if (filtrosOperador.cartorioOuMinicipio) {
            sqlWhere += ' and ( lower(remove_acento(a.nome_cartorio)) like remove_acento(:cartorio_municipio) ' +
                    ' or lower(remove_acento(a.bairro_cartorio)) like remove_acento(:cartorio_municipio) ' +
                    ' or lower(remove_acento(a.municipio_cartorio)) like remove_acento(:cartorio_municipio) )';
            filtrosSql.put('cartorio_municipio', '%'+filtrosOperador.cartorioOuMinicipio.toLowerCase()+'%');
        }

        if (filtrosOperador.cartorioIndefinido) {
            sqlWhere += ' and (a.nome_cartorio is null or a.municipio_cartorio is null or a.uf_cartorio is null or a.endereco_cartorio is null) ';
        }

        if (filtrosOperador.situacao) {
            sqlWhere += ' and a.situacao = :situacao';
            filtrosSql.put('situacao', filtrosOperador.situacao.name())
        }

        if (filtrosOperador.responsavel) {
            sqlWhere += ' and a.operador_responsavel_id = :responsavel';
            filtrosSql.put('responsavel', filtrosOperador.responsavel.id)
        }

        if (filtroCad) {
            if (segurancaService.identificacaoCodigoLegado) {
                sqlWhere += ' and b.codigo_legado = :cad'
                filtrosSql << [cad: filtroCad]
            } else {
                sqlWhere += ' and b.id = :cad'
                filtrosSql << [cad: filtroCad.toLong()]
            }
        }

        String[] nomes = filtroNome?.split(" ");
        nomes?.eachWithIndex { nome, i ->
            String label = 'nome'+i
            sqlWhere += " and ( lower(remove_acento(a.nome_solicitante)) like remove_acento(:"+label+") " +
                    " or lower(remove_acento(a.nome_registro)) like remove_acento(:"+label+") )"
            filtrosSql.put(label, '%'+nome?.toLowerCase()+'%')
        }


//================= ORDENACAO ==============

        String sqlOrder = ' order by a.id desc ';

//================= EXECUCAO ==============

        Session sess = sessionFactory.getCurrentSession();
        SQLQuery queryCount = sess.createSQLQuery(sqlSelectCount + sqlFrom + sqlWhere);

        SQLQuery queryList = sess.createSQLQuery(sqlSelectList + sqlFrom  + sqlWhere + sqlOrder)
                .addEntity("a", PedidoCertidao.class)
                .addJoin("b", "a.familia") //eager join for better performance
                .addJoin("c", "a.operadorResponsavel"); //eager join for better performance
        queryList.setFirstResult(params.offset ? new Integer(params.offset) : 0);
        queryList.setMaxResults(params.max ? new Integer(params.max) : 20);

        filtrosSql.each { key, value ->
            queryCount.setParameter(key, value);
            queryList.setParameter(key, value);
        }
        Integer count = queryCount.uniqueResult();
        List resultado = queryList.list();
        List<PedidoCertidao> pedidos = [];

        Iterator<PedidoCertidao> iterator = resultado.iterator()
        while (iterator.hasNext()) {
            PedidoCertidao pedido = iterator.next()[0] //seleciona o cidadao e ignora o resto
            pedidos << pedido;
        }
        return new HqlPagedResultList(pedidos, count)

//==============================================

    }

    public List<PedidoCertidao> getPedidosPendentes(Familia familia) {
        return PedidoCertidao.where {
                   familia == familia &&
                   servicoSistemaSeguranca == segurancaService.servicoLogado &&
                   situacao in PedidoCertidao.Situacao.PENDENTES
        }.findAll();
    }
}
