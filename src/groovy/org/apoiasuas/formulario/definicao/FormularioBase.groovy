package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.builder.FormularioBuilder
import org.apoiasuas.formulario.FormularioService

class FormularioBase {
//just for type checking
    Closure formulario(
            @DelegatesTo(value = FormularioBuilder, strategy = groovy.lang.Closure.DELEGATE_FIRST) groovy.lang.Closure closure) {
        closure
    }

    public Class<? extends FormularioService> classeServico() { return FormularioService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Formulario completo'
            descricao 'Para ser usado como base na criação de novos formulários (possui todos os campos previstos)'
            template 'Base-Template.docx'
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_completo'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'parentesco_referencia'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'data_nascimento'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nis'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_mae'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_pai'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'identidade'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'naturalidade'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'UF_naturalidade'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'codigo_legado'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'telefone'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.ENDERECO
                codigo 'tipo_logradouro'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.ENDERECO
                codigo 'nome_logradouro'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.ENDERECO
                codigo 'numero'
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
            campoBancoDeDados {
                origem CampoFormulario.Origem.ENDERECO
                codigo 'municipio'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.ENDERECO
                codigo 'UF'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_NOME_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Nome do equipamento'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_ENDERECO_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Endereço do equipamento'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Telefone(s) do equipamento'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                tamanho 60
                descricao 'Responsável (preenchimento)'
            }
        }
    }
}

