package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioAcompanhamentoService
import org.apoiasuas.formulario.FormularioService

class FormularioAcompanhamento extends FormularioBase {


    public static final String NOME_REFERENCIA = 'nome_referencia'
    public static final String NIS_REFERENCIA = 'nis_referencia'
    public static final String DATA_INICIO = 'data_inicio'

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioAcompanhamentoService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Plano de Acompanhamento'
            descricao 'Plano de Acompanhamento Familiar Individualizado'
            template "PlanoAcompanhamento-Template.docx"
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'codigo_legado'
                obrigatorio true
            }
            campoAvulso {
                codigo 'numero_SIGPS'
                descricao 'Número da família no SIGPS'
                obrigatorio true
            }
            campoAvulso {
                codigo NOME_REFERENCIA
                descricao 'Nome da referência familiar'
                obrigatorio true
            }
            campoAvulso {
                codigo NIS_REFERENCIA
                descricao 'NIS da referência familiar'
                obrigatorio false
            }
            campoAvulso {
                codigo DATA_INICIO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data de início'
                obrigatorio true
            }
            campoAvulso {
                codigo 'data_encerramento'
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data de encerramento'
                obrigatorio false
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
/*
                campoAvulso {
                    codigo 'cidade'
                    descricao 'Cidade'
                    obrigatorio true
                }
                campoAvulso {
                    codigo 'UF'
                    descricao 'UF'
                }
*/
                campoBancoDeDados {
                    origem CampoFormulario.Origem.FAMILIA
                    codigo 'telefone'
                }
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO
                tipo CampoFormulario.Tipo.TEXTO
                descricao 'Técnico Responsável'
                obrigatorio true
            }
        }
    }
}
