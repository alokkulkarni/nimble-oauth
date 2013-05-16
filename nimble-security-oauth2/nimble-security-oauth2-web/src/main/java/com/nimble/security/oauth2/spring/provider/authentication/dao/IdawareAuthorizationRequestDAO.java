package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.oauth2.spring.provider.IdAwareAuthorizationRequest;

/**
 * Date: 5/6/13
 * Time: 4:02 PM
 */
public interface IdawareAuthorizationRequestDAO<A extends IdAwareAuthorizationRequest> extends AuthorizationRequestDAO<A> {
    A getAuthorizationRequestById(int id);
}
