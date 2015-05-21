package org.apoiasuas.util

import grails.gorm.DetachedCriteria
import org.grails.datastore.mapping.query.Query

/**
 * Created by home64 on 25/01/2015.
 * TODO: Essa classe ainda eh usada? Podemos exclui-la?
 */
class MyDetachedCriteria<T> extends DetachedCriteria<T> {
    MyDetachedCriteria(Class targetClass, String alias) {
        super(targetClass, alias)
    }
    MyDetachedCriteria(Class targetClass) {
        super(targetClass)
    }
    void addAll(List<Query.Criterion> criterions) {
        criterions.each {
            super.add(it)
        }
    }

    MyDetachedCriteria<T> build(Closure callable) {
        MyDetachedCriteria newCriteria = this.clone()
        newCriteria.with callable
        return newCriteria
    }

}
