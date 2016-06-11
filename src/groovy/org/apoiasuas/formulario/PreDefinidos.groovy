package org.apoiasuas.formulario

import org.apoiasuas.formulario.definicao.FormularioBase
import org.apoiasuas.formulario.definicao.FormularioCertidoes
import org.apoiasuas.formulario.definicao.FormularioCertidoesPedido
import org.apoiasuas.formulario.definicao.FormularioCurriculoEmprego
import org.apoiasuas.formulario.definicao.FormularioEncaminhamento
import org.apoiasuas.formulario.definicao.FormularioFotos
import org.apoiasuas.formulario.definicao.FormularioIdentidade
import org.apoiasuas.formulario.definicao.FormularioIdentidadeFoto

/**
 * Enum contendo os formularios pre-definidos do sistema com informações necessárias para suas respectivas inicializações
 */
enum PreDefinidos {

    IDENTIDADE_FOTO(FormularioIdentidadeFoto.class), IDENTIDADE(FormularioIdentidade.class), FOTOS(FormularioFotos.class),
    CERTIDOES(FormularioCertidoes.class), CERTIDOES_E_PEDIDO(FormularioCertidoesPedido.class),
    ENCAMINHAMENTO(FormularioEncaminhamento.class),
//    PLANO_ACOMPANHAMENTO(FormularioAcompanhamento.class), GENERICO(FormularioBase.class),
    CURRICULO_EMPREGO(FormularioCurriculoEmprego.class)

    Class<FormularioBase> definicaoFormulario

    Formulario instanciaPersistida

    PreDefinidos(Class definicaoFormulario) {
        this.definicaoFormulario = definicaoFormulario
    }

}
