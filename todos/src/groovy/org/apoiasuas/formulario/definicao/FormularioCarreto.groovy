package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioBeneficioEventualService
import org.apoiasuas.formulario.FormularioCarretoService
import org.apoiasuas.formulario.FormularioService

class FormularioCarreto extends FormularioBase {

    public static final String CODIGO_ORIGEM = "origem"
    public static final String CODIGO_DESTINO = "destino"

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioCarretoService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Guia de carreto'
            tipo 'Benefícios Eventuais'
            descricao 'Formulário de concessão de benefício de carreto para mudança'
            template 'Carreto-Template.docx'
            campoAvulso {
                codigo CODIGO_ORIGEM
                tipo CampoFormulario.Tipo.TEXTO
                tamanho 100
                descricao 'Endereço e referências da origem'
                obrigatorio true
            }
            campoAvulso {
                codigo CODIGO_DESTINO
                tipo CampoFormulario.Tipo.TEXTO
                tamanho 100
                descricao 'Endereço e referências do destino'
                obrigatorio true
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'nome_completo'
                obrigatorio true
            }
            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'cad'
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
