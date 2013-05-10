package com.nimble.security.oauth2.spring.provider.authentication;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Date: 5/6/13
 * Time: 1:49 PM
 */
public interface Oauth2AuthenticationManager<A extends OAuth2Authentication> {
    public A storeOAuth2Authentication(OAuth2Authentication authentication);

    public A readAuthenticationByAccessToken(String accessToken);

    public A readAuthenticationByRefreshToken(String accessToken);

    public String getIdForOAuth2Authentication(OAuth2Authentication authentication);
}
