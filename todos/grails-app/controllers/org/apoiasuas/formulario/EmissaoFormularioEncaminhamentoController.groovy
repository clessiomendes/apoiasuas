package org.apoiasuas.formulario

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.cidadao.*
import org.apoiasuas.formulario.definicao.FormularioBase
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.redeSocioAssistencial.Servico
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.text.ParseException
import java.text.SimpleDateFormat

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class EmissaoFormularioEncaminhamentoController extends EmissaoFormularioController {

    @Override
    protected void antesGerarFormularioPreenchido(Formulario formulario, FormularioEmitido formularioEmitido, Map params) {
        formulario.anexarFichaNoEncaminhamento = params.containsKey('anexoFichaServico');
        if (params.idServico?.isLong()) {
            formularioEmitido.servicoDestino = Servico.load(new Long(params.idServico));
            if (! formularioEmitido.servicoDestino)
                throw new ApoiaSuasException('Serviço não encontrado com id '+params.idServicoDestino)
        }
    }

    @Override
    protected String getTemplateCamposCustomizados(Formulario formulario) {
        return "/emissaoFormulario/formularioEncaminhamento";
    }

/*
    @Override
    def exibirPreencherFormulario(Long idFormulario, Long idServico */
/*preenchido apenas quando vindo do cdu de servico socio assistencial*//*
,
                                  Long membroSelecionado, Long familiaSelecionada) {
        render(view: '/emissaoFormulario/preencherFormulario',
                model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario),
                        dtoFormulario: formulario,
                        idServico: idServico,
                        tecnicos: getTecnicosOrdenadosController(true) ])
    }
*/


}


