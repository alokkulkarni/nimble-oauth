package com.nimble.security.oauth2.spring.provider.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 5/6/13
 * Time: 3:41 PM
 */
public class NimbleAuthentication implements Authentication {
    private String oauth2AuthorizationId;
    private String userName;
    private boolean authenticated;
    private Object principal;
    private String nimbleToken;
    private Set<GrantedAuthority> authorities;

    public NimbleAuthentication() {
    }

    public NimbleAuthentication(Authentication authentication) {
        this(authentication, null);
    }

    public NimbleAuthentication(Authentication authentication, String nimbleToken) {
        this(authentication.getName(), authentication.isAuthenticated(), authentication.getPrincipal(), nimbleToken, new HashSet<GrantedAuthority>(authentication.getAuthorities()));
    }

    public NimbleAuthentication(String userName, boolean authenticated, Object principal, String nimbleToken, Set<GrantedAuthority> authorities) {
        this.userName = userName;
        this.authenticated = authenticated;
        this.principal = principal;
        this.nimbleToken = nimbleToken;
        this.authorities = authorities;
    }

    public NimbleAuthentication(String authenticationId, String userName, boolean authenticated, Object principal, String nimbleToken, Set<GrantedAuthority> authorities) {
        this(userName, authenticated, principal, nimbleToken, authorities);
        this.oauth2AuthorizationId = authenticationId;
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

    public String getOauth2AuthorizationId() {
        return oauth2AuthorizationId;
    }

    public void setOauth2AuthorizationId(String oauth2AuthorizationId) {
        this.oauth2AuthorizationId = oauth2AuthorizationId;
    }

    public String getNimbleToken() {
        return nimbleToken;
    }

    public void setNimbleToken(String nimbleToken) {
        this.nimbleToken = nimbleToken;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
