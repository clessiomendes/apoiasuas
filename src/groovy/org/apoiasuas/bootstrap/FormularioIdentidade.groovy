package org.apoiasuas.bootstrap

import groovy.transform.TypeChecked

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioBeneficioEventualService
import org.apoiasuas.formulario.FormularioService

class FormularioIdentidade extends FormularioBase {

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioBeneficioEventualService.class }

    @TypeChecked
    Closure run() {
        formulario {

            nome 'Guia de identidade'
            tipo 'Benefícios Eventuais'
            descricao 'Formulário de gratuidade para segunda via de identidade'
            template "Identidade-Template.docx"
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_completo'
                obrigatorio true
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'data_nascimento'
                obrigatorio true
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
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'identidade'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'naturalidade'
                obrigatorio true
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'UF_naturalidade'
                obrigatorio true
            }
            grupo 'Endereço', {
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'tipo_logradouro'
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'nome_logradouro'
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
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
                obrigatorio true
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                obrigatorio true
                descricao 'Responsável (preenchimento)'
                obrigatorio true
            }
        }
    }
}
