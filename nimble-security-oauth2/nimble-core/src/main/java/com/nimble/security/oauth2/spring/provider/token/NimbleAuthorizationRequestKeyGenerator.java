package com.nimble.security.oauth2.spring.provider.token;

import com.nimble.security.oauth2.spring.provider.NimbleAuthorizationRequest;
import org.springframework.security.oauth2.common.util.OAuth2Utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic key generator taking into account the client id, scope, reource ids and username (principal name) and other field
 * from the NimbleOAuth2Authentication if they exist.
 */
public class NimbleAuthorizationRequestKeyGenerator implements AuthorizationRequestKeyGenerator<NimbleAuthorizationRequest> {
    private static final String CLIENT_ID = "client_id";

    private static final String SCOPE = "scope";

    private static final String USERNAME = "username";

    private static final String REDIRECT_URI = "username";


    public String extractKey(NimbleAuthorizationRequest authorizationRequest) {
        Map<String, String> values = new LinkedHashMap<String, String>();

        values.put(CLIENT_ID, authorizationRequest.getClientId());
        values.put(USERNAME, authorizationRequest.getUserName());
        if (authorizationRequest.getScope() != null) {
            values.put(SCOPE, OAuth2Utils.formatParameterList(authorizationRequest.getScope()));
        }
        values.put(REDIRECT_URI, authorizationRequest.getRedirectUri());
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }
}
