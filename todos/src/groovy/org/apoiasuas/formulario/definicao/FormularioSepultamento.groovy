package org.apoiasuas.formulario.definicao

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.FormularioService

class FormularioSepultamento extends FormularioBase {

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Guia de sepultamento'
            tipo 'Benefícios Eventuais'
            descricao 'Formulário de concessão de benefício de sepultamento'
            template 'Sepultamento-Template.docx'
            campoAvulso {
                codigo "nome_orgao"
                tipo CampoFormulario.Tipo.TEXTO
                tamanho 60
                descricao 'Nome do órgão destinatário'
            }
            campoAvulso {
                codigo "endereco_orgao"
                tipo CampoFormulario.Tipo.TEXTO
                tamanho 60
                descricao 'Endereço do órgão destinatário'
            }
            campoAvulso {
                codigo "nome_falecido"
                tipo CampoFormulario.Tipo.TEXTO
                tamanho 60
                descricao 'Nome do falecido'
            }
            campoAvulso {
                codigo "natimorto"
                descricao 'Natimorto ?'
                tipo CampoFormulario.Tipo.SELECAO
                opcoes 'sim', 'não'
            }
            campoAvulso {
                codigo "velorio"
                descricao 'Solicitação de velorio ?'
                tipo CampoFormulario.Tipo.SELECAO
                opcoes 'sim', 'não'
            }

            grupo 'Certidão de óbito', {
                campoAvulso {
                    codigo "nome_cartorio"
                    tipo CampoFormulario.Tipo.TEXTO
                    tamanho 60
                    descricao 'Cartório'
                }
                campoAvulso {
                    codigo "data_obito"
                    tipo CampoFormulario.Tipo.DATA
                    descricao 'Data do óbito'
                }
                campoAvulso {
                    codigo "n_declaracao_obito"
                    tipo CampoFormulario.Tipo.TEXTO
                    tamanho 10
                    descricao 'Nº da declaração de óbito'
                }
                campoAvulso {
                    codigo "n_certidao_obito"
                    tipo CampoFormulario.Tipo.TEXTO
                    tamanho 10
                    descricao 'Nº da certidão de óbito'
                }
                campoAvulso {
                    codigo "n_livro"
                    tipo CampoFormulario.Tipo.TEXTO
                    tamanho 10
                    descricao 'Nº do livro'
                }
                campoAvulso {
                    codigo "n_folha"
                    tipo CampoFormulario.Tipo.TEXTO
                    tamanho 10
                    descricao 'Nº da folha'
                }
            }

            campoBancoDeDados {
                origem CampoFormulario.Origem.FAMILIA
                codigo 'cad'
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
