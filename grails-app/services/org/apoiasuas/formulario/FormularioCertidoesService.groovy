package org.apoiasuas.formulario

import org.apoiasuas.formulario.definicao.FormularioCertidoes
import org.apoiasuas.formulario.definicao.FormularioCertidoesPedido
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.util.StringUtils

/**
 * Created by admin on 19/04/2015.
 */
class FormularioCertidoesService extends FormularioService {

    public static final String NACIONALIDADE_PADRAO = "brasileira"
    def pedidoCertidaoProcessoService

/*
    @Override
    Formulario preparaPreenchimentoFormulario(Long idFormulario, Long idFamilia, Long idCidadao) {
        Formulario formulario = super.preparaPreenchimentoFormulario(idFormulario, idFamilia, idCidadao)

        //Atribui valores default para alguns campos
        formulario.getCampoAvulso(Cidadao.CODIGO_NACIONALIDADE).setValorArmazenado(NACIONALIDADE_PADRAO);
        return formulario
    }
*/

    /**
     * Gera um processo de pedido de certidao de nascimento.
     */
    @Override
    protected void eventoPosEmissao(Formulario formulario) {
        if (formulario.formularioPreDefinido == PreDefinidos.CERTIDOES_E_PEDIDO && segurancaService.acessoRecursoServico(RecursosServico.PEDIDOS_CERTIDAO)) {
            String cartorio = StringUtils.concatena(", ", formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_NOME_CARTORIO),
                    formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_BAIRRO_DISTRITO_CARTORIO),
                    formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_MUNICIPIO_CARTORIO),
                    formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_UF_CARTORIO));

            String observacoesInternas = formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_OBSERVACOES_INTERNAS);

//            CertidoesDTO dto = new CertidoesDTO(formulario)
            //TODO: definir tipo de certidão
            pedidoCertidaoProcessoService.novoProcesso(formulario.usuarioSistema,
                    formulario.familia?.id,
                    formulario.usuarioSistema.id,
                    /*defineTipoCertidao(dto) +*/ "Certidão em nome de " + formulario.getConteudoCampo(FormularioCertidoes.CODIGO_NOME_REGISTRO),
                    formulario.formularioEmitido.id,
                    cartorio, null/*sem AR por enquanto*/, observacoesInternas)
        }
    }

}
