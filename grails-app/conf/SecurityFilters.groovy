/**
 * Filtro que deve garantir que o sistema de seguranca esta devidamente configurado antes de se acessar qualquer parte da aplicacao
 * (exceto as actions
 */
class SecurityFilters {

    def segurancaService

    def filters = {
        loginCheck(controller: '*', actionExclude: '*') {
            before = {
                if (controllerName == "inicio" && actionName == "actionInicial")
                    return true;

                if (controllerName == "inicio" && actionName == "servicoEscolhido")
                    return true;

                if (controllerName == "inicio" && actionName == "escolheServicoSistema")
                    return true;

                if (controllerName == "login" || controllerName == "logout")
                    return true;

                //Testa se o componente de ServicoLogado ja esta presente no ambiente de segurança
                if (segurancaService.servicoLogado)
                    return true;

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