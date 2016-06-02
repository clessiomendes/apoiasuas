package org.apoiasuas.seguranca

import grails.plugin.springsecurity.userdetails.GrailsUser
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.springframework.security.core.GrantedAuthority

/**
 * Created by admin on 15/05/2016.
 */
class ApoiaSuasUser extends GrailsUser {

    public ServicoSistema servicoSistemaSessaoCorrente

    public ApoiaSuasUser(String username, String password, boolean enabled,
                        boolean accountNonExpired, boolean credentialsNonExpired,
                        boolean accountNonLocked, Collection<GrantedAuthority> authorities,
                        long id) {

        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities, id)
    }
}