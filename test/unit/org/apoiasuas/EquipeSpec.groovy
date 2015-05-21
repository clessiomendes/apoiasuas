package org.apoiasuas

import grails.test.mixin.TestFor
import org.apoiasuas.seguranca.UsuarioSistema
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(UsuarioSistema)
class UsuarioSistemaSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test UsuarioSistema"() {
		mockForConstraintsTests UsuarioSistema;
		
		def operador = new UsuarioSistema(nomeCompleto: "Clessio Cunha Mendes", username: "admin");
		assert operador.validate();
		
		operador.setNomeCompleto(null);
		assert ! operador.validate();
    }
}
