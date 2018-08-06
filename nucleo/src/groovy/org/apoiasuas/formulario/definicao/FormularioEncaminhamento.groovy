package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioEncaminhamentoService
import org.apoiasuas.formulario.FormularioService

class FormularioEncaminhamento extends FormularioBase {

    public static final String CODIGO_CAMPO_DESTINO = "destino"
    public static final String CODIGO_CAMPO_NOME_COMPLETO = "nome_completo"
    public static final String CODIGO_CAMPO_CAD = "cad"
    public static final String CODIGO_CAMPO_ENDERECO_DESTINO = "endereco_destino"
    public static final String CODIGO_CAMPO_DESCRICAO_ENCAMINHAMENTO = "descricao_encaminhamento"

    public Class<? extends FormularioService> classeServico() { return FormularioEncaminhamentoService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Encaminhamento'
            descricao 'Encaminhamentos de prósito geral para a rede de serviços sócio-assistenciais'
            template 'Encaminhamento-2009.docx', 'Encaminhamento-2017.docx'
            campoAvulso {
                codigo CODIGO_CAMPO_DESTINO
                obrigatorio true
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Nome formal'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                obrigatorio true
                codigo CODIGO_CAMPO_NOME_COMPLETO
                descricao 'Nome completo do cidadão'
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo CODIGO_CAMPO_CAD
            }
            campoAvulso {
                codigo CODIGO_CAMPO_ENDERECO_DESTINO
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Endereço e horários de atendimento'
            }
            campoAvulso {
                codigo CODIGO_CAMPO_DESCRICAO_ENCAMINHAMENTO
                tipo CampoFormulario.Tipo.TEXTO
                obrigatorio true
                multiplasLinhas 8
                descricao 'Detalhamento'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                obrigatorio true
                descricao 'Data (preenchimento)'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                obrigatorio true
                descricao 'Responsável (preenchimento)'
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                exibirParaPreenchimento false
                descricao 'Matrícula (BM)'
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
                campoAvulso {
                    codigo CampoFormulario.CODIGO_EMAIL_EQUIPAMENTO
                    tipo CampoFormulario.Tipo.TEXTO
                    exibirParaPreenchimento false
                    descricao 'Email'
                }
            }
        }
    }
}

