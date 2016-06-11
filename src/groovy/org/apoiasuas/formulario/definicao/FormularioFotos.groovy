package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioBeneficioEventualService
import org.apoiasuas.formulario.FormularioService

class FormularioFotos extends FormularioBase {

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioBeneficioEventualService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Guia de fotos'
            tipo 'Benefícios Eventuais'
            descricao 'Formulário de gratuidade para fotos 3x4'
            template 'Fotos-Template.docx'
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_completo'
                obrigatorio true
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'identidade'
            }
            grupo 'Endereço', {
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'tipo_logradouro'
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'nome_logradouro'
                    listaLogradourosCidadaos true
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'numero'
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'complemento'
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'bairro'
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'CEP'
                }
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'telefone'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
                obrigatorio true
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Responsável (preenchimento)'
                obrigatorio true
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Matrícula (BM)'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_NOME_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Nome do equipamento'
            }
        }
    }
}
