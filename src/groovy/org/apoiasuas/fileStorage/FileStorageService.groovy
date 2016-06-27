package org.apoiasuas.fileStorage

import org.springframework.web.multipart.MultipartFile

/**
 * Created by clessio on 17/06/2016.
 */
interface FileStorageService {

    /**
     * Adiciona um novo arquivo no repositorio, retornando a nova chave gerada
     */
    public String add(String bucket, FileStorageDTO arquivo);

    public FileStorageDTO get(String bucket, String chave);

    public String getFileName(String bucket, String chave);

    public void remove(String bucket, String chave);

    public void init();

    public String showConfig();


}