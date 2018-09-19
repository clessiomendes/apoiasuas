package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.CampoFormulario.Origem
import org.apoiasuas.formulario.FormularioCertidoesService
import org.apoiasuas.formulario.FormularioService

class FormularioPasseLivrePCD extends FormularioBase {

//    public static final String CODIGO_NOME_REGISTRO = "nome_registro"
//    public static final String CODIGO_DATA_REGISTRO = "data_registro"

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioService.class }

    @TypeChecked
    Closure run() {

        formulario {
            nome 'Gratuidade Ônibus BH - PCD'
            tipo 'Outros'
            descricao 'Solicitação de gratuidade no transporte coletivo de Belo Horizonte para pessoas com deficiência'
            template "PasseLivrePCD-Template.docx"
            grupo 'Dados Pessoais', {
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Nome'
                    codigo 'nome_completo'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.CIDADAO
                    codigo 'data_nascimento'
                }
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Identidade'
                    codigo 'identidade'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    codigo 'cpf'
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.FAMILIA
                    descricao 'Telefone Celular'
                    codigo 'telefone'
                }
                campoAvulso {
                    descricao 'Telefone Fixo'
                    codigo 'telefone_fixo'
                    tamanho 10
                }
                campoAvulso {
                    descricao 'Telefone Recado'
                    codigo 'telefone_recado'
                    tamanho 10
                }
/*
                campoBancoDeDados {
                    origem CampoFormulario.Origem.FAMILIA
                    descricao 'Telefone Fixo'
                    codigo 'telefone'
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.FAMILIA
                    descricao 'Telefone Recado'
                    codigo 'telefone'
                }
*/

//                campoAvulso {
//                    descricao 'Nacionalidade'
//                    codigo Cidadao.CODIGO_NACIONALIDADE
//                    tamanho 20
//                }

            }
            grupo 'Endereço', {
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'tipo_logradouro'
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'nome_logradouro'
                    listaLogradourosCidadaos true
                }
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'numero'
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
                }
/*
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'UF'
                }
*/
                campoBancoDeDados {
                    origem Origem.ENDERECO
                    codigo 'CEP'
                }
            }
            campoAvulso {
                codigo 'tipo_deficiencia'
                tipo CampoFormulario.Tipo.SELECAO
                descricao 'Tipo de deficiência'
                opcoes 'Física', 'Mental', 'Auditiva', 'Visual'
            }
            campoAvulso {
                codigo 'observacoes'
                multiplasLinhas 3
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Observações'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
                obrigatorio true
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_NOME_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Equipamento'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Equipamento'
            }
        }
    }
}
