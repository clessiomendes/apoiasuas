package org.apoiasuas.formulario

import org.apoiasuas.formulario.definicao.FormularioBase
import org.apoiasuas.formulario.definicao.FormularioCarreto
import org.apoiasuas.formulario.definicao.FormularioCertidoes
import org.apoiasuas.formulario.definicao.FormularioCertidoesPedido
import org.apoiasuas.formulario.definicao.FormularioCurriculoEmprego
import org.apoiasuas.formulario.definicao.FormularioEncaminhamento
import org.apoiasuas.formulario.definicao.FormularioFotos
import org.apoiasuas.formulario.definicao.FormularioIdentidade
import org.apoiasuas.formulario.definicao.FormularioIdentidadeFoto
import org.apoiasuas.formulario.definicao.FormularioPasseLivrePCD
import org.apoiasuas.formulario.definicao.FormularioSepultamento

/**
 * Enum contendo os formularios pre-definidos do sistema com informações necessárias para suas respectivas inicializações
 */
enum PreDefinidos {

    //Atenção!!! Nunca remover um enum ja usado antes em producao. Ha o risco de que registros de formularios emitidos
    //em producao que tenham o tipo de formulario sendo excluido gerem erros em tempo de execucao ao tentar instanciar
    //uma instancia de FormularioEmitido cujo formulario pre-definido ja nao consta mais neste enum.
    //Para desabilitar o formulario da tela de tipos de formularios a serem definidos, mude o atributo "habilitado" para false
    IDENTIDADE(FormularioIdentidade.class, 1),
    FOTOS(FormularioFotos.class, 2),
    IDENTIDADE_FOTO(FormularioIdentidadeFoto.class, 3),
    CARRETO(FormularioCarreto.class, 4),
    SEPULTAMENTO(FormularioSepultamento.class, 5),
    CERTIDOES(FormularioCertidoes.class, 6),
    CERTIDOES_E_PEDIDO(FormularioCertidoesPedido.class, 7),
    ENCAMINHAMENTO(FormularioEncaminhamento.class, 8),
    CURRICULO_EMPREGO(FormularioCurriculoEmprego.class, 9),
    PASSE_LIVRE_PCD(FormularioPasseLivrePCD.class, 10),
    //GENERICO(FormularioBase.class),
    PLANO_ACOMPANHAMENTO(null, 999, false) //DESATIVADO

    Class<FormularioBase> definicaoFormulario
    boolean habilitado
    int ordem

    Formulario instanciaPersistida

    PreDefinidos(Class definicaoFormulario, int ordem, boolean habilitado = true) {
        this.definicaoFormulario = definicaoFormulario
        this.habilitado = habilitado
        this.ordem = ordem;
    }

    int getOrdem() {
        return ordem;
    }

}
