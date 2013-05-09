package com.nimble.security.oauth2.spring.provider.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Date: 5/2/13
 * Time: 3:22 PM
 */
public class IdAwareOAuth2Authentication extends OAuth2Authentication {
    private int id = -1;

    /**
     * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
     * authentication may be null.
     *
     * @param authorizationRequest The authorization request (must not be null).
     * @param userAuthentication   The user authentication (possibly null).
     */
    public IdAwareOAuth2Authentication(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        super(authorizationRequest, userAuthentication);
    }

    public IdAwareOAuth2Authentication(int id, AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        super(authorizationRequest, userAuthentication);
        setId(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
