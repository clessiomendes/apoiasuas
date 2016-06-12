package org.apoiasuas.seguranca

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.AmbienteExecucao
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

class SegurancaService {

    public static final String LOGIN_AMD = "admin"
    public static final int MIN_TAMANHO_SENHA = 4
    def springSecurityService
    def authenticationManager
    def userDetailsService

    static transactional = false

    public UsuarioSistema getUsuarioLogado() {
        return springSecurityService.currentUser
    }


    public ServicoSistema getServicoLogado() {
        return getPrincipal()?.servicoSistemaSessaoCorrente
    }
/*
    public void setServicoLogado(ServicoSistema servicoSistemaSessao) {
        getUsuarioLogado()?.apoiaSuasUser = servicoSistemaSessao
    }
*/

    /**
     * Verifica usuario e senha e retorna o registro do banco de dados ou nulo.
     */
    @Transactional(readOnly = true)
    public UsuarioSistema autentica(String login, String senha, String papel) {
        try {
            //Primeiro precisamos construir um "user detail" aa partir do login
            UsuarioSistema user = UsuarioSistema.findByUsername(login)
            ApoiaSuasUser apoiaSuasUser = userDetailsService.createUserDetails(user, [])
            //passamos o "user detail" (do nosso tipo especializado ApoiaSuasUser) para o mecanismo de autenticacao do spring
            Authentication token = new UsernamePasswordAuthenticationToken(apoiaSuasUser, senha);
            token = authenticationManager.authenticate(token)
            if (token.authenticated && token.authorities.contains(new SimpleGrantedAuthority(papel))) {
                //E necessario atualizar o token recem gerado no contexto de seguranca do spring
                //http://stackoverflow.com/a/7903912/1916198
                SecurityContextHolder.getContext().setAuthentication(token)
                setServicoLogado(user.servicoSistemaSeguranca)
                return user
            }
            else
                return null
        } catch (BadCredentialsException e) {
            log.error(e.getMessage())
            return null
        }
    }

    @Transactional
    public UsuarioSistema inicializaSeguranca(ServicoSistema servicoAdm) {
        DefinicaoPapeis.getHierarquia().each { DefinicaoPapeis definicaoPapel ->
            geraPapelBD(definicaoPapel.definicaoPapel)
        }

        def admin = UsuarioSistema.findByUsername(LOGIN_AMD)
        if (!admin) {
            admin = new UsuarioSistema();
            admin.servicoSistemaSeguranca = servicoAdm
            admin.username = LOGIN_AMD
            admin.password = "senha"
            admin.enabled = true
            admin.nomeCompleto = "Administrador"
            admin.accountExpired = false
            admin.accountLocked = false
            admin.passwordExpired = false
            admin.lastUpdated = new Date()
            admin.dateCreated = new Date()
            admin.save()

            UsuarioSistemaPapel assoc = new UsuarioSistemaPapel(usuarioSistema: admin, papel: Papel.findByAuthority(DefinicaoPapeis.STR_SUPER_USER)).save()
        }

        //Cria usuarios de teste para cada papel previsto no sistema
        if (AmbienteExecucao.desenvolvimento) {
            DefinicaoPapeis.getHierarquia().each { DefinicaoPapeis definicaoPapel ->
                if (definicaoPapel.definicaoPapel != DefinicaoPapeis.STR_SUPER_USER) {
                    String nomeUsuario = definicaoPapel.definicaoPapel.substring('ROLE_'.size()).toLowerCase()
                    def usuario = UsuarioSistema.findByUsername(nomeUsuario)
                    if (!usuario) {
                        usuario = new UsuarioSistema();
                        usuario.username = nomeUsuario
                        usuario.password = "senha"
                        usuario.enabled = true
                        usuario.nomeCompleto = "Usuario "+nomeUsuario
                        usuario.accountExpired = false
                        usuario.accountLocked = false
                        usuario.passwordExpired = false
                        usuario.criador = admin
                        usuario.ultimoAlterador = admin
                        usuario.lastUpdated = new Date()
                        usuario.dateCreated = new Date()
                        usuario.save()

                        UsuarioSistemaPapel assoc = new UsuarioSistemaPapel(usuarioSistema: usuario, papel: Papel.findByAuthority(definicaoPapel.definicaoPapel)).save()
                    }
                }
            }
        }

        return admin
    }

