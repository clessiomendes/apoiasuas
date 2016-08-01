package org.apoiasuas.fileStorage

import org.apache.commons.io.FileUtils

/**
 * Created by clessio on 18/06/2016.
 */
class FileStorageDTO {

    public static final long MAX_FILE_SIZE = 10 * FileUtils.ONE_MB //10M
    public static String INPUT_FILE = "inputFile"
    public static String FILE_ACTION = "fileAction"
    /**
     * Acoes usadas na camada de visao para inidicar o que deve ser feito com o arquivo anexado na camada de serviço
     **/
    public static enum FileActions { MANTER_ATUAL, ATUALIZAR, ANULAR }


    String fileName
    byte[] bytes

    public FileStorageDTO(String fileName, byte[] bytes) {
        this.fileName = fileName
        this.bytes = bytes
    }
}
