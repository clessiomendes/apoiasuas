package org.apoiasuas.seguranca

import org.apache.commons.lang.builder.HashCodeBuilder

class UsuarioSistemaPapel implements Serializable {

	private static final long serialVersionUID = 1

	UsuarioSistema usuarioSistema
	Papel papel

	boolean equals(other) {
		if (!(other instanceof UsuarioSistemaPapel)) {
			return false
		}

		other.usuarioSistema?.id == usuarioSistema?.id &&
		other.papel?.id == papel?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (usuarioSistema) builder.append(usuarioSistema.id)
		if (papel) builder.append(papel.id)
		builder.toHashCode()
	}

	static UsuarioSistemaPapel get(long usuarioSistemaId, long papelId) {
		UsuarioSistemaPapel.where {
			usuarioSistema == UsuarioSistema.load(usuarioSistemaId) &&
			papel == Papel.load(papelId)
		}.get()
	}

	static boolean exists(long usuarioSistemaId, long papelId) {
		UsuarioSistemaPapel.where {
			usuarioSistema == UsuarioSistema.load(usuarioSistemaId) &&
			papel == Papel.load(papelId)
		}.count() > 0
	}

//TODO: remover metodos create, remove e removeAll de UsuarioSistemaPapel - manipulacao de persistencia em uma classe que deveria ser POJO
	static UsuarioSistemaPapel create(UsuarioSistema usuarioSistema, Papel papel, boolean flush = false) {
		def instance = new UsuarioSistemaPapel(usuarioSistema: usuarioSistema, papel: papel)
		instance.save(flush: flush, insert: true)
		instance
	}

	static boolean remove(UsuarioSistema u, Papel r, boolean flush = false) {
		if (u == null || r == null) return false

		int rowCount = UsuarioSistemaPapel.where {
			usuarioSistema == UsuarioSistema.load(u.id) &&
			papel == Papel.load(r.id)
		}.deleteAll()

		if (flush) { UsuarioSistemaPapel.withSession { it.flush() } }

		rowCount > 0
	}

	static void removeAll(UsuarioSistema u, boolean flush = false) {
		if (u == null) return

		UsuarioSistemaPapel.where {
			usuarioSistema == UsuarioSistema.load(u.id)
		}.deleteAll()

		if (flush) { UsuarioSistemaPapel.withSession { it.flush() } }
	}

	static void removeAll(Papel r, boolean flush = false) {
		if (r == null) return

		UsuarioSistemaPapel.where {
			papel == Papel.load(r.id)
		}.deleteAll()

		if (flush) { UsuarioSistemaPapel.withSession { it.flush() } }
	}

	static constraints = {
		papel validator: { Papel papel, UsuarioSistemaPapel usuarioPapel ->
			if (usuarioPapel.usuarioSistema == null) return
			boolean existing = false
			UsuarioSistemaPapel.withNewSession {
                if (usuarioPapel?.usuarioSistema && papel)
				    existing = UsuarioSistemaPapel.exists(usuarioPapel.usuarioSistema.id, papel.id)
			}
			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
		id composite: ['papel', 'usuarioSistema']
		version false
	}
}
