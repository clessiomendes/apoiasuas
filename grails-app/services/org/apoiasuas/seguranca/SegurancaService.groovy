package org.apoiasuas.seguranca

import grails.transaction.Transactional
import grails.util.Environment
import org.apoiasuas.util.AmbienteExecucao

class SegurancaService {

    def springSecurityService

    static transactional = false

    @Transactional(readOnly = true)
    UsuarioSistema getUsuarioLogado() {
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
    void gravaNovoUsuario(UsuarioSistema usuarioSistema) {
        usuarioSistema.accountExpired = false
        usuarioSistema.accountLocked = false
        usuarioSistema.passwordExpired = false
        usuarioSistema.enabled = true
        usuarioSistema.criador = getUsuarioLogado()
        usuarioSistema.ultimoAlterador = getUsuarioLogado()
        usuarioSistema.save()

        gravaPapel(usuarioSistema)
    }

    @Transactional
    void atualizaUsuario(UsuarioSistema usuarioSistema) {
        usuarioSistema.ultimoAlterador = getUsuarioLogado()
        usuarioSistema.save()

        gravaPapel(usuarioSistema)
    }

    @Transactional
    void apagaUsuario(UsuarioSistema usuarioSistema) {
        //TODO: Validar que exista pelo menos um usuario com papel de administrador
        UsuarioSistemaPapel.findAllByUsuarioSistema(usuarioSistema)?.each {
            it.delete() //apaga papeis existentes
        }
        usuarioSistema.delete()
    }

    private void gravaPapel(UsuarioSistema usuarioSistemaInstance) {
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
        UsuarioSistemaPapel.findAllByUsuarioSistema(usuarioSistema).each { result << it.papel}
        return result
    }
}
