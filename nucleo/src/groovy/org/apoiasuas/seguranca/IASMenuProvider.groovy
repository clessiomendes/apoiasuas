package org.apoiasuas.seguranca

/**
 * Interface para sinalizar que um servico ou controler (falta decidir) deve fornecer um metodo que alimente opcoes de menu.
 * Estaria presente em cada modulo/plugin para gerar dinamicamente os itens de menu relativos aaquele modulo.
 */
interface IASMenuProvider {
    public void buildMenu();
}