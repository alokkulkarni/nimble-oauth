package com.nimble.security.oauth2.spring.common;

import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.util.Date;

/**
 * Date: 5/2/13
 * Time: 4:44 PM
 */
public class NimbleRefreshToken extends DefaultExpiringOAuth2RefreshToken {
    private int id;
    private int timesUsed = 0;
    private boolean isEncrypted = false;
    private Date created;
    private Date updated;
    private String authenticationId = null;

    /**
     * @param value
     */
    public NimbleRefreshToken(String value, Date expiration, String authenticationId) {
        super(value, expiration);
        setAuthenticationId(authenticationId);
    }

    public NimbleRefreshToken(OAuth2RefreshToken copy) {
        super(copy.getValue(), ((copy instanceof DefaultExpiringOAuth2RefreshToken) ? ((DefaultExpiringOAuth2RefreshToken) copy).getExpiration() : null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }
}
