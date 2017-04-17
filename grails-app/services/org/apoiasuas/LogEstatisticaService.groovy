package org.apoiasuas

import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils
import org.apoiasuas.log.Log

import javax.annotation.PostConstruct
import java.lang.management.ManagementFactory
import com.sun.management.OperatingSystemMXBean

import java.lang.management.MemoryPoolMXBean

@Transactional(readOnly = true)
class LogEstatisticaService {

    //todo: testar se OperatingSystemMXBean e Runtime são threadsafe
    private final OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    private final Runtime runtime = Runtime.getRuntime();
    private MemoryPoolMXBean beanPermGen, beanCodeCache, beanEdenSpace, beanSurvivorSpace, beanTenuredGen;

    @PostConstruct
    def init() {
        ManagementFactory.getMemoryPoolMXBeans().each { bean ->
            if (bean.getName().toLowerCase().indexOf("perm gen") >= 0)
                beanPermGen = bean
            else if (bean.getName().toLowerCase().indexOf("code cache") >= 0)
                beanCodeCache = bean
            else if (bean.getName().toLowerCase().indexOf("eden space") >= 0)
                beanEdenSpace = bean
            else if (bean.getName().toLowerCase().indexOf("survivor space") >= 0)
                beanSurvivorSpace = bean
            else if (bean.getName().toLowerCase().indexOf("old gen") >= 0)
                beanTenuredGen = bean;
        }
    }

    @Transactional
    public Log iniciaLog(String username, String sessionId, String request, String parametros, String valoresParametros) {


        final Log novoLog = new Log();
        novoLog.request = StringUtils.substring(request, 0, Log.MAX_SIZE);
        novoLog.parametros = StringUtils.substring(parametros, 0, Log.MAX_SIZE);
        novoLog.valoresParametros = StringUtils.substring(valoresParametros, 0, Log.MAX_SIZE_VALORES_PARAMETROS);
        novoLog.inicio = new Date();
        novoLog.username = StringUtils.substring(username, 0, Log.MAX_SIZE);
        novoLog.sessionId = StringUtils.substring(sessionId, 0, Log.MAX_SIZE);

        novoLog.JVMUsedMemory0 = runtime.maxMemory() - runtime.freeMemory();
        novoLog.JVMMaxMemory0 = runtime.maxMemory();
        novoLog.processCpuTime0 = operatingSystemMXBean.getProcessCpuLoad();
        novoLog.freePhysicalMemorySize0 = operatingSystemMXBean.getFreePhysicalMemorySize();
        novoLog.totalPhysicalMemorySize0 = operatingSystemMXBean.getTotalPhysicalMemorySize();
        novoLog.permGen0 = beanPermGen?.usage?.used;
        novoLog.codeCache0 = beanCodeCache?.usage?.used;
        novoLog.edenSpace0 = beanEdenSpace?.usage?.used;
        novoLog.survivorSpace0 = beanSurvivorSpace?.usage?.used;
        novoLog.tenuredGen0 = beanTenuredGen?.usage?.used;

        return novoLog.save();
    }

    @Transactional
    public void finalizaLog(Log log) {
        log.duracaoms = new Date().time - log.inicio.time;

        log.JVMUsedMemory1 = runtime.maxMemory() - runtime.freeMemory();
        log.JVMMaxMemory1 = runtime.maxMemory();
        log.processCpuTime1 = operatingSystemMXBean.getProcessCpuLoad();
        log.freePhysicalMemorySize1 = operatingSystemMXBean.getFreePhysicalMemorySize();
        log.totalPhysicalMemorySize1 = operatingSystemMXBean.getTotalPhysicalMemorySize();
        log.permGen1 = beanPermGen?.usage?.used;
        log.codeCache1 = beanCodeCache?.usage?.used;
        log.edenSpace1 = beanEdenSpace?.usage?.used;
        log.survivorSpace1 = beanSurvivorSpace?.usage?.used;
        log.tenuredGen1 = beanTenuredGen?.usage?.used;
        
        log.save();
    }

}
