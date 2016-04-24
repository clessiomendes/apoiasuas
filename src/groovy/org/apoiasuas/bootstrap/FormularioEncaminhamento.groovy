package org.apoiasuas.bootstrap

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioBuilder
import org.apoiasuas.formulario.FormularioService

class FormularioEncaminhamento extends FormularioBase {

    public Class<? extends FormularioService> classeServico() { return FormularioService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Encaminhamento'
            descricao 'Encaminhamentos de prósito geral para a rede de serviços sócio-assistenciais'
            template 'Encaminhamento-Template.docx'
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_completo'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'codigo_legado'
            }
            campoAvulso {
                codigo 'destino'
                obrigatorio true
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Destino (unidade/entidade/ação)'
            }
            campoAvulso {
                codigo 'endereco_destino'
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Endereço'
            }
            campoAvulso {
                codigo 'descricao_encaminhamento'
                tipo CampoFormulario.Tipo.TEXTO
                multiplasLinhas 8
                descricao 'Detalhamento'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                obrigatorio true
                descricao 'Responsável (preenchimento)'
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

