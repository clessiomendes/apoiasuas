package org.apoiasuas.seguranca

import grails.plugin.springsecurity.SecurityTagLib
import grails.util.Holders
import groovy.transform.Synchronized
import org.apoiasuas.InicioController
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Constroi e adiciona itens de menu, linkados com as actions e controllers correpondentes
 */
class ASMenuBuilder {

    private final List<ItemMenuDTO> menusDisponiveis = [];

    @Synchronized
    public void novaOpcaoMenu(ItemMenuDTO itemMenuDTO) {
//        System.out.println("ola");
        ItemMenuDTO menuTemp = this.menusDisponiveis.find { it.descricao == itemMenuDTO.descricao };
        if (menuTemp)
            this.menusDisponiveis.remove(menuTemp)
        this.menusDisponiveis << itemMenuDTO
    }

    public void limpaMenus() {
        menusDisponiveis.clear();
    }

    public void montaMenuBasico() {

        //TODO: Criar perfil usuario avancado

        novaOpcaoMenu(new ItemMenuDTO(ordem: 100L, descricao: "Emissão de Formulários",
                hint: "Formulários emitidos on-line com preenchimento automático à partir do banco de dados de cidadãos (quando disponíveis)",
                imagem: "usecases/formulario-w.png", classeCss: "verde_oliva", link: [controller:"emissaoFormulario", action:"escolherFormulario"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 200L, descricao: "Pesquisa de Usuários",
                hint: "Banco de dados de famílias cadastradas no serviço",
                imagem: "usecases/procurar-usuario-w.png", classeCss: "laranja", link: [controller:"cidadao", action:"procurarCidadao"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 300L, descricao: "Cadastrar família",
                hint: "Cadastra uma nova família no banco de dados",
                imagem: "usecases/cadastrar-familia-w.png", classeCss: "rosa ${InicioController.novoRecurso("31/03/2018","novo-recurso-menu")}", link: [controller:"familiaDetalhado", action:"create"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 400L, descricao: "Rede intersetorial e sócioassistencial",
                hint: "Serviços, benefícios e programas disponíveis na rede sócioassistencial e intersetorial",
                imagem: "usecases/rede-socio-assistencial-w.png", classeCss: "verde_agua", link: [controller:"servico"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 500L, descricao: "Agenda",
                hint: "Agenda de atendimentos e demais compromissos",
                imagem: "usecases/agenda-w.png", classeCss: "lilas", link: [controller:"agenda", action:"calendario"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 600L, descricao: "Links e documentos",
                hint: "Links para sites externos ou documentos, formulários, planilhas, etc salvos no sistema para consulta posterior",
                imagem: "usecases/link-w.png", classeCss: "azul", link: [controller:"link", action:"exibeLinks"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 700L, descricao: "Listagens",
                hint: "Geração de planilhas com a relação de famílias ou membros de acordo com diferentes critérios (idade, técnico de referência, programa de que participa, etc)",
                imagem: "usecases/listagens-w.png", classeCss: "magenta", link: [controller:"emissaoRelatorio", action:"definirListagem"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 800L, descricao: "Gestão de Pedidos de Certidão",
                recursoServico: RecursosServico.PEDIDOS_CERTIDAO,
                hint: "Consultar a situação de pedidos de certidão emitidos anteriormente (ou registrar manualmente um pedido feito fora do sistema)",
                imagem: "usecases/pedidos-certidao-w.png", classeCss: "marrom", link: [controller: "pedidoCertidaoProcesso", action: "preList"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 900L, descricao: "Acompanhamento familiar",
                hint: "Registrar um acompanhamento e emitir o Plano de Acompanhamento Familiar",
                recursoServico: RecursosServico.PLANO_ACOMPANHAMENTO, restricaoAcesso: DefinicaoPapeis.STR_TECNICO,
                imagem: "usecases/acompanhamento-w.png", classeCss: "lilas", link: [controller:"familia", action:"selecionarAcompanhamento"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 1000L, descricao: "Gestão técnica",
                hint: "Permite ao técnico gerenciar as famílias de quem é referência, monitoramentos das intervenções com as famílias (acompanhadas ou não), pedidos de certidão, etc",
                imagem: "usecases/gestao-tecnica-w.png", classeCss: "rosa", link: [controller:"gestaoTecnica", action:"inicial"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 1100L, descricao: "Status do sistema",
                hint: "Informações técnicas do sistema",
                classeCss: "verde_oliva", link: [controller:"inicio", action:"status"]));

        novaOpcaoMenu(new ItemMenuDTO(ordem: 1200L, descricao: "Perfil e senha",
                hint: "Alterar suas informações como nome, matrícula, senha, etc",
                classeCss: "laranja", link: [controller:"usuarioSistema", action:"alteraPerfil"]));

        super_user: { // Restritos ao operador administrador
            novaOpcaoMenu(new ItemMenuDTO(ordem: 1300L, descricao: "Configuração de formulários",
                    hint: "Alterações no mecanismo de emissão automática de formulários",
                    restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
                    classeCss: "azul", link: [controller:"formulario", action:"list"]));

            novaOpcaoMenu(new ItemMenuDTO(ordem: 1400L, descricao: "Territórios, Regionais e Entes federativos",
                    hint: "Definição das áreas geográficas de atuação dos serviços, programas, para compartilhamento de links, etc",
                    restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
                    classeCss: "beje", link: [controller:"abrangenciaTerritorial"]));

            novaOpcaoMenu(new ItemMenuDTO(ordem: 1500L, descricao: "Importação de famílias",
                    hint: "Importação de planilhas de banco de dados do cadastro de famílias do seu serviço para uso no sistema",
                    restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
                    classeCss: "verde_oliva", link: [controller:"importacaoFamilias", action:"list"]));

            novaOpcaoMenu(new ItemMenuDTO(ordem: 1600L, descricao: "Operadores do sistema",
                    hint: "Criação e modificação dos usuários (operadores) do sistema, respectivos perfis e serviços a que estão vinculados",
                    restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
                    classeCss: "laranja", link: [controller:"usuarioSistema", action:"list"]));

            novaOpcaoMenu(new ItemMenuDTO(ordem: 1700L, descricao: "Serviços utilizando o sistema",
                    hint: "Criação e modificação dos serviços habilitados a usarem o sistema",
                    restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
                    classeCss: "verde_agua", link: [controller:"servicoSistema", action:"list"]));

            novaOpcaoMenu(new ItemMenuDTO(ordem: 1800L, descricao: "Programas, Vulnerabilidades, etc",
                    hint: "Criação e modificação de programas, vulnerabilidades, ações e outros marcadores disponíveis para serem aplicados às famílias",
                    restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
                    classeCss: "rosa", link: [controller:"marcador", action:"list"]));
        }

        novaOpcaoMenu(new ItemMenuDTO(ordem: 1900L, descricao: "Configurações do serviço",
                hint: "Alteração das informações institucionais do seu serviço, como nome, endereço, etc",
                restricaoAcesso: DefinicaoPapeis.STR_USUARIO,
                classeCss: "verde_agua", link: [controller:"servicoSistema", action:"editCurrent"]));

//        novaOpcaoMenu(new ItemMenuDTO(ordem: XXX, descricao: XXX,
//                hint: XXX,
//                restricaoAcesso: DefinicaoPapeis.STR_SUPER_USER,
//                classeCss: XXX, link: [controller:XXX, action:XXX]));

    }

    public List<ItemMenuDTO> getMenusDisponiveis() {
        return menusDisponiveis.findAll { it != null}.sort { it.ordem };
    }

}
