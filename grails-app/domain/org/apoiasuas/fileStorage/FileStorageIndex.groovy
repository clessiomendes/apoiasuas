package org.apoiasuas.fileStorage

class FileStorageIndex {

    Long id
    String nomeArquivo
//    String descricao
    String bucket
//    Date dateCreated

    static constraints = {
        nomeArquivo(nullable: false, maxSize: 255);
//        dateCreated(nullable: true);
//        descricao(nullable: true, maxSize: 10000);
        bucket(nullable: false, maxSize: 255);
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_file_storage_index'];
        nomeArquivo length: 1000000;
//        descricao length: 1000000;
    }

    public String getBucket() {
        return bucket;
    }

}
