package com.nimble.security.oauth2.spring.provider;

import com.nimble.security.oauth2.spring.provider.authentication.IdAwareOAuth2Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Date: 5/6/13
 * Time: 3:56 PM
 */
public class NimbleOAuth2Authentication extends IdAwareOAuth2Authentication {
    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
