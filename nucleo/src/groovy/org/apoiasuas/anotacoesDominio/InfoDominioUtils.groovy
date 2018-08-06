package org.apoiasuas.anotacoesDominio

import java.lang.annotation.Annotation

class InfoDominioUtils {

    /**
     * Verifica e obtém a anotação referente ao campo acessivel aa partir de 'caminhoCampo' na 'classeDominio' desejada
     * @param classeDominio
     * @param caminhoCampo
     * @return
     */
    public static InfoPropriedadeDominio infoPropriedadePeloCaminho(Class classeDominio, String caminhoCampo) {
        try {
            String[] caminho = caminhoCampo.split("\\.", 2)
            //extrai APENAS a primeira parte do endereco/caminho cujas partes sao separadas por "."
            if (caminho.size() == 1) { //sem caminho complexo. Apenas o nome campo procurado
                InfoPropriedadeDominio info = classeDominio.getDeclaredField(caminhoCampo).getAnnotation(InfoPropriedadeDominio.class)
//                info.propriedadeReferenciadaTransiente = classeDominio.getDeclaredField(caminhoCampo)
                return info;
            } else //procura pelo campo no restante do caminho recursivamente
                return infoPropriedadePeloCaminho(classeDominio.getDeclaredField(caminho[0]).getType(), caminho[1])
        } catch (Throwable t) {
            throw new RuntimeException("Erro obtendo campo anotado: ${classeDominio.name}.${caminhoCampo}", t)
        }
    }

    /**
     * Verifica e obtém a anotação com 'codigo' na 'classeDominio' desejada
     * @param classeDominio
     * @param codigo
     * @return
     */
    public static InfoPropriedadeDominio infoPropriedadePeloCodigo(Class classeDominio, String codigo) {
        try {
            Collection camposAnotados = classeDominio.getDeclaredFields().findAll {
                it.getAnnotation(InfoPropriedadeDominio.class)?.codigo() == codigo
            }
            if (camposAnotados.size() > 1)
                throw new RuntimeException("Múltiplos campos anotados com o mesmo códido ${codigo} na classe ${classeDominio.name}")
            else if (camposAnotados.size() == 1)
                return camposAnotados[0].getAnnotation(InfoPropriedadeDominio.class)
            else
                return null
        } catch (Throwable t) {
            throw new RuntimeException("Erro obtendo campo anotado: ${classeDominio.name} com codigo ${codigo}", t)
        }
    }

/*
    public static Map infoPropriedadePeloCodigo(Class classeDominio, String codigoPropriedade) {
            Map result = null
            classeDominio.getDeclaredFields().each {
                Annotation info = it.getAnnotation(InfoPropriedadeDominio.class)
                if (info?.codigo() == codigoPropriedade) {
                    result = [anotacao: info, propriedade: it]
                    return
                }
            }
            return result
    }
*/

    /**
     * Verifica e obtém a anotação referente aa 'classeDominio' desejada
     */
    public static InfoClasseDominio infoClasseDominio(Class classeDominio) {
        return (InfoClasseDominio) classeDominio.getAnnotation(InfoClasseDominio.class);
    }

}
