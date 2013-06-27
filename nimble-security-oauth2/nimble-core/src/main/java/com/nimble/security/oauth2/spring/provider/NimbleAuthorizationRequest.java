package com.nimble.security.oauth2.spring.provider;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;

import java.util.Collection;
import java.util.Map;

/**
 * Date: 5/6/13
 * Time: 3:13 PM
 */
public class NimbleAuthorizationRequest extends DefaultAuthorizationRequest {
    private String authorizationId;
    private String userName;

    public NimbleAuthorizationRequest(AuthorizationRequest request) {
        super(request.getAuthorizationParameters(), request.getApprovalParameters(), request.getClientId(), request.getScope());
        setApproved(request.isApproved());
        setAuthorities(request.getAuthorities());
        setRedirectUri(request.getRedirectUri());
        setResourceIds(request.getResourceIds());

    }

    public NimbleAuthorizationRequest(Map<String, String> authorizationParameters, Map<String, String> approvalParameters, String clientId, Collection<String> scope) {
        super(authorizationParameters, approvalParameters, clientId, scope);
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
