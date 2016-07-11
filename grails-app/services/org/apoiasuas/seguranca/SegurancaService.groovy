package org.apoiasuas.seguranca

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import org.apoiasuas.Link
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
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
    public static final String ATRIBUTO_SERVICO_SISTEMA = 'servicoSistemaSeguranca'
    public static final boolean HABILITAR_RESTRICAO_DE_ACESSO_AO_DOMINIO = true; //FIXME: manter habilitado em produção, se ainda não estiver

    def springSecurityService
    def authenticationManager
    def userDetailsService
    def linkService
    def familiaService
    def cidadaoService
    def abrangenciaTerritorialService

    static transactional = false

    @NotTransactional
    public UsuarioSistema getUsuarioLogado() {
        return springSecurityService.currentUser
    }

    @NotTransactional
    public ServicoSistema getServicoLogado() {
        return getPrincipal()?.servicoSistemaSessaoCorrente
    }

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

    @Transactional(readOnly = true)
    String getMunicipio() {
        ServicoSistema servicoLogado = getServicoLogado()
        servicoLogado.attach();
        return servicoLogado.endereco.municipio
    }

    @Transactional(readOnly = true)
    String getUF() {
        ServicoSistema servicoLogado = getServicoLogado()
        servicoLogado.attach();
        return servicoLogado.endereco.UF
    }

/*
    @Transactional(readOnly = true)
    String getDDDpadrao() {
        return "31"
    }
*/

    @Transactional(readOnly = true)
    public List<Papel> getPapeisUsuario(UsuarioSistema usuarioSistema = null) {
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

    /**
     * Lista todos os operadores filtrados para o Servico logado. Marca o operador logado com * e os operadores desabilitados
     * com -. Mostra primeiro os operadores habilitados e, por ultimo, os desabilitados
     */
    @Transactional(readOnly = true)
    public ArrayList<UsuarioSistema> getOperadoresOrdenados(boolean somenteHabilitados) {
        //adiciona o usuario logado no topo da lista, marcando com *
        getUsuarioLogado().discard() //desconecta dos objetos na cache da sessao hibernate
        UsuarioSistema logado = getUsuarioLogado()
        logado.username = "*"+logado.username
        ArrayList<UsuarioSistema> result = [logado]

        //Busa lista de usuarios ativos
        ArrayList<UsuarioSistema> usuarios = UsuarioSistema.findAllByServicoSistemaSegurancaAndEnabled(getServicoLogado(), true).sort {it.username}

        if (! somenteHabilitados) {
            //Busa lista de usuarios NAO ativos, marcados com -
            ArrayList<UsuarioSistema> usuariosNaoHabilitados = UsuarioSistema.findAllByServicoSistemaSegurancaAndEnabled(getServicoLogado(), false).sort {it.username}
            usuariosNaoHabilitados.each {
                it.discard() //desconecta dos objetos na cache da sessao hibernate
                UsuarioSistema usuario = UsuarioSistema.get(it.id)
                usuario.username = "-"+usuario.username
                usuarios << usuario
            }
        }

        //Remove usuario logado (duplicado) da lista ao montar o resultado
        usuarios.each {
            if (it.id != usuarioLogado.id)
                result << it
        }
        return result
    }

    @NotTransactional
    public boolean usuarioLogadoTemAcesso(String definicaoPapel) {
        SpringSecurityUtils.ifAnyGranted(definicaoPapel)
    }

    @NotTransactional
    public boolean isSuperUser() {
        return usuarioLogadoTemAcesso(DefinicaoPapeis.STR_SUPER_USER)
    }

    @NotTransactional
    public ApoiaSuasUser getPrincipal() {
        return springSecurityService.principal
    }

    @NotTransactional
    public void setServicoLogado(ServicoSistema servicoSistema) {
        ApoiaSuasUser principal = getPrincipal();
        principal.servicoSistemaSessaoCorrente = servicoSistema;
    }

    /**
     * Monta a hierarquia de abrangencias territoriais a partir do servico logado
     */
    @Transactional(readOnly = true)
    public List<AbrangenciaTerritorial> getAbrangenciasTerritoriaisAcessiveis() {
        //Recarrega a partir do banco para preencher o atributo "abrangenciaTerritorial"
        ServicoSistema servicoLogado = ServicoSistema.get(getServicoLogado().id);
        return abrangenciaTerritorialService.getAbrangenciasTerritoriaisMaes(servicoLogado.abrangenciaTerritorial)
    }

    /**
     * Confirma se o usuario logado tem permicoes de acesso a uma entidade de dominio qualquer (de acordo com as regras de seguranca de cada entidade)
     */
    @Transactional(readOnly = true)
    public void testaAcessoDominio(def entityObject) {
//        if (entityObject && temPropriedadeSeguranca(entityObject)) {

        if (    isSuperUser() //Administrador tem acesso restrito
                || (! HABILITAR_RESTRICAO_DE_ACESSO_AO_DOMINIO) //flag de teste para desabilitar checagem de seguranca
                || (! getUsuarioLogado())) //se estiver fora de um contexto de sessao de usuario, manter acesso irrestrito
            return; //Super usuário tem acesso irrestrito

        boolean temAcesso = true;
        if (entityObject instanceof Link)
            temAcesso = linkService.testaAcessoDominio(entityObject)
        else if (entityObject instanceof Familia)
            temAcesso = familiaService.testaAcessoDominio(entityObject)
        else if (entityObject instanceof Cidadao)
            temAcesso = cidadaoService.testaAcessoDominio(entityObject)
/*
        //Despresar classe de usuarios (porque usuarios aparecem como "criador/ultimoAlterador" de entidades que podem ser
        //vistas de servicos diferentes do que criou a entidade
        else if (entityObject instanceof UsuarioSistema)
            temAcesso = usuarioSistemaService.testaAcessoDominio(entityObject)
*/
        if (! temAcesso) {
            def e = new AcessoNegadoPersistenceException(getUsuarioLogado().username, entityObject.class.simpleName, entityObject.toString())
            log.error(e.getMessage())
            throw e;
        }

//        ServicoSistema servicoLogado = segurancaService.servicoLogado
//        ServicoSistema propriedadeServicoSistema = entityObject.metaClass.getMetaProperty(ATRIBUTO_SERVICO_SISTEMA)?.getProperty(entityObject)
//        if (propriedadeServicoSistema && servicoLogado && propriedadeServicoSistema.id != servicoLogado.id) {
//            return false
//        }
    }

}
