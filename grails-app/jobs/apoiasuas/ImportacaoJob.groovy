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

/**
 * Implementação da Job de importação agendada (de madrugada) usando o plugin Quartz.
 * As rotinas de importação tiveram que ser adaptadas pois não mais estavam em um
 * contexto HTTP (nao faz mais sentido falar em usuario logado ou servico logado).
 * Todas as importacoes agora sao feitas em nome do usuario admin e o servico logado
 * eh extraido das ultimas posicoes do nome do arquivo sendo importado.
 */

class ImportacaoJob {
    public final static String BUCKET = "importacao";
    public final static String BUCKET_CONCLUIDA = BUCKET+"/concluidas";
    private final static String ID_SERVICO_IMPORTACAO = "id-";
    private final static String FIM_ARQUIVO_IMPORTACAO = ".xlsx";

//    public static String CRON_DEFINITION = "0 41 16 ? * *"
    //De segunda a sexta, entre 1:00 e 3:00 da manhã. Caso haja algum erro no processamento da importacao, permite que sejam feitas ate 3 tentativas
    private static String CRON_DEFINITION = "0 0 1,2,3 ? * MON-FRI"

    //Serializa a execução da job impedindo que duas importacoes sejam feitas simultaneamente
    def concurrent = false

//    static triggers = {
//      simple repeatInterval: 5000l // execute job once in 5 seconds
//    }

    ImportarFamiliasService servicoImportarFamilias;
    SegurancaService segurancaService;
    FileStorageService fileStorageService;

    def execute() {
        log.info("Job: Verificando importacoes pendentes...");

        FileStorageDTO[] dtos = fileStorageService.list(BUCKET, FIM_ARQUIVO_IMPORTACAO)

        dtos.each { dto ->
            final UsuarioSistema admin = segurancaService.admin;
            InputStream inputStream = new ByteArrayInputStream(dto.bytes);
            log.info("Job: Importando de "+dto.fileName);

            ServicoSistema servicoSistema = ServicoSistema.read(extraiIdServico(dto.fileName))
            if (servicoSistema) {
                DefinicoesImportacaoFamilias definicoes = servicoImportarFamilias.getDefinicoes(servicoSistema);
                if (!definicoes.linhaDoCabecalho || !definicoes.abaDaPlanilha) {
                    throw new ApoiaSuasException("Job: Configuracoes nao definidas (linha do cabecalho ou aba da planilha)");
                }

                TentativaImportacao tentativaImportacao = servicoImportarFamilias.registraNovaImportacao(definicoes.linhaDoCabecalho, definicoes.abaDaPlanilha, admin, servicoSistema)
//        try {
                tentativaImportacao = servicoImportarFamilias.preImportacao(inputStream, tentativaImportacao, definicoes.linhaDoCabecalho, definicoes.abaDaPlanilha, false/*assincrono*/)
//        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
//            response.status = 500
//            return render ([errorMessage: "Formato invalido ou arquivo de importacao nao enviado"] as JSON)
//        }
                log.info("Job: pre importacao encerrada")
                if (!tentativaImportacao?.id) {
//            response.status = 500
//            return render ([errorMessage: "Erro na pre-importacao"] as JSON)
                    throw new ApoiaSuasException("Job: Erro na pre-importacao");
                }

                Map camposPreenchidos = servicoImportarFamilias.getDefinicoesImportacaoFamilia(servicoSistema)

                log.info("Job: concluindo importacao")
                servicoImportarFamilias.concluiImportacao(camposPreenchidos, tentativaImportacao.id, admin, servicoSistema)
                fileStorageService.move(BUCKET, BUCKET_CONCLUIDA, dto);

                log.info("Job: Importacao concluida com sucesso (id ${tentativaImportacao.id}). Veja detalhes na tela de importacoes.");

            } else { //Serviço não encontrado para este arquivo de importação
                log.error("Job: Impossivel identificar id do servico para importacao no arquivo "+dto.fileName);
            }
        }
    }

    private Long extraiIdServico(String nomeArquivo) {
        int inicio = nomeArquivo.lastIndexOf(ID_SERVICO_IMPORTACAO) + ID_SERVICO_IMPORTACAO.length();
        int fim = nomeArquivo.lastIndexOf(FIM_ARQUIVO_IMPORTACAO);
        String tempResult = nomeArquivo.substring(inicio, fim);
        return tempResult.isLong() ? tempResult as Long : null;
    }

}
