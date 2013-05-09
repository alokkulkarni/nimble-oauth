package com.nimble.security.oauth2.spring.provider.token.dao;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;

/**
 * Date: 5/2/13
 * Time: 12:29 PM
 */
public interface RefreshTokenDAO {
    void storeRefreshToken(OAuth2RefreshToken refreshToken, int authId);

    OAuth2RefreshToken readRefreshToken(String tokenValue);

    void removeRefreshToken(OAuth2RefreshToken token);


}
