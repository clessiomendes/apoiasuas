package org.apoiasuas.seguranca

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import grails.transaction.Transactional
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class ApoiaSuasDetailsService extends GormUserDetailsService {

    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least
     * one role, so we give a user with no granted roles this one which gets
     * past that restriction but doesn't grant anything.
     */
/*
    static final GrantedAuthority NO_ROLE = new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)

    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        return loadUserByUsername(username)
    }

    @Transactional(readOnly=true, noRollbackFor=[IllegalArgumentException, UsernameNotFoundException])
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UsuarioSistema user = UsuarioSistema.findByUsername(username)
        if (!user) throw new UsernameNotFoundException(
                'User not found', username)

        def authorities = user.authorities.collect {
            new GrantedAuthorityImpl(it.authority)
        }

        return new ApoiaSuasUser(user.username, user.password,
                user.enabled, !user.accountExpired, !user.passwordExpired,
                !user.accountLocked, authorities ?: NO_ROLES, user.id,
                user.servicoSistemaSeguranca.id)
    }
*/
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        GrailsUser tempUser = super.createUserDetails(user, authorities)
        return new ApoiaSuasUser(tempUser.username, tempUser.password, tempUser.enabled, tempUser.accountNonExpired,
                tempUser.credentialsNonExpired, tempUser.accountNonLocked, tempUser.authorities, tempUser.id)
    }

}