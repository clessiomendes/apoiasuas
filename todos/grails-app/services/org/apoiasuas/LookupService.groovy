package org.apoiasuas

import grails.transaction.NotTransactional
import org.apoiasuas.lookup.LookupRecord
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver

/**
 * Serviço para lidar com as tabelas lookup aa partir de arquivos txt de configuracao. Mensagem para constar em todos os cabeçalhos destes arquivos a seguir:
 *
 * Arquivo de configuracao das opcoes disponiveis para o sistema. O nome deste arquivo corresponde aa tabela que sera alimentada no sistema e, em geral, ao campo do cadastro de familias ou cidadãos.
 * Utilizar formato UTF8 (para caracteres especiais)

 * Cada linha tem um código numério, seguida de espaço e a descrição correspondente (que pode ter várias palavras, mas idealmente deve ser curta)
 * As opções serão exibidas para o operador na ordem em que aparecem neste arquivo, e NÃO NA ORDEM DOS CÓDIGOS.
 * A opção "Outros", quando existir, deve ter o código ZERO, que tem um tratamento especial no sistema (exibindo um novo campo para especificar, por exemplo). Em geral, é a última opção.
 * Sugere-se que as descrições com várias palavras tenham somente a primeira letra maiúscula, a não ser que se trate de nome próprio ou siglas.

 * Instruções de manutenção:
 *  => Novas linhas podem ser incluidas, mas sempre com novos codigos. Não necessariamente a nova linha precisa ser a última, mesmo que tenha o último número de código.
 * Ela deve ser inserida na ordem em que se deseja que seja apresentada para o operador do sistema.

 *  => Linhas existentes NÃO devem ser excluídas. Caso precise-se excluir uma opção do sistema, marcar o código prescendo-o de um sinal de menos, como no exemplo abaixo:
 * -5 Preto

 *  => Linhas existentes podem ter sua descrição alterada preservando-se o código OU, marcadas como excluídas ("-") e inseridas com um novo código.
 * No primeiro caso, o sistema atualiza todas as escolhas atualmente no banco de dados.
 * No segundo caso, o sistema passa a exibir a opção como se ela não existisse antes. Exemplo:
 * -5 Preto
 * 6 Negro
 * No exemplo, a opção "Preto" deixa de existir para novas escolhas e a opção "Negro" é tratada não como substituta da primeira, mas como uma novo opção disponível no sistema.

 *  => Linhas desativadas no passado podem ser reativadas, bastando-se retirar o sinal "-" do código
 */
class LookupService {

    static scope = "singleton" //enfatizando o valor default
    static transactional = false;

    //propriedade de instância: escrita sincronizada e leitura concorrente
    private static HashMap<String, List<LookupRecord>> tabelas = [:];

    @NotTransactional
    public Map getTabelas() {
        if (! tabelas)
            inicializaLookups();
        return tabelas;
    }

    @NotTransactional
    public String getDescricao(String tabela, Object codigo) {
        if (! codigo)
            return "";
        return getTabelas()[tabela].find { it.codigo == Integer.parseInt(codigo.toString()) }.descricao
    }

    @NotTransactional
    /**
     * Varre o classpath inteiro em busca de arquivos texto com a extensao .lookup para alimentar as tabelas lookup (codigo - descricao)
     * que serão utilizadas no sistema (principalmente o cadastro de familias e cidadaos)
     */
    public synchronized boolean inicializaLookups() {

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        Resource[] resources = resolver.getResources("classpath*:/**/*.lookup");
        for (Resource resource: resources){
            log.debug("Carregando "+resource.getFilename())
            String nomeTabela = resource.getFilename().substring(0, resource.getFilename().lastIndexOf('.'))
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
                String line;
                List<LookupRecord> lookupRecordList = [];
                while ((line = br.readLine()) != null) {
                    //expressão regular para ver se a linha inicia com um número inteiro (possivelmente negativo)
                    if (line.split(" ")[0].matches("^(\\+|-)?\\d+\$")) {
                        //corta o codigo do inicio da linha e converte em inteiro
                        int codigo = Integer.parseInt(line.split(" ")[0]);
                        //obtem o restante da linha desprezando o codigo (e espaços em branco no começo e no fim)
                        String descricao = line.split(" ")[-1..1].reverse().join(" ").trim();
                        //se o codigo estiver marcado com sinal de menos (ou seja, se tiver sido convertido para um numero negativo), desativa o registro
                        lookupRecordList << new LookupRecord(codigo: codigo, ativo: codigo >= 0, descricao: descricao);
                    } else {
                        //sinaliza linhas que não foram processadas (e não deveriam ser ignoradas)
                        if (line.trim() != "" && ! line.startsWith("//"))
                            log.warn("Ignorando entrada do arquivo de opções lookup ${resource.getFilename()}. Formato inválido na linha <$line> ");
                    }
                }
                tabelas << [(nomeTabela): lookupRecordList];
            } catch (Exception e) {
                log.error(e);
            } finally {
                if (br)
                    br.close();
            }
        }
        log.debug(" carregados! ")
    }

}
