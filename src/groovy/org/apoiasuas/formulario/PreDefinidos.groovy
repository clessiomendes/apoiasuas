package org.apoiasuas.formulario

import org.apoiasuas.bootstrap.FormularioAcompanhamento
import org.apoiasuas.bootstrap.FormularioBase
import org.apoiasuas.bootstrap.FormularioCertidoes
import org.apoiasuas.bootstrap.FormularioCertidoesPedido
import org.apoiasuas.bootstrap.FormularioCurriculoEmprego
import org.apoiasuas.bootstrap.FormularioEncaminhamento
import org.apoiasuas.bootstrap.FormularioFotos
import org.apoiasuas.bootstrap.FormularioIdentidade
import org.apoiasuas.bootstrap.FormularioIdentidadeFoto

/**
 * Enum contendo os formularios pre-definidos do sistema com informações necessárias para suas respectivas inicializações
 */
enum PreDefinidos {

    IDENTIDADE(FormularioIdentidade.class), FOTOS(FormularioFotos.class), CERTIDOES(FormularioCertidoes.class),
    GENERICO(FormularioBase.class), IDENTIDADE_FOTO(FormularioIdentidadeFoto.class),
    CERTIDOES_E_PEDIDO(FormularioCertidoesPedido.class), ENCAMINHAMENTO(FormularioEncaminhamento.class),
    PLANO_ACOMPANHAMENTO(FormularioAcompanhamento.class), CURRICULO_EMPREGO(FormularioCurriculoEmprego.class)

    Class<FormularioBase> definicaoFormulario

    Formulario instanciaPersistida

    PreDefinidos(Class definicaoFormulario) {
        this.definicaoFormulario = definicaoFormulario
    }

}
