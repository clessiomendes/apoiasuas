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

        CampoFormulario tecnico = formulario.getCampoAvulso(CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO)
//        CampoFormulario matricula = formulario.getCampoAvulso(.CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO)
        //Substitui , no campo RESPONSAVEL_PREENCHIMENTO, o ID pelo nome completo do usuario
        tecnico.valorArmazenado = formulario.usuarioSistema?.nomeCompleto
//        matricula.valorArmazenado = formulario.usuarioSistema?.matricula

        /*
        FIXME O plugin Melody (para monitoramento e profilling) tem um bug: quando invocamos o metodo super o plguin
        acaba direcionando para o proprio método, em uma recursão infinita, ocasionando um stackoverflowError
        */
        super.transfereConteudo(formulario, reportDTO)
    }

}
