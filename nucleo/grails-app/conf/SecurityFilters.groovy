/**
 * Filtro que deve garantir que o sistema de seguranca esta devidamente configurado antes de se acessar qualquer parte da aplicacao
 * (exceto algumas actions)
 */
class SecurityFilters {

    def segurancaService

    /**
     * Define o servicoSistema do usuario logado e guarda na sessao. Em caso de usuario admin, direciona para tela de
     * escolha do servico a ser utilizado.
     */
    def filters = {
        loginCheck(controller: '*', actionExclude: '*') {
            before = {
                if (controllerName == "inicio" && actionName in["actionInicial","servicoEscolhido","escolheServicoSistema","alive"])
                    return true;

                if (controllerName == "login" || controllerName == "loginApoiaSuas" || controllerName == "logout")
                    return true;

                if (controllerName == "importacaoFamilias" && actionName == "restUpload")
                    return true;

                //Testa se o componente de ServicoLogado ja esta presente no ambiente de segurança
//                try {
                    if (segurancaService.servicoLogado)
                        return true;
//                } catch (Exception e) {
//
//                }

                //Seta o servicoLogado no ambiente de seguranca ou direcionado para a tela de selecao do servico (usuario admin)
                if (segurancaService.isSuperUser()) {
                    redirect(controller: "inicio", action: "escolheServicoSistema")
                    return false
                } else {
                    segurancaService.setServicoLogado(segurancaService.usuarioLogado.servicoSistemaSeguranca)
                }
            }
        }
    }
}