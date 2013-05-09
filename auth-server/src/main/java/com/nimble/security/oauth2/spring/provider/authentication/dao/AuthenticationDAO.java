package com.nimble.security.oauth2.spring.provider.authentication.dao;

import org.springframework.security.core.Authentication;

/**
 * Date: 5/2/13
 * Time: 3:00 PM
 */
public interface AuthenticationDAO {
    Authentication readAuthentication(int id);

    /**
     * @param authentication
     * @return - the id of the authentication object
     */
    int storeAuthentication(Authentication authentication);

}
