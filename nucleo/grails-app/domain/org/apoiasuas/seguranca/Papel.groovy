package org.apoiasuas.seguranca

class Papel {

	String authority

	static mapping = {
        id generator: 'native', params: [sequence: 'sq_papel']
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
