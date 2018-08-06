package apoiasuas

import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.importacao.DefinicoesImportacaoFamilias
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.importacao.TentativaImportacao
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils

import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Implementação da Job para logar o uso de memória a cada X minutos
 */

class MemLoggingJob {

    static triggers = {
      simple repeatInterval: 60000l // execute job each 60 seconds
    }

    /**
     * Gera um log específico para monitorar o consumo de recursos do sistema, incluindo memórias e cpu
     * os valores são separados por ; para facilitar o uso em arquivos CSV e conversão em planilhas.
     * Primeiramente são impressos no log todos os valores, seguidos (na mesma linha) dos rótulos para cada valor
     */
    def execute() {
        Map<String,String> indicadores = [:];
        indicadores << ["JVMMaxMemory": StringUtils.readableLong(Runtime.getRuntime().maxMemory())];
        indicadores << ["JVMUsedMemory":StringUtils.readableLong(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory())];

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                    if (value.toString().isNumber())
                        value = StringUtils.readableLong(value.toString().toLong());
//                    if (value.toString().isDouble() || value.toString().isFloat())
//                        value = (new Long (Math.round(value.toString().toDouble()*100))).toString()+"%";
                } catch (Exception e) {
                    value = e;
                }
                indicadores.put(method.getName().substring(3), value)
            } // if
        } // for

        ManagementFactory.getMemoryPoolMXBeans().each { item ->
            indicadores.put(item.getName() + " corrente", StringUtils.readableLong(item.getUsage().getUsed()))
            indicadores.put(item.getName() + " maximo", StringUtils.readableLong(item.getUsage().getMax()))
        }

        log.debug(indicadores.values().join(";")+";"+indicadores.keySet().join(";"));
    }

}
