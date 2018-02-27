package org.apoiasuas.seguranca

import grails.validation.Validateable
import org.apoiasuas.redeSocioAssistencial.ServicoSistema

@Validateable
class UsuarioSistema {

	transient springSecurityService

    public static final int SEM_SELECAO = -1
    public static final int SELECAO_ALGUM_TECNICO = -2
    public static final int SELECAO_NENHUM_TECNICO = -3

	Date dateCreated, lastUpdated;
	UsuarioSistema criador, ultimoAlterador;
    ServicoSistema servicoSistemaSeguranca;
	String nomeCompleto;
	String username;
    String matricula;
    String password;
    String configuracaoAgenda;

	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	boolean enabled

    String papel //transiente
    ApoiaSuasUser apoiaSuasUser //transiente

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_usuario_sistema']
//		accountExpired(defaultValue:DataSourceType.getFalse())
//		accountLocked(defaultValue:DataSourceType.getFalse())
//		passwordExpired(defaultValue:DataSourceType.getFalse())
//		password(defaultValue:"'password'")
	}

	static transients = ['springSecurityService', 'papel', 'apoiaSuasUser']

	static constraints = {
		nomeCompleto(nullable: false)
        servicoSistemaSeguranca(nullable: false)
		username(nullable: false, unique: true)
//		perfil(nullable: false)
		password(nullable: false, bindable: false) //segurança - impede que se tente passar uma mudanca de senha no usuario diretamente como parametro de request
		accountExpired(nullable: false)
		accountLocked(nullable: false)
		passwordExpired(nullable: false)
        springSecurityService(nullable: true)
        enabled(nullable: false)
		papel(nullable: true, bindable: true)
        configuracaoAgenda(nullable: true, maxSize: 10000)

//Manter criador e ultimoAlterador obrigatorios pode ser um problema na inicializacao de usuarios
//		criador(nullable: false)
//		ultimoAlterador(nullable: false)
	}

	Set<Papel> getAuthorities() {
		//TODO: tirar manipulacao de banco de dados implementada na classe de dominio
		UsuarioSistemaPapel.findAllByUsuarioSistema(this).collect { it.papel }
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}

	String toString() { return username }

	String getPapel() {
		return papel
	}

	void setPapel(String papel) {
		this.papel = papel
	}

    boolean temPerfil(String definicaoPapel) {
        boolean result = false
        authorities?.each { papel ->
            if (papel.authority == definicaoPapel)
                result = true
        }
        return result
    }

    /**
     * Retorna o servicoSistema sendo utilizado pelo usuario (logado). Em geral, sera o servicoSistema associado ao usuario,
     * mas eventualmente pode ser um servico diferente, escolhido durante o login (somente util para o usuario admin)
     * @return
     */
    public ServicoSistema getApoiaSuasUser() {
        return apoiaSuasUser ?: servicoSistemaSeguranca
    }
}
