package com.nimble.security.oauth2.spring.provider.token;

import org.springframework.security.oauth2.provider.AuthorizationRequest;

/**
 * Date: 5/10/13
 * Time: 3:44 PM
 */
public interface AuthorizationRequestKeyGenerator<A extends AuthorizationRequest> {

    /**
     * @param authorizationRequest an AuthorizationRequest
     * @return a unique key identifying the authorization request
     */
    String extractKey(A authorizationRequest);
}
