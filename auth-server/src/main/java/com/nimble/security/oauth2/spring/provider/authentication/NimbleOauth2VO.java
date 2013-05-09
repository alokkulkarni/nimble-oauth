package com.nimble.security.oauth2.spring.provider.authentication;

/**
 * Date: 5/9/13
 * Time: 1:19 PM
 */
public class NimbleOauth2VO {
    private int id;
    private int clientAuthorizationId;
    private int userAuthorizationId;
    private boolean authenticated;
    private Object details;

    public NimbleOauth2VO() {
    }

    public NimbleOauth2VO(int id, int clientAuthorizationId, int userAuthorizationId, boolean authenticated, Object details) {
        this.id = id;
        this.clientAuthorizationId = clientAuthorizationId;
        this.userAuthorizationId = userAuthorizationId;
        this.authenticated = authenticated;
        this.details = details;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    int getClientAuthorizationId() {
        return clientAuthorizationId;
    }

    void setClientAuthorizationId(int clientAuthorizationId) {
        this.clientAuthorizationId = clientAuthorizationId;
    }

    int getUserAuthorizationId() {
        return userAuthorizationId;
    }

    void setUserAuthorizationId(int userAuthorizationId) {
        this.userAuthorizationId = userAuthorizationId;
    }

    boolean isAuthenticated() {
        return authenticated;
    }

    void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    Object getDetails() {
        return details;
    }

    void setDetails(Object details) {
        this.details = details;
    }
}
