package com.nimble.security.oauth2.spring.provider.authentication.dao;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Date: 5/2/13
 * Time: 3:00 PM
 */
public interface OAuth2AuthenticationDAO<A extends OAuth2Authentication>  {
    OAuth2Authentication readAuthentication(String token);

    /**
     * @param authentication
     * @return - the id of the authentication object
     */
    int storeOAuth2Authentication(A authentication);

}
