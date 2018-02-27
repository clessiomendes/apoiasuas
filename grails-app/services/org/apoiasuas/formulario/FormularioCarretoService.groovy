package org.apoiasuas.formulario

import org.apoiasuas.formulario.definicao.FormularioCarreto

/**
 * Created by admin on 19/04/2015.
 */
class FormularioCarretoService extends FormularioService {

    @Override
    Formulario preparaPreenchimentoFormulario(Long idFormulario, Long idFamilia, Long idCidadao) {
        Formulario formulario = super.preparaPreenchimentoFormulario(idFormulario, idFamilia, idCidadao);
        String endereco = formulario?.cidadao?.familia?.endereco.obtemEnderecoCompleto();
        formulario.getCampoAvulso(FormularioCarreto.CODIGO_ORIGEM).setValorArmazenado(endereco);
        return formulario
    }


}
