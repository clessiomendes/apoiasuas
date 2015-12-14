package org.apoiasuas.seguranca

import grails.validation.Validateable

@Validateable
class UsuarioSistema {

	transient springSecurityService

    public static final int SEM_SELECAO = -1
    public static final int SELECAO_ALGUM_TECNICO = -2
    public static final int SELECAO_NENHUM_TECNICO = -3

	Date dateCreated, lastUpdated;
	UsuarioSistema criador, ultimoAlterador;
	String nomeCompleto;
	String username;
    String matricula
    String password

	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	boolean enabled

    String papel //transiente

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_usuario_sistema']
//		accountExpired(defaultValue:DataSourceType.getFalse())
//		accountLocked(defaultValue:DataSourceType.getFalse())
//		passwordExpired(defaultValue:DataSourceType.getFalse())
//		password(defaultValue:"'password'")
	}

	static transients = ['springSecurityService', 'papel']

	static constraints = {
		nomeCompleto(nullable: false)
		username(nullable: false, unique: true)
//		perfil(nullable: false)
		password(nullable: false)
		accountExpired(nullable: false)
		accountLocked(nullable: false)
		passwordExpired(nullable: false)
		enabled(nullable: false)

//Manter criador e ultimoAlterador obrigatorios pode ser um problema na inicializacao de usuarios
//		criador(nullable: false)
//		ultimoAlterador(nullable: false)
	}

	Set<Papel> getAuthorities() {
		//TODO: tirar manipulacao de banco de dados implementada na classe de dominio
		UsuarioSistemaPapel.findAllByUsuarioSistema(this).collect { it.papel }
	}

/*
    boolean pode(List<DefinicaoPapeis> testadas) {
        getAuthorities().each { atribuida ->
            testadas.each { testada ->
                if (testada.toString() == atribuida.toString())
                    return true
            }
        }
        return false
    }
*/

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
}
