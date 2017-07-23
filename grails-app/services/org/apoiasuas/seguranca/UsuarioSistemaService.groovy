package org.apoiasuas.seguranca

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UsuarioSistemaService {

    def segurancaService

    @Transactional
    public boolean apagaUsuario(UsuarioSistema usuarioSistema) {
        UsuarioSistemaPapel.findAllByUsuarioSistema(usuarioSistema)?.each {
            it.delete() //apaga papeis existentes
        }
        //TODO: Validacoes pendentes -> Pelo menos um usuario administrador habilitado.
        usuarioSistema.delete()

        return true
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
            usuarioSistema.criador = segurancaService.getUsuarioLogado()
            usuarioSistema.servicoSistemaSeguranca = segurancaService.getServicoLogado()
            if (! senha)
                erroSenha = "Senha é ogragatória"
        }

        usuarioSistema.ultimoAlterador = segurancaService.getUsuarioLogado()

        if (senha || confirmacaoSenha) {
            if (senha != confirmacaoSenha)
                erroSenha = "Senhas digitadas não conferem"
            if (senha?.length() < SegurancaService.MIN_TAMANHO_SENHA)
                erroSenha = "Senhas devem ter pelo menos ${SegurancaService.MIN_TAMANHO_SENHA} dígitos"
            usuarioSistema.password = senha
        }

        usuarioSistema.validate()

        validacoesEspecificas: { //Validacoes especificas

            //permissoes exclusivas de administradores
            if (SpringSecurityUtils.ifNotGranted(DefinicaoPapeis.STR_SUPER_USER)) {

                //Valida se um operador esta tentando alterar outro operador que nao ele proprio
                if (segurancaService.getUsuarioLogado().id != usuarioSistema.id)
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

    /**
     * Retorna um resultado PAGINADO filtrado por login/nome ou servicoSistema e ordenado por nomeCompleto
     * O mesmo parametro loginOuNome e usado para uma busca do tipo like em ambos os campos username e nomeCompleto
     * Os parametros offset, max e sort sao responsaveis pela paginacao
     */
    public def listUsuarios(FiltroUsuarioSistemaCommand filtro, def offset, def max) {
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

    public UsuarioSistema getUsuarioSistema(Long id) {
        return UsuarioSistema.get(id);
    }

}
