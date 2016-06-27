package org.apoiasuas.util

/**
 * Created by admin on 21/05/2016.
 */
class ApoiaSuasException extends RuntimeException {

    ApoiaSuasException() {
    }

    ApoiaSuasException(String var1) {
        super(var1)
    }

    ApoiaSuasException(String var1, Throwable var2) {
        super(var1, var2)
    }

    ApoiaSuasException(Throwable var1) {
        super(var1)
    }

    ApoiaSuasException(String var1, Throwable var2, boolean var3, boolean var4) {
        super(var1, var2, var3, var4)
    }
}
