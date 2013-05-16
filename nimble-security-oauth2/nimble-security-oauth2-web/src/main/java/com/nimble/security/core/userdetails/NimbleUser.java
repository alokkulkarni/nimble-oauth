package com.nimble.security.core.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Date: 4/11/13
 * Time: 4:50 PM
 */
public class NimbleUser extends User {
    private String nimbleToken;

    public String getNimbleToken() {
        return nimbleToken;
    }

    public void setNimbleToken(String nimbleToken) {
        this.nimbleToken = nimbleToken;
    }

    public NimbleUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String nimbleToken) {
        super(username, password, authorities);
        setNimbleToken(nimbleToken);
    }

    public NimbleUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String nimbleToken) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        setNimbleToken(nimbleToken);
    }
}
