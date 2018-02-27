package org.apoiasuas.util

import org.joda.time.DateTime

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class ApoiaSuasDateUtils {
    public static final String FORMATO_DATA_HORA = 'dd/MM/yyyy H:mm'   

    public static boolean momentosProximos(Date momento0, Date momento1, long proximidadeMillis = TimeUnit.SECONDS.toMillis(10)) {
        return Math.abs(momento1.time - momento0.time) < proximidadeMillis
    }

    public static Date stringToDateTime(String s) {
        if (s && s != "" && s != "null") {
            final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return df.parse(s);
        } else
            return null;
    }

    public static Date stringToDateTimeIso8601(String s) {
        if (s && s != "" && s != "null")
            return new DateTime(s).toDate()
        else
            return null;
    }

    public static String dateTimeToStringIso8601(Date date) {
        if (date)
            return new DateTime(date).toString()
        else
            return null;
    }

    /**
     * Retorna a diferença aproximada, em anos e em valor absoluto, entre duas datas
     */
    public static Integer yearsBetween(Date date1, Date date2) {
        if (! date1 || ! date2)
            return null;
        int days = Math.abs(Math.round(date1 - date2))
        int years = Math.round(days / 365.2422)
        return years
    }
}
