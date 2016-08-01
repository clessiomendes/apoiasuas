package org.apoiasuas.fileStorage

class FileStorageIndex {

    Long id
    String nomeArquivo
    String bucket

    static constraints = {
        nomeArquivo(nullable: false, maxSize: 255)
        bucket(nullable: false, maxSize: 255)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_file_storage_index']
    }

    public String getBucket() {
        return bucket;
    }

}
