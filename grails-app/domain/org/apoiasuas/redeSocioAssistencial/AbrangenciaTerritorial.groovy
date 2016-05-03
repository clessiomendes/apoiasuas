package org.apoiasuas.redeSocioAssistencial

class AbrangenciaTerritorial {

    public class ComponenteVisual {
        public Boolean aberto
        public Boolean selecionado
        public Boolean desabilitado
    }

    String nome
    boolean habilitado
    AbrangenciaTerritorial mae

    //transientes
    private List<AbrangenciaTerritorial> filhos = []
    private ComponenteVisual componenteVisual = new ComponenteVisual();

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_abrangencia_territorial']
    }

    static transients = ['filhos','componenteVisual', 'nomeCompleto']

    static constraints = {
        nome(nullable: false, maxSize: 100)
    }

    public String toString() {
        return nome + "($id)"
    }

    ComponenteVisual getComponenteVisual() {
        return componenteVisual
    }

    void setComponenteVisual(ComponenteVisual componenteVisual) {
        this.componenteVisual = componenteVisual
    }

    List<AbrangenciaTerritorial> getFilhos() {
        return filhos
    }

    void setFilhos(List<AbrangenciaTerritorial> filhos) {
        this.filhos = filhos
    }

    void addFilho(AbrangenciaTerritorial filho) {
        this.filhos += filho
    }

    public String getNomeCompleto() {
        String result = nome
        AbrangenciaTerritorial maeTemp = this.mae
        boolean temMae = this.mae != null
        if (temMae)
            result += " ("
        while (maeTemp != null) {
            result += (result.endsWith("(") ? "" : ", ") + maeTemp.nome
            maeTemp = maeTemp.mae
        }
        if (temMae)
            result += ")"
        return result
    }

}
