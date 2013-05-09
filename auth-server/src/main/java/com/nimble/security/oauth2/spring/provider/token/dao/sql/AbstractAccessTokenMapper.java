package com.nimble.security.oauth2.spring.provider.token.dao.sql;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAccessTokenMapper<N extends DefaultOAuth2AccessToken> implements RowMapper<N> {
    public N mapRow(ResultSet rs, int rowNum) throws SQLException {
        N token = getTokenInstance(rs);
        token.setAdditionalInformation((Map<String, Object>) rs.getBlob("additional_info"));
        token.setExpiration(rs.getDate("expiration"));
        //token.setRefreshToken();
        String scopes = rs.getString("scope");
        if (StringUtils.hasText(scopes)) {
            Set<String> scopeList = StringUtils.commaDelimitedListToSet(scopes);
            if (!scopeList.isEmpty()) {
                token.setScope(scopeList);
            }
        }
        token.setTokenType(rs.getString("token_type"));
        //create simple refreshtoken that can be used to be extended upon in post processing up the chain as necessary
        token.setRefreshToken(new DefaultOAuth2RefreshToken(rs.getString("refresh_token")));
        return token;
    }

    protected abstract N getTokenInstance(ResultSet rs) throws SQLException;
}