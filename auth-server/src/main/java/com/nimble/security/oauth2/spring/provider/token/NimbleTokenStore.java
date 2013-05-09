package com.nimble.security.oauth2.spring.provider.token;

import com.nimble.security.oauth2.spring.provider.NimbleOAuth2Authentication;
import com.nimble.security.oauth2.spring.provider.authentication.Oauth2AuthenticationManager;
import com.nimble.security.oauth2.spring.provider.token.dao.AccessTokenDAO;
import com.nimble.security.oauth2.spring.provider.token.dao.RefreshTokenDAO;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collection;

/**
 * Date: 5/2/13
 * Time: 12:09 PM
 */
public class NimbleTokenStore implements TokenStore {
    private AccessTokenDAO accessTokenDAO;
    private RefreshTokenDAO refreshTokenDAO;
    private Oauth2AuthenticationManager<NimbleOAuth2Authentication> oauth2AuthenticationManager;
    private MessageDigest digest;

    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO;
    }

    public void setRefreshTokenDAO(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }

    public void setoAuth2AuthenticationDAO(Oauth2AuthenticationManager oauth2AuthenticationManager) {
        this.oauth2AuthenticationManager = oauth2AuthenticationManager;
    }

    public void setDigest(MessageDigest digest) {
        this.digest = digest;
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        throw new UnsupportedOperationException("Not yet implemented");
        //return oauth2AuthenticationManager.readAuthentication(token);
    }

    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        //make sure the authorization is up to date.  Will want to associate with auth with the token
        int authId = oauth2AuthenticationManager.getIdForOAuth2Authentication(authentication);
        accessTokenDAO.storeAccessToken(token, authId, authentication);
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken token = accessTokenDAO.readAccessToken(extractTokenKey(tokenValue));

        return token;
    }

    public void removeAccessToken(OAuth2AccessToken token) {
        accessTokenDAO.removeAccessToken(token);
    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        //make sure the authorization is up to date.  Will want to associate with auth with the token
        int authId = oauth2AuthenticationManager.getIdForOAuth2Authentication(authentication);
        refreshTokenDAO.storeRefreshToken(refreshToken, authId);
    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return refreshTokenDAO.readRefreshToken(extractTokenKey(tokenValue));
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        throw new UnsupportedOperationException("Not yet implemented");
        //return oauth2AuthenticationManager.readAuthentication(token.getValue());
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        refreshTokenDAO.removeRefreshToken(token);
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        accessTokenDAO.removeAccessTokenUsingRefreshToken(refreshToken);
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return accessTokenDAO.getAccessToken(authentication);
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        return accessTokenDAO.findTokensByUserName(userName);
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return accessTokenDAO.findTokensByClientId(clientId);
    }

    protected String extractTokenKey(String value) {

        if (digest != null) {
            if (value != null) {
        /*try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }*/

                try {
                    byte[] bytes = digest.digest(value.getBytes("UTF-8"));
                    value = String.format("%032x", new BigInteger(1, bytes));
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
                }
            }
        }
        return value;
    }
}
