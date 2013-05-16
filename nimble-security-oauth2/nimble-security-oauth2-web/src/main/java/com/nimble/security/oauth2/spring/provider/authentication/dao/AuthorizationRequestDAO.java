package com.nimble.security.oauth2.spring.provider.authentication.dao;

import org.springframework.security.oauth2.provider.AuthorizationRequest;

/**
 * Date: 5/6/13
 * Time: 4:02 PM
 */
public interface AuthorizationRequestDAO<A extends AuthorizationRequest> {
    AuthorizationRequest getAuthorizationRequest(String requestId);

    String storeAuthorizationRequest(AuthorizationRequest authorizationRequest);
}
