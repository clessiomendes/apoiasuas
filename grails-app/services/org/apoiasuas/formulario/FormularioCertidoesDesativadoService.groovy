package org.apoiasuas.formulario

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.formulario.definicao.FormularioCertidoes
import org.apoiasuas.formulario.definicao.FormularioCertidoesPedido
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.util.StringUtils

/**
 * Created by admin on 19/04/2015.
 */
class FormularioCertidoesDesativadoService extends FormularioService {

    public static final String NACIONALIDADE_PADRAO = "brasileira"
    def pedidoCertidaoProcessoService

    class CertidoesDTO {
        Formulario formulario
        CampoFormulario nomeNascimento
        CampoFormulario dataNascimento
        CampoFormulario maeNascimento
        CampoFormulario paiNascimento
        CampoFormulario conjuge1
        CampoFormulario conjuge2
        CampoFormulario dataCasamento
        CampoFormulario nomeFalecido
        CampoFormulario dataFalecimento
        CampoFormulario maeObito
        CampoFormulario paiObito
        CampoFormulario nacionalidade
        public CertidoesDTO(Formulario formulario) {
            this.formulario = formulario
            nomeNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_NOME_NASCIMENTO)
            dataNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_NASCIMENTO)
            maeNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_MAE_NASCIMENTO)
            paiNascimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_PAI_NASCIMENTO)
            conjuge1 = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_CONJUGE_1)
            conjuge2 = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_CONJUGE_2)
            dataCasamento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_CASAMENTO)
            nomeFalecido = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_NOME_FALECIDO)
            dataFalecimento = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_DATA_FALECIMENTO)
            maeObito = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_MAE_OBITO)
            paiObito = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_PAI_OBITO)
            nacionalidade = formulario.getCampoAvulso(FormularioCertidoes.CODIGO_NACIONALIDADE)
        }
        public boolean isTipoNascimento() {
            return StringUtils.isNotBlank(nomeNascimento?.valorArmazenado?.toString())
        }
        public boolean isTipoCasamento() {
            return StringUtils.isNotBlank(conjuge1?.valorArmazenado?.toString()) && ! tipoNascimento
        }
        public boolean isTipoObito() {
            return StringUtils.isNotBlank(nomeFalecido?.valorArmazenado?.toString()) && ! tipoNascimento && ! tipoCasamento
        }
    }
    
    @Override
    Formulario preparaPreenchimentoFormulario(Long idFormulario, Long idFamilia, Long idCidadao) {
        Formulario formulario = super.preparaPreenchimentoFormulario(idFormulario, idFamilia, idCidadao)
        CertidoesDTO dto = new CertidoesDTO(formulario)

        //Atribui valores default para alguns campos
        dto.nomeNascimento.valorArmazenado = formulario.cidadao.nomeCompleto
        dto.dataNascimento.valorArmazenado = formulario.cidadao.dataNascimento
        dto.maeNascimento.valorArmazenado = formulario.cidadao.nomeMae
        dto.paiNascimento.valorArmazenado = formulario.cidadao.nomePai
        dto.nacionalidade.valorArmazenado = NACIONALIDADE_PADRAO
        return formulario
    }

    @Override
    public void gravarAlteracoes(Formulario formulario) {
        //Por via das duvidas, nao gravar alteracoes pois ha o risco de se substituir dados do cidadao no cadastro pelo do declarante no formulario
        //TODO https://github.com/clessiomendes/apoiasuas/issues/17
    }

    @Override
    protected void transfereConteudo(Formulario formulario, ReportDTO reportDTO) {
        CertidoesDTO dto = new CertidoesDTO(formulario)
        Cidadao cidadao = formulario.cidadao

        if (defineTipoCertidao(dto))
            reportDTO.context.put(StringUtils.upperToCamelCase(CampoFormulario.Origem.AVULSO.toString()) + "." + FormularioCertidoes.CODIGO_TIPO_CERTIDAO, defineTipoCertidao(dto))

        //Maraca um X no tipo de certidao:
        reportDTO.context.put(FormularioCertidoes.CODIGO_IS_NASCIMENTO, dto.tipoNascimento ? "X" : "_")
        reportDTO.context.put(FormularioCertidoes.CODIGO_IS_CASAMENTO, dto.tipoCasamento ? "X" : "_")
        reportDTO.context.put(FormularioCertidoes.CODIGO_IS_OBITO, dto.tipoObito ? "X" : "_")

        String dataRegistro
        String filiacaoConjuge
        if (dto.tipoNascimento) {
            dataRegistro = ((Date) dto.dataNascimento?.valorArmazenado)?.format("dd/MM/yyyy")
            filiacaoConjuge = filiacao(dto.maeNascimento, dto.paiNascimento)
        } else if (dto.tipoCasamento) {
            dataRegistro = ((Date) dto.dataCasamento?.valorArmazenado)?.format("dd/MM/yyyy")
            filiacaoConjuge = dto.conjuge2?.valorArmazenado + ""
        } else if (dto.tipoObito) {
            dataRegistro = ((Date) dto.dataFalecimento?.valorArmazenado)?.format("dd/MM/yyyy")
            filiacaoConjuge = filiacao(dto.maeObito, dto.paiObito)
        }

        reportDTO.context.put(CampoFormulario.Origem.AVULSO.toCamelCase() + "." + FormularioCertidoes.CODIGO_DATA_REGISTRO, dataRegistro)
        reportDTO.context.put(CampoFormulario.Origem.AVULSO.toCamelCase() + "." + FormularioCertidoes.CODIGO_FILIACAO_CONJUGE, filiacaoConjuge)

        String conteudoCertidao = defineConteudoCertidao(dto)
        if (conteudoCertidao)
            reportDTO.context.put(CampoFormulario.Origem.AVULSO.toCamelCase() + "." + FormularioCertidoes.CODIGO_DADOS_CERTIDAO, conteudoCertidao)

        super.transfereConteudo(formulario, reportDTO)
    }

    private String defineTipoCertidao(CertidoesDTO dto) {
        String tipoCertidao = dto.tipoNascimento ? "Certidão de Nascimento" :
                              dto.tipoCasamento ? "Certidão de Casamento" :
                              dto.tipoObito ? "Certidão de Óbito" :
                              null
        return tipoCertidao
    }

    private String defineConteudoCertidao(CertidoesDTO dto) {
        String lDataNascimento = ((Date) dto.dataNascimento?.valorArmazenado)?.format("dd/MM/yyyy")
        String lDataCasamento = ((Date) dto.dataCasamento?.valorArmazenado)?.format("dd/MM/yyyy")
        String lDataFalecimento = ((Date) dto.dataFalecimento?.valorArmazenado)?.format("dd/MM/yyyy")

        String conteudoCertidao = ""
        if (dto.nomeNascimento.valorArmazenado) {
            conteudoCertidao += dto.nomeNascimento.valorArmazenado;
            if (lDataNascimento)
                conteudoCertidao += " nascido(a) em " + lDataNascimento
            conteudoCertidao += " "+filiacao(dto.maeNascimento, dto.paiNascimento)
        } else if (dto.conjuge1.valorArmazenado) {
            conteudoCertidao += dto.conjuge1.valorArmazenado;
            if (dto.conjuge2.valorArmazenado)
                conteudoCertidao += " e " + dto.conjuge2.valorArmazenado
            if (lDataCasamento)
                conteudoCertidao += " casados em " + lDataCasamento
        } else if (dto.nomeFalecido.valorArmazenado) {
            conteudoCertidao += dto.nomeFalecido.valorArmazenado
            if (lDataFalecimento)
                conteudoCertidao += " falecido(a) em " + lDataFalecimento
            conteudoCertidao += " "+filiacao(dto.maeObito, dto.paiObito)
        }
        return conteudoCertidao
    }

    private String filiacao(CampoFormulario mae, CampoFormulario pai) {
        String result = ""
        if (mae.valorArmazenado)
            result += "filho(a) de " + mae.valorArmazenado
        if (pai.valorArmazenado) {
            if (mae.valorArmazenado)
                result += " e "
            else
                result += " filho(a) de "
            result += pai.valorArmazenado
        }
        result
    }

    /**
     * Gera um processo de pedido de certidao de nascimento.
     */
    @Override
    protected void eventoPosEmissao(Formulario formulario) {
        if (formulario.formularioPreDefinido == PreDefinidos.CERTIDOES_E_PEDIDO && segurancaService.acessoRecursoServico(RecursosServico.PEDIDOS_CERTIDAO)) {
            String cartorio = formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_NOME_CARTORIO) + ", " +
                    formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_BAIRRO_DISTRITO_CARTORIO) + ", " +
                    formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_MUNICIPIO_CARTORIO) + ", " +
                    formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_UF_CARTORIO);

            String observacoesInternas = formulario.getConteudoCampo(FormularioCertidoesPedido.CODIGO_OBSERVACOES_INTERNAS);

            CertidoesDTO dto = new CertidoesDTO(formulario)
            pedidoCertidaoProcessoService.novoProcesso(formulario.usuarioSistema,
                    formulario.cidadao?.familia?.id,
                    formulario.usuarioSistema.id,
                    defineTipoCertidao(dto) + " em nome de " + defineConteudoCertidao(dto),
                    formulario.formularioEmitido.id,
                    cartorio, null/*sem AR por enquanto*/, observacoesInternas)
        }
    }

}
