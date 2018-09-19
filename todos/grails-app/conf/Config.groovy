import grails.util.Holders
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao;

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

//def configuracoes = [];
//configuracoes << NucleoConfig;
//configuracoes << PedidoCertidaoConfig;
//configuracoes << NaoExisteConfig
//grails.config.defaults.locations = configuracoes;
grails.config.defaults.locations = AmbienteExecucao.getConfiguracoes();




