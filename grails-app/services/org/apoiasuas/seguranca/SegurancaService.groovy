package org.apoiasuas.seguranca

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import grails.util.Environment
import org.apoiasuas.util.AmbienteExecucao
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class SegurancaService {

    public static final int MIN_TAMANHO_SENHA = 4
    def springSecurityService

    static transactional = false

    @Transactional(readOnly = true)
    public UsuarioSistema getUsuarioLogado() {
        def usuarioLogado = springSecurityService.currentUser
        return usuarioLogado
    }

    @Transactional
    UsuarioSistema inicializaSeguranca() {
        DefinicaoPapeis.hierarquia.each { DefinicaoPapeis definicaoPapel ->
            geraPapelBD(definicaoPapel.pai)
        }

        def admin = UsuarioSistema.findByUsername('admin')
        if (!admin) {
            admin = new UsuarioSistema();
            admin.username = "admin"
            admin.password = "senha"
            admin.enabled = true
            admin.nomeCompleto = "Administrador"
            admin.accountExpired = false
            admin.accountLocked = false
            admin.passwordExpired = false
            admin.lastUpdated = new Date()
            admin.dateCreated = new Date()
            admin.save()

            UsuarioSistemaPapel assoc = new UsuarioSistemaPapel(usuarioSistema: admin, papel: Papel.findByAuthority(DefinicaoPapeis.SUPER_USER)).save()
        }

        //Cria usuarios de teste para cada papel previsto no sistema
        if (AmbienteExecucao.desenvolvimento) {
            DefinicaoPapeis.hierarquia.each { DefinicaoPapeis definicaoPapel ->
                if (definicaoPapel.pai != DefinicaoPapeis.SUPER_USER) {
                    String nomeUsuario = definicaoPapel.pai.substring('ROLE_'.size()).toLowerCase()
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

                        UsuarioSistemaPapel assoc = new UsuarioSistemaPapel(usuarioSistema: usuario, papel: Papel.findByAuthority(definicaoPapel.pai)).save()
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
            if (SpringSecurityUtils.ifNotGranted(DefinicaoPapeis.SUPER_USER)) {

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
    public ArrayList<UsuarioSistema> getOperadoresOrdenados() {
        ArrayList<UsuarioSistema> result = [usuarioLogado]
        ArrayList<UsuarioSistema> usuarios = UsuarioSistema.listOrderByUsername()
        usuarios.each {
            if (it.id != usuarioLogado.id)
                result << it
        }
        return result
    }

}
