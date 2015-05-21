package org.apoiasuas.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by home64 on 19/01/2015.
 */
    public class SafeMap<K,V> extends LinkedHashMap<K,V> {

    protected List camposBDDisponiveis = null;

    public SafeMap(Boolean validateKeys, List camposBDDisponiveis) {
        super();
        if (validateKeys)
            this.camposBDDisponiveis = camposBDDisponiveis;
    }

    @Override
    public V get(Object key) {
        if (camposBDDisponiveis != null && ! camposBDDisponiveis.contains(key))
            throw new RuntimeException("Chave "+key+" não é um campo válido: "+ camposBDDisponiveis);
        return super.get("coluna"+key);
    }
}