    private Papel geraPapelBD(String papel) {
        def result = Papel.findByAuthority(papel)
        if (!result)
            result = new Papel(authority: papel).save()
        return result
    }

    @Transactional
    /**
     * Grava usuário ou retorna uma mensagem de erro em caso de violacao de regras de integridade
     */
    public boolean gravaUsuario(UsuarioSistema usuarioSistema, String senha, String confirmacaoSenha) {
        String erroSenha = null

        //Criacao de novo usuario
        if (! usuarioSistema.id) {
            usuarioSistema.accountExpired = false
            usuarioSistema.accountLocked = false
            usuarioSistema.passwordExpired = false
            usuarioSistema.criador = getUsuarioLogado()
            if (! senha)
                erroSenha = "Senha é ogragatória"
        }

        usuarioSistema.ultimoAlterador = getUsuarioLogado()

        if (senha || confirmacaoSenha) {
            if (senha != confirmacaoSenha)
                erroSenha = "Senhas digitadas não conferem"
            if (senha?.length() < MIN_TAMANHO_SENHA)
                erroSenha = "Senhas devem ter pelo menos ${MIN_TAMANHO_SENHA} dígitos"
            usuarioSistema.password = senha
        }

        usuarioSistema.validate()

        validacoesEspecificas: { //Validacoes especificas

            //permissoes exclusivas de administradores
            if (SpringSecurityUtils.ifNotGranted(DefinicaoPapeis.STR_SUPER_USER)) {

                //Valida se um operador esta tentando alterar outro operador que nao ele proprio
                if (usuarioLogado.id != usuarioSistema.id)
                    usuarioSistema.errors.reject("", "Você não tem permissão para alterar outros operadores")

                //Valida se um operador esta tentando modificar campos que ele nao pode
                if (usuarioSistema.enabled != usuarioSistema.getPersistentValue("enabled"))
                    usuarioSistema.errors.reject("", "Você não tem permissão para habilitar/desabilitar operadores")
                if (usuarioSistema.username != usuarioSistema.getPersistentValue("username"))
                    usuarioSistema.errors.reject("", "Você não tem permissão para alterar um apelido de operador")
                if (usuarioSistema.papel)
                    usuarioSistema.errors.reject("", "Você não tem permissão para definir papeis de operadores")
            }

            //TODO: Validacoes pendentes -> Pelo menos um usuario administrador habilitado.
//                  usuarioSistema.errors.reject("Deve haver pelo menos um operador administrador habilitado")
        }

        if (erroSenha || usuarioSistema.errors.hasErrors()) {
            usuarioSistema.discard()
            if (erroSenha)
                usuarioSistema.errors.rejectValue("password", "", erroSenha)
            return false
        } else {
            usuarioSistema.save()
            gravaPapel(usuarioSistema)
            return true //registro gravado com sucesso
        }

    }

/*
    @Transactional
    void atualizaUsuario(UsuarioSistema usuarioSistema) {
        usuarioSistema.ultimoAlterador = getUsuarioLogado()
        usuarioSistema.save()

        gravaPapel(usuarioSistema)
    }
*/

    @Transactional
    public boolean apagaUsuario(UsuarioSistema usuarioSistema) {
        UsuarioSistemaPapel.findAllByUsuarioSistema(usuarioSistema)?.each {
            it.delete() //apaga papeis existentes
        }
        //TODO: Validacoes pendentes -> Pelo menos um usuario administrador habilitado.
        usuarioSistema.delete()

        return true
    }

    private void gravaPapel(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance.papel)
            return //Papel eh obrigatorio na tela do CRUD QUANDO ELE EH EXIBIDO. Logo, a ausencia desta informacao indica que ela nao eh para ser modificada.

//Define o papel no sistema de seguranca
        Papel papel = Papel.findByAuthority(usuarioSistemaInstance.papel)
        boolean papelExistente = false
        UsuarioSistemaPapel.findAllByUsuarioSistema(usuarioSistemaInstance)?.each {
            if (it.papel == papel)
                papelExistente = true
            else
                it.delete() //apaga eventuais papeis anteriores
        }
        if (!papelExistente) //cria novo papel (quando ainda nao existir)
            new UsuarioSistemaPapel(usuarioSistema: usuarioSistemaInstance, papel: papel).save()
    }


