package org.apoiasuas.bootstrap

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioCertidoesService
import org.apoiasuas.formulario.FormularioService

class FormularioCertidoesPedido extends FormularioBase {

    public static String CODIGO_TIPO_CERTIDAO = "tipo_certidao"
    public static String CODIGO_DADOS_CERTIDAO = "dados_certidao"
    public static String CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO = "matricula"


    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioCertidoesService.class }

    @TypeChecked
    Closure run() {

        formulario {
            nome 'Declaracao de pobreza e Solicitação de certidões'
            tipo 'Certidões'
            descricao 'Declaracao de pobreza para segunda via de certidões de nascimento, casamento ou óbito, juntamente' +
                    ' com a solicitação a ser enviada para o cartório pelo correio.'
            template "PedidoCertidao-Template.docx"
            importaCampos FormularioCertidoes.class
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                obrigatorio true
                descricao 'Repsonsável (preenchimento)'
            }
            campoAvulso {
                codigo CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Matrícula (BM)'
            }
            campoAvulso {
                codigo 'observacoes'
                tipo CampoFormulario.Tipo.TEXTO
                multiplasLinhas 5
                descricao 'Observações'
            }
            grupo 'Cartório', {
                campoAvulso {
                    codigo 'nome_cartorio'
                    descricao 'Nome'
                    obrigatorio true
                }
                campoAvulso {
                    codigo 'endereco_cartorio'
                    descricao 'Endereço'
                    obrigatorio true
                }
                campoAvulso {
                    codigo 'bairro_distrito_cartorio'
                    tamanho 30
                    descricao 'Bairro ou Distrito'
                }
                campoAvulso {
                    codigo 'municipio_cartorio'
                    tamanho 30
                    descricao 'Município'
                    obrigatorio true
                }
                campoAvulso {
                    codigo 'uf_cartorio'
                    tamanho 2
                    descricao 'UF'
                    obrigatorio true
                }
                campoAvulso {
                    codigo 'cep_cartorio'
                    tamanho 8
                    descricao 'CEP'
                    obrigatorio true
                }
            }
            grupo 'Equipamento', {
                campoAvulso {
                    codigo CampoFormulario.CODIGO_NOME_EQUIPAMENTO
                    tipo CampoFormulario.Tipo.TEXTO
                    exibirParaPreenchimento false
                    descricao 'Nome'
                }
                campoAvulso {
                    codigo CampoFormulario.CODIGO_ENDERECO_EQUIPAMENTO
                    tipo CampoFormulario.Tipo.TEXTO
                    exibirParaPreenchimento false
                    descricao 'Endereço'
                }
                campoAvulso {
                    codigo CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO
                    tipo CampoFormulario.Tipo.TEXTO
                    exibirParaPreenchimento false
                    descricao 'Telefone(s)'
                }
            }
        }
    }
}
