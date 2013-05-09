package com.nimble.security.oauth2.spring.provider.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

/**
 * Date: 5/6/13
 * Time: 3:41 PM
 */
public class NimbleClientAuthentication implements Authentication {
    private int id;
    private String userName;
    private boolean authenticated;
    private Object principal;
    private String nimbleToken;
    private Set<GrantedAuthority> authorities;

    public NimbleClientAuthentication() {
    }

    public NimbleClientAuthentication(String userName, boolean authenticated, Object principal, String nimbleToken, Set<GrantedAuthority> authorities) {
        this.userName = userName;
        this.authenticated = authenticated;
        this.principal = principal;
        this.nimbleToken = nimbleToken;
        this.authorities = authorities;
    }

    public NimbleClientAuthentication(int id, String userName, boolean authenticated, Object principal, String nimbleToken, Set<GrantedAuthority> authorities) {
        this(userName, authenticated, principal, nimbleToken, authorities);
        this.id = id;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Object getCredentials() {
        return null;
    }

    public Object getDetails() {
        return null;
    }

    public Object getPrincipal() {
        return principal;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public String getName() {
        return userName;
    }
}
