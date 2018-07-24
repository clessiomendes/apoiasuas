package org.apoiasuas.formulario

import org.apoiasuas.formulario.definicao.FormularioCarreto
import org.apoiasuas.redeSocioAssistencial.Servico

class FormularioEncaminhamentoService extends FormularioService {

    def servicoService;

    @Override
    public List<ReportDTO> prepararImpressao(Formulario formulario, Long idModelo) {
        List<ReportDTO> result = super.prepararImpressao(formulario, idModelo);
        if (formulario.anexarFichaNoEncaminhamento && formulario.formularioEmitido.servicoDestino)
            result << servicoService.imprimir(formulario.formularioEmitido.servicoDestino)
        return result;
    }

    @Override
    protected FormularioEmitido registraEmissao(Formulario formulario) {
        FormularioEmitido result = super.registraEmissao(formulario);
        if (formulario?.formularioEmitido?.servicoDestino
                //evita registrar duas vezes a memsa emissao de encaminhamento
                && formulario.formularioEmitido.version == 0L)
            servicoService.registraEstatisticaEncaminhamento(formulario.formularioEmitido.servicoDestino)
        return result;
    }

}