//TODO: Criar parâmetros persistentes para o equipamento ao qual o usuário logado esta vinculado e buscar de la os dados
    @Transactional(readOnly = true)
    String getMunicipio() {
        return "Belo Horizonte"
    }

    @Transactional(readOnly = true)
    String getUF() {
        return "MG"
    }

    @Transactional(readOnly = true)
    String getDDDpadrao() {
        return "31"
    }

    @Transactional(readOnly = true)
    List<Papel> getPapeisUsuario(UsuarioSistema usuarioSistema = null) {
        if (! usuarioSistema)
            usuarioSistema = usuarioLogado
        List<Papel> result = []
        if (usuarioSistema.id)
            UsuarioSistemaPapel.findAllByUsuarioSistema(usuarioSistema).each { result << it.papel}
        return result
    }

    @Transactional(readOnly = true)
    public UsuarioSistema getAdmin() {
        //FIXME procurar usuario com papel admin
        return UsuarioSistema.findByUsername("admin")
    }

    @Transactional(readOnly = true)
    /**
     * Lista todos os operadores filtrados para o Servico logado
     */
    public ArrayList<UsuarioSistema> getOperadoresOrdenados(boolean somenteHabilitados) {
        //adiciona o usuario logado no topo da lista, marcando com *
        UsuarioSistema logado = getUsuarioLogado()
        logado.discard()
        logado.username = "*"+logado.username
        ArrayList<UsuarioSistema> result = [logado]

        //Busa lista de usuarios ativos
        ArrayList<UsuarioSistema> usuarios = UsuarioSistema.findAllByServicoSistemaSegurancaAndEnabled(getServicoLogado(), true).sort {it.username}

        if (! somenteHabilitados) {
            //Busa lista de usuarios NAO ativos, marcados com -
            ArrayList<UsuarioSistema> usuariosNaoHabilitados = UsuarioSistema.findAllByServicoSistemaSegurancaAndEnabled(getServicoLogado(), false).sort {it.username}
            usuariosNaoHabilitados.each {
                it.discard()
                it.username = "-"+it.username
                usuarios << it
            }
        }

        //Remove usuario logado (duplicado) da lista ao montar o resultado
        usuarios.each {
            if (it.id != usuarioLogado.id)
                result << it
        }
        return result
    }

    @Transactional(readOnly = true)
    public boolean temAcesso(String definicaoPapel) {
        SpringSecurityUtils.ifAnyGranted(definicaoPapel)
    }

    public boolean isSuperUser() {
        return temAcesso(DefinicaoPapeis.STR_SUPER_USER)
    }

    /**
     * Retorna um resultado PAGINADO filtrado por login/nome ou servicoSistema e ordenado por nomeCompleto
     * O mesmo parametro loginOuNome e usado para uma busca do tipo like em ambos os campos username e nomeCompleto
     * Os parametros offset, max e sort sao responsaveis pela paginacao
     */
    def listUsuarios(FiltroUsuarioSistemaCommand filtro, def offset, def max) {
        //converte parametro string para long
        Long idServicoSistema = filtro?.servicoSistema?.toString()?.matches("\\d+") ? Long.parseLong(filtro.servicoSistema) : null;

        return UsuarioSistema.createCriteria().list(offset: offset, max: max) {
            if (filtro.nome) {
                or { ilike("username", "%$filtro.nome%") ilike("nomeCompleto", "%$filtro.nome%") }
            }
            if (idServicoSistema) {
                eq("servicoSistemaSeguranca.id", idServicoSistema)
            }
            order("nomeCompleto")
        }
    }

    public ApoiaSuasUser getPrincipal() {
        return springSecurityService.principal
    }

    public void setServicoLogado(ServicoSistema servicoSistema) {
        ApoiaSuasUser principal = getPrincipal();
        principal.servicoSistemaSessaoCorrente = servicoSistema;
    }
}
