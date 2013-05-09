package com.nimble.security.oauth2.spring.provider;

import com.nimble.security.oauth2.spring.provider.authentication.IdAwareOAuth2Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.HashMap;

/**
 * Date: 5/6/13
 * Time: 3:56 PM
 */
public class NimbleOAuth2Authentication extends IdAwareOAuth2Authentication {
    private int clientRequestId;
    private int userAuthId;

    /**
     * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
     * authentication may be null.
     *
     * @param authorizationRequest The authorization request (must not be null).
     * @param userAuthentication   The user authentication (possibly null).
     */
    public NimbleOAuth2Authentication(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        super(authorizationRequest, userAuthentication);
    }

    public NimbleOAuth2Authentication(AuthorizationRequest authorizationRequest, Authentication userAuthentication, int clientRequestId, int userAuthId) {
        super(authorizationRequest, userAuthentication);
        this.clientRequestId = clientRequestId;
        this.userAuthId = userAuthId;
    }

    public NimbleOAuth2Authentication(int id, AuthorizationRequest authorizationRequest, Authentication userAuthentication, int clientRequestId, int userAuthId) {
        super(id, authorizationRequest, userAuthentication);
        this.clientRequestId = clientRequestId;
        this.userAuthId = userAuthId;
    }

    public NimbleOAuth2Authentication() {
        //the super class needs one or the other of the constructors
        super(new DefaultAuthorizationRequest(new HashMap<String, String>()), null);
    }

    public int getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(int clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    public int getUserAuthId() {
        return userAuthId;
    }

    public void setUserAuthId(int userAuthId) {
        this.userAuthId = userAuthId;
    }
}
