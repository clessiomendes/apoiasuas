package org.apoiasuas.util

import grails.gorm.PagedResultList

/**
 * Classe para contornar a ausencia do recurso de paginacao em consultas HQL
 * Herda de PagedResultList para poder ser usada quando este tipo e esperado
 * Necessita que a lista de resultado (em um dado intervalo de paginacao) e o total de registros (sem paginacao) sejam alimentados explicitamente
 */
class HqlPagedResultList extends PagedResultList  {

    private List resultList;
    private int totalCount = Integer.MIN_VALUE;

    HqlPagedResultList(List resultList, int totalCount) {
        super(null)
        this.resultList = resultList
        this.totalCount = totalCount
    }
/**
     * @return The total number of records for this query
     */
    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public Object get(int i) {
        return resultList.get(i);
    }

    @Override
    public Object set(int i, Object o) {
        return resultList.set(i, o);
    }

    @Override
    public Object remove(int i) {
        return resultList.remove(i);
    }

    @Override
    public void add(int i, Object o) {
        resultList.add(i, o);
    }

    @Override
    public int size() {
        return resultList.size();
    }
}
