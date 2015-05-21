package org.apoiasuas.util

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Created by home64 on 24/01/2015.
 */
class CollectionUtils {

    //metodo join do Grails usado em colecoes, mas ignorando valores nulos
    public static String join(Iterable self, String separator) {
        StringBuilder buffer = new StringBuilder();
        boolean first = true;

        if (separator == null) separator = "";

        for (Object value : self) {
            if (value != null) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(separator);
                }
                buffer.append(InvokerHelper.toString(value));
            }
        }
        return buffer.toString();
    }

}
