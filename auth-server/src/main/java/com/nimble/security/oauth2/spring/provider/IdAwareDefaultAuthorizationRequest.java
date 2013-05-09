package com.nimble.security.oauth2.spring.provider;

import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;

import java.util.Collection;
import java.util.Map;

/**
 * Date: 5/6/13
 * Time: 3:13 PM
 */
public class IdAwareDefaultAuthorizationRequest extends DefaultAuthorizationRequest implements IdAwareAuthorizationRequest {
    private int id;

    public IdAwareDefaultAuthorizationRequest(Map<String, String> authorizationParameters, Map<String, String> approvalParameters, String clientId, Collection<String> scope) {
        super(authorizationParameters, approvalParameters, clientId, scope);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
