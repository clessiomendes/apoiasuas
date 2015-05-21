package org.apoiasuas.formulario

import org.apoiasuas.cidadao.Cidadao

/**
 * Created by admin on 19/04/2015.
 */
class FormularioBeneficioEventualService extends FormularioService {

    @Override
    protected void transfereConteudo(Formulario formulario, ReportDTO reportDTO) {
        Cidadao cidadao = formulario.cidadao
        if (!cidadao.nomeMae)
            cidadao.nomeMae = "NAO CONSTA"
        if (!cidadao.nomePai)
            cidadao.nomePai = "NAO CONSTA"
        if (!cidadao.naturalidade)
            cidadao.naturalidade = "NAO CONSTA"
        if (!cidadao.UFNaturalidade)
            cidadao.UFNaturalidade = "NAO CONSTA"
        if (!cidadao.identidade)
            cidadao.identidade = "NAO POSSUI"

        super.transfereConteudo(formulario, reportDTO)
    }

}
