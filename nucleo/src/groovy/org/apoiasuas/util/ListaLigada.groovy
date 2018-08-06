package org.apoiasuas.util

/**
 * Extensão de lista ligada (LinkedList) com um ponteiro de avanço que permite guardar quais nós já foram lidos
 * até o momento
 */
class ListaLigada<E> extends LinkedList<E> {
    private int pos;

    public ListaLigada() {
        reinicia()
    }

    public ListaLigada(Collection<? extends E> lista) {
        super(lista)
        reinicia()
    }

    public void reinicia() {
        pos = -1
    }

    public boolean pula() {
        pos = Math.min(pos+1, size())
        return ! fim()
    }

    public E atual() {
        if (fim())
            throw new IndexOutOfBoundsException("Fim de lista ultrapassado ($pos)")
        else
            this.get(pos)
    }

    public boolean fim() {
        return pos >= size()
    }

    public static void main(String[] args) {
        testa(["um","dois"])
        testa(["um"])
        testa([])
    }

    public static void testa(List listaComum) {
        println("Lista com ${listaComum.size()} elementos")
        ListaLigada<String> listaLigada = new ListaLigada<String>(listaComum)
        while (listaLigada.pula())
            println("-> "+ listaLigada.atual());
        println("Fim? "+ listaLigada.fim());
    }
}
