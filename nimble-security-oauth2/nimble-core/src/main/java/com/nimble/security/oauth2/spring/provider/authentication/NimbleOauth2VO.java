package com.nimble.security.oauth2.spring.provider.authentication;

/**
 * Date: 5/9/13
 * Time: 1:19 PM
 */
public class NimbleOauth2VO {
    private String id;
    /*private String clientAuthorizationId;
    private int userAuthorizationId;*/
    private boolean authenticated;
    private Object details;

    public NimbleOauth2VO() {
    }

    public NimbleOauth2VO(String id, /*String clientAuthorizationId, int userAuthorizationId,*/ boolean authenticated, Object details) {
        this.id = id;
        /*this.clientAuthorizationId = clientAuthorizationId;
        this.userAuthorizationId = userAuthorizationId;*/
        this.authenticated = authenticated;
        this.details = details;
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    /*String getClientAuthorizationId() {
        return clientAuthorizationId;
    }

    void setClientAuthorizationId(String clientAuthorizationId) {
        this.clientAuthorizationId = clientAuthorizationId;
    }

    int getUserAuthorizationId() {
        return userAuthorizationId;
    }

    void setUserAuthorizationId(int userAuthorizationId) {
        this.userAuthorizationId = userAuthorizationId;
    }*/

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
