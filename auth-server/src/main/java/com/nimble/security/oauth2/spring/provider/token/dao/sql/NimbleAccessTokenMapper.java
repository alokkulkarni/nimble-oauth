package com.nimble.security.oauth2.spring.provider.token.dao.sql;

import com.nimble.security.oauth2.spring.common.NimbleAccessToken;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Date: 5/6/13
 * Time: 10:29 AM
 */
public class NimbleAccessTokenMapper extends AbstractAccessTokenMapper<NimbleAccessToken> {
    private String accessTokenField = "access_token";

    public void setAccessTokenField(String accessTokenField) {
        this.accessTokenField = accessTokenField;
    }

    @Override
    protected NimbleAccessToken getTokenInstance(ResultSet rs) throws SQLException {
        return new NimbleAccessToken(rs.getString(accessTokenField));
    }

    @Override
    public NimbleAccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        NimbleAccessToken token = super.mapRow(rs, rowNum);
        token.setEncrypted(rs.getBoolean("is_encrypted"));
        token.setCreated(rs.getDate("created_date"));
        token.setUpdated(rs.getDate("updated_date"));
        token.setId(rs.getInt("id"));
        return token;
    }
}
