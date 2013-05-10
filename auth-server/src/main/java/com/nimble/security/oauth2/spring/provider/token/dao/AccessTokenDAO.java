package com.nimble.security.oauth2.spring.provider.token.dao;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;

/**
 * Date: 5/2/13
 * Time: 12:20 PM
 */
public interface AccessTokenDAO<T extends OAuth2AccessToken> {
    T readAccessToken(String tokenValue);

    void removeAccessToken(T token);

    T getAccessToken(OAuth2Authentication authentication, String authenticationId);

    Collection<T> findTokensByUserName(String userName);

    Collection<T> findTokensByClientId(String clientId);

    void storeAccessToken(T token, String authenticationId, OAuth2Authentication authentication);

    void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken);
}
