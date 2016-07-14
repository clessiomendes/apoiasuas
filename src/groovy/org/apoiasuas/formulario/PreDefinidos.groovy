package org.apoiasuas.formulario

import org.apoiasuas.formulario.definicao.FormularioAcompanhamento
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

    //Atenção!!! Nunca remover um enum ja usado antes em producao. Ha o risco de que registros de formularios emitidos
    //em producao que tenham o tipo de formulario sendo excluido gerem erros em tempo de execucao ao tentar instanciar
    //uma instancia de FormularioEmitido cujo formulario pre-definido ja nao consta mais neste enum.
    //Para desabilitar o formulario da tela de tipos de formularios a serem definidos, mude o atributo "habilitado" para false
    IDENTIDADE_FOTO(FormularioIdentidadeFoto.class), IDENTIDADE(FormularioIdentidade.class), FOTOS(FormularioFotos.class),
    CERTIDOES(FormularioCertidoes.class), CERTIDOES_E_PEDIDO(FormularioCertidoesPedido.class),
    ENCAMINHAMENTO(FormularioEncaminhamento.class),
    PLANO_ACOMPANHAMENTO(FormularioAcompanhamento.class, false), //GENERICO(FormularioBase.class),
    CURRICULO_EMPREGO(FormularioCurriculoEmprego.class)

    Class<FormularioBase> definicaoFormulario
    boolean habilitado

    Formulario instanciaPersistida

    PreDefinidos(Class definicaoFormulario, boolean habilitado = true) {
        this.definicaoFormulario = definicaoFormulario
        this.habilitado = habilitado
    }

}
