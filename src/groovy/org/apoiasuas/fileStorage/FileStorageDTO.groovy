package org.apoiasuas.fileStorage

/**
 * Created by clessio on 18/06/2016.
 */
class FileStorageDTO {
    String fileName
    byte[] bytes

    public FileStorageDTO(String fileName, byte[] bytes) {
        this.fileName = fileName
        this.bytes = bytes
    }
}
