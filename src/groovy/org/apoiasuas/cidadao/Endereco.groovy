package org.apoiasuas.cidadao

import org.apoiasuas.anotacoesDominio.InfoClasseDominio
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.util.CollectionUtils

//  ===================>>>   CAMPOS COMPOSTOS
@InfoClasseDominio(codigo=CampoFormulario.Origem.ENDERECO)
class Endereco  implements Serializable {

    @InfoPropriedadeDominio(codigo='tipo_logradouro', descricao = 'Tipo de logradouro', tamanho = 10)
    String tipoLogradouro //importado

    @InfoPropriedadeDominio(codigo='nome_logradouro', descricao = 'Nome do logradouro', tamanho = 60)
    String nomeLogradouro //importado

    @InfoPropriedadeDominio(codigo='numero', descricao = 'Número', tamanho = 5)
    String numero //importado

    @InfoPropriedadeDominio(codigo='complemento', descricao = 'Complemento', tamanho = 60)
    String complemento //importado

    @InfoPropriedadeDominio(codigo='bairro', descricao = 'Bairro', tamanho = 60)
    String bairro //importado

    @InfoPropriedadeDominio(codigo='CEP', descricao = 'CEP', tamanho = 8)
    String CEP //importado

    @InfoPropriedadeDominio(codigo='municipio', descricao = 'Município', tamanho = 60)
    String municipio //importado

    @InfoPropriedadeDominio(codigo='UF', descricao = 'UF', tamanho = 2)
    String UF //importado

    //atributos transientes (calculados)
    static transients = ['tipoENomeLogradouro', 'enderecoCompleto']
    static constraints = {
        CEP(nullable: true)
        tipoLogradouro (nullable: true)
        nomeLogradouro (nullable: false)
        numero (nullable: true)
        complemento (nullable: true)
        bairro (nullable: true)
        CEP (nullable: true)
        municipio (nullable: true)
        UF (nullable: true)
    }

    String toString() {
        return CollectionUtils.join([
                getTipoENomeLogradouro(),
                numero, complemento, bairro], ", ") ?: ""
    }

    String getEnderecoCompleto() {
        return CollectionUtils.join([
                getTipoENomeLogradouro(),
                numero, complemento, bairro, municipio, UF, "CEP "+CEP], ", ") ?: ""
    }

    String getTipoENomeLogradouro() {
        return nomeLogradouro ? (tipoLogradouro ? tipoLogradouro + " ": "") + nomeLogradouro : null
    }

}