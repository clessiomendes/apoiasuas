package org.apoiasuas.formulario

import org.apoiasuas.bootstrap.FormularioAcompanhamento
import org.apoiasuas.bootstrap.FormularioBase
import org.apoiasuas.bootstrap.FormularioCertidoes
import org.apoiasuas.bootstrap.FormularioCertidoesPedido
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
    PLANO_ACOMPANHAMENTO(FormularioAcompanhamento.class)

    Class<FormularioBase> definicaoFormulario

    PreDefinidos(Class definicaoFormulario) {
        this.definicaoFormulario = definicaoFormulario
    }

/*
    IDENTIDADE(new Formulario(
            nome: 'Guia de identidade 2',
            descricao: 'blablabla',
            caminhoTemplate: "classpath:/org/apoiasuas/report/GuiaIdentidade-Template.docx"
            ,
            campos: [
                    new CampoFormulario(
                            codigoPropriedade: 'teste1',
                            obrigatorio: false
                    )
            ]
    ))
    FOTO("classpath:/org/apoiasuas/report/GuiaFoto-Template.docx", "Guia de foto",
            "Guia para obtenção de fotos 3x4", [
            [codigoClasse: 'familia', codigoPropriedade: 'tipoLogradouro'],
            [codigoClasse: 'familia', codigoPropriedade: 'nomeLogradouro'],
            [codigoClasse: 'familia', codigoPropriedade: 'telefones'],
            [codigoClasse: 'cidadao', codigoPropriedade: 'nomeCompleto'],
            [codigoClasse: 'cidadao', codigoPropriedade: 'nomeMae']
    ])

    Formulario formulario

    PreDefinidos(Formulario formulario) {
        this.formulario = formulario
    }

    String resource
    String nome
    String descricao
    List codigos = []

    PreDefinidos(String resource, String nome, String descricao, List codigos) {
        this.resource = resource
        this.nome = nome
        this.descricao = descricao
        this.codigos = codigos
    }
*/

}
