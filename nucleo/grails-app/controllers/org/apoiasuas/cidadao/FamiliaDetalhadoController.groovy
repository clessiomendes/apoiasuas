package org.apoiasuas.cidadao

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.util.Holders
import grails.validation.ValidationException
import groovy.time.TimeCategory
import org.apoiasuas.ApoiaSuasService
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.cidadao.detalhe.CampoDetalheLookup
import org.apoiasuas.cidadao.detalhe.CampoDetalheMultiLookup
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.ApoiaSuasDateUtils
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils
import org.springframework.context.ApplicationContext

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class FamiliaDetalhadoController extends FamiliaController {

    def lookupService
    def detalheService
    ApoiaSuasService apoiaSuasService

//    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Familia.class, only: ['show','edit', 'delete', 'update', 'save']]

    def index(Integer max) {
        redirect(controller: 'cidadao', action: 'procurarCidadao')
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        if (! segurancaService.acessoRecursoServico(RecursosServico.INCLUSAO_FAMILIA))
            throw new ApoiaSuasException("O recurso de inclusão de família não está habilitado para este serviço")

        Familia novaFamilia = new Familia();
        render view: "/familia/detalhes/createEdit", model: getEditCreateModel(novaFamilia) +
                [modoCriacao: true];
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Familia familiaInstance) {
        if (AmbienteExecucao.desenvolvimento) { //sempre atualiza tabelas lookup SOMENTE EM AMBIENTE DE DESENVOLVIMENTO
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            ctx.getBean("lookupService").inicializaLookups();
        }

        if (! familiaInstance)
            return notFound()

//        familiaInstance.membros.add(new Cidadao());

        render view: "/familia/detalhes/createEdit", model: getEditCreateModel(familiaInstance) +
                [modoEdicao: true] + (params.idCidadao ? [idCidadao: params.idCidadao] : [:]);
    }

    @Override
    protected Map getEditCreateModel(Familia familiaInstance) {
        converteTipoDeficiencia(familiaInstance)
        Map result = super.getEditCreateModel(familiaInstance);
        if (segurancaService.servicoLogado.token == ServicoSistema.Tokens.CRJ) {
            result << [formFamilia: "/personalizados/familia/detalhes/familia/formFamiliaCRJ"];
            result << [formCidadao: "/personalizados/familia/detalhes/cidadao/formCidadaoCRJ"];
        }

        return result + [municipioLogado: segurancaService.getMunicipio(), UFLogada: segurancaService.getUF()];
    }

    /**
     * Converte eventual campo (desativado) tipoDeficiencia, de cada membro, para tiposDeficiencia (multi valorado)
     */
    private void converteTipoDeficiencia(Familia familiaInstance) {
        familiaInstance?.membros?.each { Cidadao cidadao ->
            CampoDetalheLookup tipoDeficiencia = cidadao.mapaDetalhes['tipoDeficiencia'];
            if (tipoDeficiencia?.notEmpty() && ! cidadao.mapaDetalhes['tiposDeficiencia']) {
                //remove informacao - FIXME: passar para save() ?
//            familiaInstance.mapaDetalhes.remove('tipoDeficiencia');
                CampoDetalheMultiLookup tiposDeficiencia = CampoDetalheMultiLookup.novo(lookupService, 'tipoDeficiencia', [tipoDeficiencia.codigo]);
                cidadao.mapaDetalhes.put('tiposDeficiencia', tiposDeficiencia);
            }
        }
    }

    /**
     * Chamado da gsp para formatar o nome de cada cidadao que aparece nas abas
     * @param cidadao
     * @return
     */
    public static String labelNome(Cidadao cidadao) {
        if (! cidadao?.nomeCompleto)
            return "(sem nome)"
        //Pegar primeiro nome
        String result = cidadao.nomeCompleto.split(" ")[0];
        //Primeira letra maiúscula
        result = StringUtils.camelCaseNomeProprio(result);
        //Referência
        if (cidadao?.referencia)
            result += " (RF)";
        return result;
    }

    /**
     * Chamado da gsp para formatar o tooltip de cada cidadao que aparece nas abas
     * @param cidadao
     * @return
     */
    public static String tooltipNome(Cidadao cidadao) {
        String result = ''
        if (cidadao.referencia)
            result = "referência: "
        else
            result = cidadao.parentescoReferencia ? cidadao.parentescoReferencia.toLowerCase()+": " : "";
        return result + StringUtils.camelCaseNomeProprio(cidadao.nomeCompleto ?: "");
    }

    /**
     * Gravação específica para quando uma família é gravada pela primeira vez
     */
    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveNew(Familia familiaInstance) {
        boolean validado = true;
        Map errosPersonalizados = [:]

        //A gravação de uma nova família embute também a gravação de um primeiro membro como referencia familiar
        familiaInstance.membros.clear();
        Cidadao novaReferenciaFamiliar = cidadaoService.novoCidadao(new Cidadao(params.cidadao), familiaInstance);

        familiaInstance.detalhes = detalheService.paramsToJson(params.detalhe);

        if (! params['nomeReferenciaFamiliar']) {
            validado = false;
            errosPersonalizados << ['erroReferencia': true]
        }
        novaReferenciaFamiliar.referencia = true;
        novaReferenciaFamiliar.nomeCompleto = params['nomeReferenciaFamiliar'];
        //Validacao: se estiver gravando também uma referencia familiar, é preciso validá-la também
        validado = novaReferenciaFamiliar.validate() && validado;

        //inicializando a familia
        familiaInstance.servicoSistemaSeguranca = segurancaService.servicoLogado;
//        familiaInstance.criador = segurancaService.usuarioLogado;
        familiaInstance.ultimoAlterador = segurancaService.usuarioLogado;
        familiaInstance.situacaoFamilia = SituacaoFamilia.CADASTRADA;

        //Validacao: valida a nova familia e telefone
        validado = familiaInstance.validate() && validado;
        validado = telefonesFromRequest(familiaInstance) && validado ;
        validado = validaVersao(familiaInstance) && validado;

        //Passou nos primeiros testes de validacao, mas ainda falta a validacao durante a gravacao
        if (validado) try {
                //Grava a familia e a referencia
                familiaInstance = familiaService.gravaNovo(familiaInstance, novaReferenciaFamiliar);
            } catch (ValidationException e) {
                log.warn(e);
                validado = false;
            }

        //Decide o proximo passo da navegacao
        if (validado) {
            guardaUltimaFamiliaSelecionada(familiaInstance);
            return render(view: '/familia/detalhes/createEdit', model: getEditCreateModel(familiaInstance) + [modoContinuarCriacao: true]);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: '/familia/detalhes/createEdit', model: getEditCreateModel(familiaInstance) +
                    [modoCriacao: true] + errosPersonalizados + [nomeReferencia: novaReferenciaFamiliar.nomeCompleto]);
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(Familia familiaInstance) {
        boolean validado = true;

        familiaInstance.detalhes = detalheService.paramsToJson(params.detalhe);

        familiaInstance.membros.each { Cidadao cidadao ->
            String paramMembro = "membros[${cidadao.ord}]";
            //ignora cidadaos que não estejam no request
            if (params[paramMembro]) {
                //Inicializa APENAS novos cidadaos
                if (! cidadao.id)
                    cidadao = cidadaoService.novoCidadao(cidadao, familiaInstance);

                dataNascimentoFromRequest(cidadao, params[paramMembro])

                //Obtem campos de detalhes do request e so converte em uma String no formato JSON
                cidadao.detalhes = detalheService.paramsToJson(params[paramMembro]["detalhe"]);

                //Validacao: valida cada membro
                validado = cidadao.validate() && validado;
            }
        }

        //Validacao: primeiro valida os dados da familia e telefones
        validado = familiaInstance.validate() && validado;
        validado = telefonesFromRequest(familiaInstance) && validado ;
        validado = validaVersao(familiaInstance) && validado;

        //Passou nos primeiros testes de validacao, mas ainda falta a validacao durante a gravacao
        if (validado) try {
                familiaInstance = familiaService.gravaFamiliaEMembros(familiaInstance);
            } catch (ValidationException e) {
                log.warn(e);
                validado = false;
            }

        //Decide o retorno JSON para a gravacao
        if (validado) {
            flash.message = "Família gravada com sucesso, cad "+familiaInstance.getCad()
            guardaUltimaFamiliaSelecionada(familiaInstance);
            //Captura os membros em um mapa [ord, id] a ser retornado para o browser em caso de sucesso na gravacao
            Map result  =  [:];
            result << [membros: familiaInstance.membros.collectEntries { [(it.ord): it.id] }];
            //informa aa pagina que chamou os novos telefones inseridos, passando o id referente a cada um
            result << [telefones: familiaInstance.telefones.collectEntries {
                String chaveNatural = (it.DDD ?: '') + (it.numero ?: ''); //unica forma de identificar o telefone submentido eh via chave natural
                return [(chaveNatural): it.id]
            }];
            return render(contentType: 'text/json', status: 200, text: result as JSON)
        } else {
            String erros = familiaService.errosParaJson(familiaInstance);
            return render(contentType:'text/json', status: 422, text: erros)
        }
    }

/**
     * Calcula valores a serem persistidos aa partir dos campos auto-excludentes dataNascimento e idadeAproximada
     */
    protected void dataNascimentoFromRequest(Cidadao cidadao, Map params) {
        if (cidadao.dataNascimento) {
            cidadao.idadeAproximada = null;
            cidadao.dataNascimentoAproximada = null;
        } else {
            //detecta eventual mudanca na idade aproximada e atualiza a data de nascimento correspondente
            String idadeAproximadaOriginal = params['idadeAproximada_original'];
            if (cidadao.idadeAproximada) {
                if (!idadeAproximadaOriginal || idadeAproximadaOriginal != cidadao.idadeAproximada.toString())
                    use(TimeCategory) {
                        cidadao.dataNascimentoAproximada = new Date() - cidadao.idadeAproximada.year
                        cidadao.dataNascimentoAproximada.clearTime();
                    }
            } else {
                cidadao.dataNascimentoAproximada = null;
            }
        }
    }

    protected def notFound() {
        flash.message = 'Erro: familia indefinida'
        return redirect(controller: 'cidadao', action: 'procurarCidadao')
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def download(Familia familiaInstance) {
        List<ReportDTO> folhasCadastro = familiaService.emiteFormularioCadastro(familiaInstance);

        response.contentType = 'application/octet-stream'
        response.setHeader('Content-disposition', "attachment; filename=\"Cadastro Familiar ${familiaInstance.cad}.docx\"");
        apoiaSuasService.appendReports(response.outputStream, folhasCadastro)
        response.outputStream.flush();
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def familiasSemCad(String nomeOuCad, String dataCriacao, String criador) {
        List<Familia> familias = familiaService.familiasSemCad.list(nomeOuCad,
                dataCriacao ? ApoiaSuasDateUtils.stringToDate(dataCriacao) : null,
                criador ? UsuarioSistema.get(criador) : null);

        return render(view:"/familia/detalhes/familiasSemCad",
                model: [familiaInstanceList: familias, familiaInstanceCount: familias.size(),
                        ususariosDisponiveis: getOperadoresOrdenadosController(true)]);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def gravaCodigoLegado(Familia familia) {
        try {
            familiaService.grava(familia, null, null, null, null);
        } catch (ValidationException e) {
            request['familiaInstance'] = familia;
        }
        return forward(action: 'familiasSemCad');
    }

}
