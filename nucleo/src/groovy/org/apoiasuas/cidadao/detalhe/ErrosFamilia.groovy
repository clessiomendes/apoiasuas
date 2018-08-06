package org.apoiasuas.cidadao.detalhe

public class ErrosFamilia {
    //mensagens de erro globais
    MensagensErro errosGlobais = new MensagensErro()
    //chave=nomeCampo, valor=lista de mensagens de erro relativas ao campo
    Map<String, MensagensErro> mapaCampos = [:]
    //chave=ordem do membro na submissao, valor=lista de objetos contendo erros do membro
    Map<Integer, ErrosCidadao> mapaMembros = [:]
}

public class ErrosCidadao {
    //mensagens de erro globais
    MensagensErro errosGlobais = new MensagensErro()
    //chave=nomeCampo, valor=lista de mensagens de erro relativas ao campo
    Map<String, MensagensErro> mapaCampos = [:]
}

public class MensagensErro {
    List<String> mensagens = []
    public void add(String mensagem) {
        mensagens << mensagem
    }
}