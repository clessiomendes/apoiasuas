package org.apoiasuas.formulario

/**
 * Created by admin on 19/04/2015.
 */
class FormularioAcompanhamentoService extends FormularioService {

    @Override
    Formulario preparaPreenchimentoFormulario(Long idFormulario, Long idFamilia, Long idCidadao) {
        Formulario formulario = super.preparaPreenchimentoFormulario(idFormulario, idFamilia, idCidadao)
        //Atribui valores default para alguns campos
        formulario.getCampoAvulso(FormularioAcompanhamento.NOME_REFERENCIA).valorArmazenado = formulario.cidadao.familia.referencia.nomeCompleto
        formulario.getCampoAvulso(FormularioAcompanhamento.NIS_REFERENCIA).valorArmazenado = formulario.cidadao.familia.referencia.nis
        formulario.getCampoAvulso(FormularioAcompanhamento.DATA_INICIO).valorArmazenado = new Date()
        return formulario
    }

}
