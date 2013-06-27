package com.nimble.security.oauth2.spring.provider;

import org.springframework.security.oauth2.provider.AuthorizationRequest;

/**
 * Date: 5/6/13
 * Time: 4:06 PM
 */
public interface IdAwareAuthorizationRequest extends AuthorizationRequest {
    int getId();

    void setId(int id);
}
