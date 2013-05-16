package com.nimble.security.oauth2.spring.common;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Date;

/**
 * Date: 5/2/13
 * Time: 4:44 PM
 */
public class NimbleAccessToken extends DefaultOAuth2AccessToken {
    private int id;
    private boolean isEncrypted = false;
    private Date created;
    private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public NimbleAccessToken(String value) {
        super(value);
    }

    public NimbleAccessToken(OAuth2AccessToken accessToken) {
        super(accessToken);
    }
}
