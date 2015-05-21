package org.apoiasuas.util

import com.google.common.base.CaseFormat

/**
 * Created by home64 on 12/04/2015.
 */
class StringUtils {
    static String firstLowerCase(String value) {
        return value[0].toLowerCase() + value.substring(1)
    }
    static String upperToCamelCase(String value) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, value);
    }
}
