package com.nimble.security.oauth2.spring.provider.authentication;

import com.nimble.security.oauth2.spring.provider.NimbleOAuth2Authentication;
import com.nimble.security.oauth2.spring.provider.authentication.dao.AuthenticationDAO;
import com.nimble.security.oauth2.spring.provider.authentication.dao.AuthorizationRequestDAO;
import com.nimble.security.oauth2.spring.provider.authentication.dao.OAuth2AuthenticationDAO;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Date: 5/6/13
 * Time: 3:56 PM
 */
public class NimbleOauth2AuthenticationManager implements Oauth2AuthenticationManager<NimbleOAuth2Authentication> {
    private AuthorizationRequestDAO authorizationRequestDAO;
    private AuthenticationDAO authenticationDAO;
    private OAuth2AuthenticationDAO oAuth2AuthenticationDAO;

    public void setoAuth2AuthenticationDAO(OAuth2AuthenticationDAO oAuth2AuthenticationDAO) {
        this.oAuth2AuthenticationDAO = oAuth2AuthenticationDAO;
    }

    public void setAuthorizationRequestDAO(AuthorizationRequestDAO authorizationRequestDAO) {
        this.authorizationRequestDAO = authorizationRequestDAO;
    }

    public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public NimbleOAuth2Authentication storeOAuth2Authentication(OAuth2Authentication authentication) {
        //will deconstruct the pieces of the oauth authentication for storage
        AuthorizationRequest clientRequest = authentication.getAuthorizationRequest();
        //save the client request (create or update, depending on if have an existing id)
        //returning the id of the authorization - could just return an existing id in the case of update
        int clientRequestId = authorizationRequestDAO.storeAuthorizationRequest(clientRequest);

        //get the user authentication piece out to store. Same comments regarding id apply here
        Authentication userAuthentication = authentication.getUserAuthentication();
        int userAuthId = authenticationDAO.storeAuthentication(userAuthentication);

        NimbleOAuth2Authentication auth2Authentication = new NimbleOAuth2Authentication(clientRequest, userAuthentication);
        auth2Authentication.setUserAuthId(userAuthId);
        auth2Authentication.setClientRequestId(clientRequestId);
        auth2Authentication.setId(auth2Authentication.getId());
        //now store the aggregated oauth authentication which is a composition of client auth and user auth
        int auth2Id = oAuth2AuthenticationDAO.storeOAuth2Authentication(auth2Authentication);
        auth2Authentication.setId(auth2Id);

        return auth2Authentication;
    }

    public int getIdForOAuth2Authentication(OAuth2Authentication authentication) {
        if(authentication instanceof IdAwareOAuth2Authentication) {
            return ((IdAwareOAuth2Authentication)authentication).getId();
        } else {
            return storeOAuth2Authentication(authentication).getId();
        }
    }



}
