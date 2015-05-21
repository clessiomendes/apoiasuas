package org.apoiasuas.util

import java.util.concurrent.TimeUnit

/**
 * Created by home64 on 23/03/2015.
 */
class DateUtils {
    public static final String FORMATO_DATA_HORA = 'dd/MM/yyyy H:mm'   

    public static boolean momentosProximos(Date momento0, Date momento1, proximidadeMillis = TimeUnit.SECONDS.toMillis(10)) {
        return Math.abs(momento1.time - momento0.time) < proximidadeMillis
    }
}
