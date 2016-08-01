package org.apoiasuas.fileStorage

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import org.apoiasuas.util.ApoiaSuasException

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@Transactional(readOnly = true)
class LocalFSService implements FileStorageService {

    //Injetado em resources.groovy
    String caminhoRepositorio
//    public setCaminhoRepositorio(def algo) {
//        caminhoRepositorio = algo
//    }

    @Override
    @Transactional
    public String add(String bucket, FileStorageDTO file) {
        //Validacoes
        parametrosObrigatorios([bucket: bucket, arquivo: file.bytes], "armazenamento")
//        String erro = ""
//        if (! bucket)
//            erro += " bucket nao pode ser nulo para armazenamento do file "
//        if (! file)
//            erro += " file nao pode ser nulo para armazenamento de file "
//        if (! erro.isEmpty())
//            throw ApoiaSuasException(erro)

        //gerando indice no banco de dados
        FileStorageIndex fileStorageIndex = new FileStorageIndex()
        fileStorageIndex.nomeArquivo = file.fileName
        fileStorageIndex.bucket = bucket
        fileStorageIndex.save() //garante a geracao do id

        //copiando arquivo no sistema de arquivos
        Path bucketPath = montaCaminho(bucket);
        if (Files.notExists(bucketPath))
            Files.createDirectory(bucketPath)
        Path filePath = bucketPath.resolve(fileStorageIndex.id.toString())

        //TODO: verificar possiveis falhas de segurança em upload de arquivos guardados no servidor https://www.owasp.org/index.php/Unrestricted_File_Upload
        Path effectiveFilePath = Files.write(filePath, file.getBytes());

        //Tira permissão de execucao do arquivo (se o SO permitir)
        try {
            new File(effectiveFilePath.toAbsolutePath().toString()).setExecutable(false);
        } catch (SecurityException | IOException e) {
            log.warn("Nao foi possivel retirar permissao de execucao do arquivo "+filePath.toString()+". Permissoes atuais desconhecidas. Mensagem de erro: "+e.getMessage() )
        }

        return fileStorageIndex.id.toString();
    }

    @Override
    public FileStorageDTO get(String bucket, String chave) {
        //Validacoes
        parametrosObrigatorios([bucket: bucket, chave: chave], "obtenção")
//        String erro = ""
//        if (! bucket)
//            erro += " bucket nao pode ser nulo para obtencao de file "
//        if (! chave)
//            erro += " chave nao pode ser nula para obtencao de file "
//        if (! erro.isEmpty())
//            throw ApoiaSuasException(erro)

        //obtem indice do banco de dados
        FileStorageIndex fileStorageIndex = FileStorageIndex.get(chave.toLong())
        if (fileStorageIndex.bucket != bucket) //confirma se esta no bucket esperado
            throw new ApoiaSuasException("Arquivo $chave nao encontrado no repositorio $bucket")

        //busca o arquivo armazenado
        Path file = montaCaminho(bucket, fileStorageIndex.id.toString());
        return new FileStorageDTO(fileStorageIndex.nomeArquivo, Files.readAllBytes(file) )
    }

    @Override
    String getFileName(String bucket, String chave) {
        //Validacoes
        parametrosObrigatorios([bucket: bucket, chave: chave], "obtenção")
        return FileStorageIndex.get(chave.toLong())?.nomeArquivo
    }

    @Override
    @Transactional
    public void remove(String bucket, String chave) {
        //Validacoes
        parametrosObrigatorios([bucket: bucket, chave: chave], "remoção")

        //obtem indice do banco de dados
        FileStorageIndex fileStorageIndex = FileStorageIndex.get(chave.toLong())

        //apaga o arquivo do sistema de arquivos
        Path file = montaCaminho(fileStorageIndex.bucket, fileStorageIndex.id.toString());
        if (Files.exists(file))
            Files.delete(file)
    }

    private void parametrosObrigatorios(Map argumentos, String acao) {
        String erro = ""
        argumentos.each {
            if (! it.value)
                erro += " argumento '${it.key}' nao pode ser nulo para $acao de arquivo "
        }
        if (! erro.isEmpty())
            throw new ApoiaSuasException(erro)
    }

    private Path montaCaminho(String bucket, String nomeArquivo = "") {
        if (! caminhoRepositorio)
            throw new ApoiaSuasException("Nenhum caminho de repositorio definido nas configuracoes");
        return Paths.get(caminhoRepositorio, bucket, nomeArquivo).normalize();
    }

    @Override
    @NotTransactional
    public String showConfig() {
        return "Implementação: ${this.getClass().getSimpleName()}, Classe de domínio para índice: ${FileStorageIndex.getSimpleName()}, " +
                "Caminho do repositório: ${caminhoRepositorio}"
    }

    @Override
    void init() {
        Path pastaRaizRepositorio = montaCaminho("");
        if (Files.notExists(pastaRaizRepositorio))
            Files.createDirectory(pastaRaizRepositorio)
        log.info("Usando repositorio de arquivos do tipo LocalFS (sistema de arquivos local) no caminho: "+pastaRaizRepositorio.toString())
    }

}
