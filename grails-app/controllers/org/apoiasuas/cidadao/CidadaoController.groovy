package org.apoiasuas.cidadao

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils

import javax.servlet.http.HttpSession

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class CidadaoController extends AncestralController {

//    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Cidadao.class, only: ['show','edit', 'delete', 'update', 'save']];
    def beforeInterceptor = [action: this.&fetchShow, only: ['show']];
    static defaultAction = "procurarCidadao";
    private static final String SESSION_ULTIMO_CIDADAO = "SESSION_ULTIMO_CIDADAO";
    //destinos de navegação usados na busca pura de cidadãos
    public static final Map modeloProcurarCidadao = [
            controllerButtonProcurar: "cidadao",
            actionButtonProcurar: "procurarCidadaoExecuta",
            controllerLinkFamilia: "familia",
            actionLinkFamilia: "show",
            controllerLinkCidadao: "cidadao",
            actionLinkCidadao: "show"
    ];

    def cidadaoService

    /**
     * Interceptor configurado para a action show. Inicializa todas as informacoes necessarias em uma unica consulta ao banco de dados
     * @return
     */
    private boolean fetchShow(/*Class domainClass*/) {
        if (params?.id)
            cidadaoService.obtemCidadao(params.long('id'), true, true)
    }


    static List<String> getDocumentos(Cidadao cidadao) {
        List<String> result = []
        if (cidadao.nis)
            result << "NIS: "+cidadao.nis
        if (cidadao.cpf)
            result << "CPF: "+cidadao.cpf
        if (cidadao.identidade)
            result << "Identidade: "+cidadao.identidade
        if (cidadao.numeroCTPS)
            result << "CTPS: nº "+cidadao.numeroCTPS + (cidadao.serieCTPS ? " série " + cidadao.serieCTPS : "")
        return result
    }

    def procurarCidadao() {
        render(view: "procurarCidadao", model: modeloProcurarCidadao + [cidadaoInstanceList: [], cidadaoInstanceCount: 0, filtro: [:]] )
    }

    def procurarCidadaoExecuta(FiltroCidadaoCommand filtro) {
        //Preenchimento de numeros no primeiro campo de busca indica pesquisa pelo cad
        boolean buscaPorCad = filtro.nomeOuCad && ! StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCad)
        params.max = params.max ?: 20
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }

        if (buscaPorCad && cidadaos?.resultList?.size() > 0) {
            Cidadao cidadao = cidadaos?.resultList[0]
            redirect(controller: "familia", action: "show", id: cidadao.familia.id)
        } else
            render(view:"procurarCidadao", model: modeloProcurarCidadao + [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados])
    }

    def abrirFamilia(Familia familiaInstance) {
        redirect(controller: 'familia', action: 'show', params: params)
//        forward controller: GrailsNameUtils.getLogicalName(FamiliaController.class, "Controller"), action: "show"
    }

    def show(Cidadao cidadaoInstance) {
        if (! cidadaoInstance)
            return notFound()
        guardaUltimoCidadaoSelecionado(cidadaoInstance)
        render view: 'show', model: [cidadaoInstance: cidadaoInstance, podeExcluir: cidadaoService.podeExcluir(cidadaoInstance)];
//        respond cidadaoInstance
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create(Long idFamilia) {
        if (! segurancaService.acessoRecursoServico(RecursosServico.INCLUSAO_MEMBRO_FAMILIAR))
            throw new ApoiaSuasException("O recurso de inclusão de membro familiar não está habilitado para este serviço")
        if (! idFamilia) {
            Familia familiaErro = new Familia();
            familiaErro.errors.reject("erro.escolher.familia");
            return render(view: "procurarCidadao", model: modeloProcurarCidadao +
                    [cidadaoInstanceList: [], cidadaoInstanceCount: 0, filtro: [:], familiaErro: familiaErro] )
        }
        Cidadao cidadaoInstance = new Cidadao();
        cidadaoInstance.familia = Familia.get(idFamilia);
        //Verifica se é o primeiro membro e força a ser a referencia
        if (cidadaoInstance.familia.membros.count { (it.habilitado == true) && (it.referencia == true) } == 0) {
            cidadaoInstance.referencia = true;
            cidadaoInstance.parentescoReferencia = cidadaoService.PARENTESCO_REFERENCIA;
        }
        render view: "create", model: [cidadaoInstance: cidadaoInstance]
    }

    public static Cidadao getUltimoCidadao(HttpSession session) {
        return session[SESSION_ULTIMO_CIDADAO]
    }

    public static void setUltimoCidadao(HttpSession session, Cidadao cidadao) {
        session[SESSION_ULTIMO_CIDADAO] = cidadao
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Cidadao cidadaoInstance) {
        render view: 'edit', model: getEditCreateModel(cidadaoInstance);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(Cidadao cidadaoInstance) {
        if (! cidadaoInstance)
            return notFound()

        boolean modoCriacao = cidadaoInstance.id == null;

        if (modoCriacao) {
            cidadaoInstance.servicoSistemaSeguranca = segurancaService.servicoLogado;
            cidadaoInstance.criador = segurancaService.usuarioLogado;
            cidadaoInstance.habilitado = true;
        }
        cidadaoInstance.ultimoAlterador = segurancaService.usuarioLogado;

        //Grava
        if (cidadaoInstance.validate()) {
            cidadaoService.grava(cidadaoInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(cidadaoInstance))
        }

        flash.message = "Membro familiar ${cidadaoInstance.nomeCompleto} gravado com sucesso";
        guardaUltimoCidadaoSelecionado(cidadaoInstance)
        if (modoCriacao) //volta para a tela da familia que chamou a tela atual
            forward(controller: 'familia', action: 'show', id: cidadaoInstance.familia.id)
        else
            render view: 'show', model: [cidadaoInstance: cidadaoInstance, podeExcluir: cidadaoService.podeExcluir(cidadaoInstance)];
    }

    private Map getEditCreateModel(Cidadao cidadaoInstance) {
        return [cidadaoInstance: cidadaoInstance]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def desabilitar(Cidadao cidadaoInstance) {
        if (! cidadaoInstance)
            return notFound()

        cidadaoInstance.habilitado = false;
        cidadaoService.grava(cidadaoInstance)
        flash.message = "${cidadaoInstance.nomeCompleto} removido do grupo familiar";
        redirect controller: "familia", action: "show", id: cidadaoInstance.familia.id
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def reabilitar(Cidadao cidadaoInstance) {
        if (! cidadaoInstance)
            return notFound()

        cidadaoInstance.habilitado = true;
        cidadaoService.grava(cidadaoInstance)
        flash.message = "${cidadaoInstance.nomeCompleto} reintegrado ao grupo familiar";
        redirect controller: "familia", action: "show", id: cidadaoInstance.familia.id
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'cidadao.label', default: 'Cidadão'), params.id])
        return redirect(action: "procurarCidadao")
    }

}

@grails.validation.Validateable
class FiltroCidadaoCommand implements Serializable {
    String nomeOuCad
//    String segundoMembro
    String logradouro
    String numero
}
