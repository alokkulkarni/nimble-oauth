package com.nimble.security.oauth2.spring.provider.token.dao.sql;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Date: 5/6/13
 * Time: 10:29 AM
 */
public class DefaultAccessTokenMapper extends AbstractAccessTokenMapper<DefaultOAuth2AccessToken> {
    private String accessTokenField = "access_token";

    public void setAccessTokenField(String accessTokenField) {
        this.accessTokenField = accessTokenField;
    }

    @Override
    protected DefaultOAuth2AccessToken getTokenInstance(ResultSet rs) throws SQLException {
        return new DefaultOAuth2AccessToken(rs.getString(accessTokenField));
    }
}
