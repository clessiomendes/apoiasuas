package org.apoiasuas.bootstrap

import groovy.transform.TypeChecked

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.CampoFormulario.Origem
import org.apoiasuas.formulario.FormularioCertidoesService
import org.apoiasuas.formulario.FormularioService

class FormularioCertidoes extends FormularioBase {

    public static final String CODIGO_NOME_NASCIMENTO = 'nome_nascimento'
    public static final String CODIGO_DATA_NASCIMENTO = 'data_nascimento'
    public static final String CODIGO_MAE_NASCIMENTO = 'mae_nascimento'
    public static final String CODIGO_PAI_NASCIMENTO = 'pai_nascimento'
    public static final String CODIGO_DATA_CASAMENTO = 'data_casamento'
    public static final String CODIGO_CONJUGE_1 = 'conjuge_um'
    public static final String CODIGO_CONJUGE_2 = 'conjuge_dois'
    public static final String CODIGO_NOME_FALECIDO = 'nome_falecido'
    public static final String CODIGO_DATA_FALECIMENTO = 'data_falecimento'
    public static final String CODIGO_MAE_OBITO = 'mae_obito'
    public static final String CODIGO_PAI_OBITO = 'pai_obito'

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioCertidoesService.class }

    @TypeChecked
    Closure run() {

        formulario {
            nome 'Declaracao de pobreza - certidões'
            tipo 'Certidões'
            descricao 'Declaracao de pobreza para segunda via de certidões de nascimento, casamento ou óbito'
            template "Certidoes-Template.docx"
            campoBancoDeDados {
                origem Origem.CIDADAO
                descricao 'Declarante'
                codigo 'nome_completo'
                obrigatorio true
            }
            grupo 'Identidade', {
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Número'
                    codigo 'identidade'
                }
            }
            grupo 'ou Carteira de Trabalho', {
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Número'
                    codigo 'numero_ctps'
                }
                campoBancoDeDados {
                    origem Origem.CIDADAO
                    descricao 'Série'
                    codigo 'serie_ctps'
                }
            }
            campoBancoDeDados {
                origem Origem.CIDADAO
                codigo 'cpf'
            }
            campoBancoDeDados {
                origem Origem.CIDADAO
                codigo 'estado_civil'
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
            }
            grupo 'Certidão de nascimento', {
                campoAvulso {
                    codigo CODIGO_NOME_NASCIMENTO
                    descricao 'Nome no registro'
                }
                campoAvulso {
                    codigo CODIGO_DATA_NASCIMENTO
                    descricao 'Data do nascimento'
                    tipo CampoFormulario.Tipo.DATA
                }
                campoAvulso {
                    codigo CODIGO_MAE_NASCIMENTO
                    descricao 'Mãe no registro'
                }
                campoAvulso {
                    codigo CODIGO_PAI_NASCIMENTO
                    descricao 'Pai no registro'
                }
            }
            grupo 'ou Certidão de casamento', {
                campoAvulso {
                    codigo CODIGO_DATA_CASAMENTO
                    descricao 'Data do casamento'
                    tipo CampoFormulario.Tipo.DATA
                }
                campoAvulso {
                    codigo CODIGO_CONJUGE_1
                    descricao 'Cônjuge'
                }
                campoAvulso {
                    codigo CODIGO_CONJUGE_2
                    descricao 'Cônjuge'
                }
            }
            grupo 'ou Certidão de óbito', {
                campoAvulso {
                    codigo CODIGO_NOME_FALECIDO
                    descricao 'Nome do falecido'
                }
                campoAvulso {
                    codigo CODIGO_DATA_FALECIMENTO
                    descricao 'Data do óbito'
                    tipo CampoFormulario.Tipo.DATA
                }
                campoAvulso {
                    codigo CODIGO_MAE_OBITO
                    descricao 'Mãe do falecido'
                }
                campoAvulso {
                    codigo CODIGO_PAI_OBITO
                    descricao 'Pai do falecido'
                }
            }
            campoAvulso {
                codigo CampoFormulario.CODIGO_DATA_PREENCHIMENTO
                tipo CampoFormulario.Tipo.DATA
                descricao 'Data (preenchimento)'
            }
        }
    }
}
