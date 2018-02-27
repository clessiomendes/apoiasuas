package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.CampoFormulario.Origem
import org.apoiasuas.formulario.FormularioCertidoesService
import org.apoiasuas.formulario.FormularioService

class FormularioCertidoes extends FormularioBase {

    public static final String CODIGO_NOME_REGISTRO = "nome_registro"
    public static final String CODIGO_DATA_REGISTRO = "data_registro"
    public static final String CODIGO_NACIONALIDADE = "nacionalidade"

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioCertidoesService.class }

    @TypeChecked
    Closure run() {

        formulario {
            nome 'Declaracao de pobreza - certidões'
            tipo 'Certidões'
            descricao 'Declaracao de pobreza para segunda via de certidões de nascimento, casamento ou óbito'
            template "Certidoes-Template.docx"
            grupo 'Interessado', {
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Nome'
                    codigo 'nome_completo'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Identidade'
                    codigo 'identidade'
                }
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    codigo 'cpf'
                }
                campoAvulso {
                    descricao 'Nacionalidade'
                    codigo CODIGO_NACIONALIDADE
                    tamanho 20
                }
                campoAvulso {
                    codigo 'profissao'
                    descricao 'Profissão'
                    tamanho 20
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.CIDADAO
                    codigo 'nome_mae'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.CIDADAO
                    codigo 'nome_pai'
                    obrigatorio true
                }
                campoAvulso {
                    codigo 'estado_civil'
                    descricao 'Estado Civil'
                    tamanho 20
                }
                campoAvulso {
                    codigo 'uniao_estavel'
                    descricao 'Possui união estável?'
                    tipo CampoFormulario.Tipo.SELECAO
                    opcoes 'sim', 'não'
                }
                campoAvulso {
                    codigo 'nome_convivente'
                    descricao 'Nome convivente'
                }
            }
            grupo 'Endereço', {
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'tipo_logradouro'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'nome_logradouro'
                    listaLogradourosCidadaos true
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'numero'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'complemento'
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'bairro'
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'municipio'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'UF'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.FAMILIA
                    codigo 'telefone'
                }
            }
            grupo 'Dados da certidão', {
                campoAvulso {
                    codigo 'tipo_certidao'
                    descricao 'Tipo de certidão'
                    tipo CampoFormulario.Tipo.SELECAO
                    obrigatorio true
                    opcoes 'nascimento', 'casamento', 'óbito'
                }
                campoAvulso {
                    codigo CODIGO_NOME_REGISTRO
                    descricao 'Nome'
                    obrigatorio true
                }
                campoAvulso {
                    codigo CODIGO_DATA_REGISTRO
                    descricao 'Data do registro'
                    tipo CampoFormulario.Tipo.DATA
                }
                campoAvulso {
                    codigo 'livro'
                    descricao 'Livro'
                    tamanho 5
                }
                campoAvulso {
                    codigo 'folha'
                    descricao 'Folha'
                    tamanho 5
                }
                campoAvulso {
                    codigo 'termo'
                    descricao 'Termo'
                    tamanho 5
                }
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
                obrigatorio true
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_EMAIL_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Email'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_CIDADE_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Cidade'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_UF_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'UF'
            }

        }
    }
}
