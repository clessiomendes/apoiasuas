package org.apoiasuas.log

/**
 * Entidade para registrar diferentes tipos de logs, com possibilidade de calcular estatisticas de desempenho
 */
class Log {

    public final static int MAX_SIZE_VALORES_PARAMETROS = 1000
    public final static int MAX_SIZE = 255

    String request
    String parametros
    String valoresParametros
    Date inicio
    Long duracaoms
    String username
    String sessionId

    Float JVMUsedMemory0, JVMMaxMemory0, processCpuTime0, freePhysicalMemorySize0, totalPhysicalMemorySize0;
    Float permGen0, codeCache0, edenSpace0, survivorSpace0, tenuredGen0;

    Float JVMUsedMemory1, JVMMaxMemory1, processCpuTime1, freePhysicalMemorySize1, totalPhysicalMemorySize1;
    Float permGen1, codeCache1, edenSpace1, survivorSpace1, tenuredGen1;

    static constraints = {
        request(nullable: true, maxSize: 255);
        parametros(nullable: true, maxSize: 255);
        valoresParametros(nullable: true, maxSize: 1000);
        username(nullable: true, maxSize: 255);
        sessionId(nullable: true, maxSize: 255);
        inicio(nullable: false);
        duracaoms(nullable: true);
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_log']
        datasource 'log'
    }
}
