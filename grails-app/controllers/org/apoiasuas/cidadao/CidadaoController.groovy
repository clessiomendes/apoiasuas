package org.apoiasuas.cidadao

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.StringUtils

import javax.servlet.http.HttpSession

@Secured([DefinicaoPapeis.STR_USUARIO])
class CidadaoController extends AncestralController {

    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Cidadao.class, only: ['show','edit', 'delete', 'update', 'save']];
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

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def procurarCidadao() {
        render(view: "procurarCidadao", model: modeloProcurarCidadao + [cidadaoInstanceList: [], cidadaoInstanceCount: 0, filtro: [:]] )
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def procurarCidadaoExecuta(FiltroCidadaoCommand filtro) {
        //Preenchimento de numeros no primeiro campo de busca indica pesquisa por codigo legado
        boolean buscaPorCodigoLegado = filtro.nomeOuCodigoLegado && ! StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCodigoLegado)
        params.max = params.max ?: 20
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }

        if (buscaPorCodigoLegado && cidadaos?.resultList?.size() > 0) {
            Cidadao cidadao = cidadaos?.resultList[0]
            redirect(controller: "familia", action: "show", id: cidadao.familia.id)
        } else
            render(view:"procurarCidadao", model: modeloProcurarCidadao + [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def abrirFamilia(Familia familiaInstance) {
        redirect(controller: 'familia', action: 'show', params: params)
//        forward controller: GrailsNameUtils.getLogicalName(FamiliaController.class, "Controller"), action: "show"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(Cidadao cidadaoInstance) {
        guardaUltimoCidadaoSelecionado(cidadaoInstance)
        render view: 'show', model: [cidadaoInstance: cidadaoInstance]
//        respond cidadaoInstance
    }

/*
    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        respond new Cidadao(params)
    }
*/

    public static Cidadao getUltimoCidadao(HttpSession session) {
        return session[SESSION_ULTIMO_CIDADAO]
    }

    public static void setUltimoCidadao(HttpSession session, Cidadao cidadao) {
        session[SESSION_ULTIMO_CIDADAO] = cidadao
    }

    def edit(Cidadao cidadaoInstance) {
        render view: 'edit', model: getEditCreateModel(cidadaoInstance);
    }

    def save(Cidadao cidadaoInstance) {
        if (! cidadaoInstance)
            return notFound()

        boolean modoCriacao = cidadaoInstance.id == null

        //Grava
        if (! cidadaoService.grava(cidadaoInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(cidadaoInstance))
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'cidadao.label', default: 'Cidadão'), cidadaoInstance.id])
        return show(cidadaoInstance)
    }

    private Map getEditCreateModel(Cidadao cidadaoInstance) {
        return [cidadaoInstance: cidadaoInstance]
    }

}

@grails.validation.Validateable
class FiltroCidadaoCommand implements Serializable {
    String nomeOuCodigoLegado
//    String segundoMembro
    String logradouro
    String numero
}
