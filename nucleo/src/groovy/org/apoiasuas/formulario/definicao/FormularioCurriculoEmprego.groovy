package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioCurriculoService
import org.apoiasuas.formulario.FormularioService

class FormularioCurriculoEmprego extends FormularioBase {

    public static final String CODIGO_EMAIL = 'email'

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioCurriculoService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Curriculo Emprego'
            descricao 'Apropriado para empregos que não o primeiro'
            template "CurriculoEmprego-Template.docx"
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
            campoAvulso {
                codigo 'estado_civil'
                descricao 'Estado Civil'
                tamanho 20
            }
            grupo 'Endereço', {
                campoBancoDeDados {
                    origem CampoFormulario.Origem.ENDERECO
                    codigo 'tipo_logradouro'
                    obrigatorio true
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
                    obrigatorio true
                }
                campoBancoDeDados {
                    origem CampoFormulario.Origem.FAMILIA
                    codigo 'telefone'
                    descricao 'Telefone(s)'
                }
            }
        }
    }
}
