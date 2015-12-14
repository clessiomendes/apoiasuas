package org.apoiasuas.formulario

import grails.transaction.Transactional
import org.apoiasuas.bootstrap.FormularioCertidoes
import org.apoiasuas.bootstrap.FormularioCertidoesPedido
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.StringUtils

/**
 * Created by admin on 19/04/2015.
 */
class FormularioCertidoesService extends FormularioService {

    @Override
    Formulario preparaPreenchimentoFormulario(Long idFormulario, Long idFamilia, Long idCidadao) {
        Formulario formulario = super.preparaPreenchimentoFormulario(idFormulario, idFamilia, idCidadao)
        //Atribui valores default para alguns campos
        formulario.getCampoAvulso(FormularioCertidoes.CODIGO_NOME_NASCIMENTO).valorArmazenado = formulario.cidadao.nomeCompleto
        formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_NASCIMENTO).valorArmazenado = formulario.cidadao.dataNascimento
        formulario.getCampoAvulso(FormularioCertidoes.CODIGO_MAE_NASCIMENTO).valorArmazenado = formulario.cidadao.nomeMae
        formulario.getCampoAvulso(FormularioCertidoes.CODIGO_PAI_NASCIMENTO).valorArmazenado = formulario.cidadao.nomePai
        return formulario
    }

    @Override
    public void gravarAlteracoes(Formulario formulario) {
        //Por via das duvidas, nao gravar alteracoes pois ha o risco de se substituir dados do cidadao no cadastro pelo do declarante no formulario
        //TODO https://github.com/clessiomendes/apoiasuas/issues/17
    }

    @Override
    protected void transfereConteudo(Formulario formulario, ReportDTO reportDTO) {
        Cidadao cidadao = formulario.cidadao
        if (cidadao.identidade)
            cidadao.identidade = "Cart. Identidade "+cidadao.identidade
        if (cidadao.numeroCTPS) {
            cidadao.numeroCTPS = "CTPS Nº " + cidadao.numeroCTPS
            cidadao.serieCTPS = "Série " + cidadao.serieCTPS
        }
        if (cidadao.cpf)
            cidadao.cpf = "CPF "+cidadao.cpf
        if (cidadao.estadoCivil)
            cidadao.estadoCivil = "Estado Civil "+cidadao.estadoCivil
        if (cidadao.familia.endereco.complemento)
            cidadao.familia.endereco.complemento = " Complemento: "+cidadao.familia.endereco.complemento
        if (cidadao.familia.endereco.bairro)
            cidadao.familia.endereco.bairro = " Bairro: "+cidadao.familia.endereco.bairro

        CampoFormulario nomeNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_NOME_NASCIMENTO)
        CampoFormulario dataNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_NASCIMENTO)
        CampoFormulario maeNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_MAE_NASCIMENTO)
        CampoFormulario paiNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_PAI_NASCIMENTO)
        CampoFormulario conjuge1 = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_CONJUGE_1)
        CampoFormulario conjuge2 = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_CONJUGE_2)
        CampoFormulario dataCasamento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_CASAMENTO)
        CampoFormulario nomeFalecido = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_NOME_FALECIDO)
        CampoFormulario dataFalecimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_FALECIMENTO)
        CampoFormulario maeObito = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_MAE_OBITO)
        CampoFormulario paiObito = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_PAI_OBITO)

        if (formulario.formularioPreDefinido == PreDefinidos.CERTIDOES_E_PEDIDO) {
            CampoFormulario tecnico = formulario.getCampoAvulso(CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO)
            CampoFormulario matricula = formulario.getCampoAvulso(FormularioCertidoesPedido.CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO)
            //procurar o operador com base no nome completo
            List usuarioSistema = UsuarioSistema.findAllByNomeCompleto(tecnico.valorArmazenado)
            //averiguar que somente um operador foi encontrado com esse nome e buscar a matricula correspondente
            if (usuarioSistema?.size() == 1 )
                matricula.valorArmazenado = usuarioSistema.get(0).matricula
        }

        if (paiNascimento.valorArmazenado)
            paiNascimento.valorArmazenado = " e " + paiNascimento.valorArmazenado

        if (conjuge2.valorArmazenado)
            conjuge2.valorArmazenado = " e " + conjuge2.valorArmazenado

        if (paiObito.valorArmazenado)
            paiObito.valorArmazenado = " e " + paiObito.valorArmazenado

        defineTipoCertidao: {
            String tipoCertidao = nomeNascimento?.valorArmazenado ? "Certidão de Nascimento" :
                    conjuge1?.valorArmazenado ? "Certidão de Casamento" :
                            nomeFalecido?.valorArmazenado ? "Certidão de Óbito" :
                                    null
            if (tipoCertidao)
                reportDTO.context.put(StringUtils.upperToCamelCase(CampoFormulario.Origem.AVULSO.toString()) + "." + FormularioCertidoesPedido.CODIGO_TIPO_CERTIDAO, tipoCertidao)
        }

        defineConteudoCertidao: {
            String lDataNascimento = ((Date)dataNascimento?.valorArmazenado)?.format("dd/MM/yyyy")
            String lDataCasamento = ((Date)dataCasamento?.valorArmazenado)?.format("dd/MM/yyyy")
            String lDataFalecimento = ((Date)dataFalecimento?.valorArmazenado)?.format("dd/MM/yyyy")
            String conteudoCertidao = nomeNascimento?.valorArmazenado ? nomeNascimento?.valorArmazenado + " nascido(a) em " + lDataNascimento + " filho(a) de " + maeNascimento?.valorArmazenado + paiNascimento?.valorArmazenado :
            conjuge1?.valorArmazenado ? conjuge1?.valorArmazenado + conjuge2?.valorArmazenado + " casados em " + lDataCasamento :
            nomeFalecido ? nomeFalecido?.valorArmazenado + " falecido em " + lDataFalecimento + " filho(a) de " + maeObito?.valorArmazenado + paiObito?.valorArmazenado :
            null
            if (conteudoCertidao)
                reportDTO.context.put(StringUtils.upperToCamelCase(CampoFormulario.Origem.AVULSO.toString()) + "." + FormularioCertidoesPedido.CODIGO_DADOS_CERTIDAO, conteudoCertidao)
        }

        super.transfereConteudo(formulario, reportDTO)
    }

}
