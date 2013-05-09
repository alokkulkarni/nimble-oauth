package com.nimble.security.oauth2.spring.provider.authentication.dao.sql;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Date: 5/6/13
 * Time: 2:20 PM
 */
public class OAuth2AuthenticationRowMapper implements RowMapper<OAuth2Authentication> {
    private RowMapper<? extends AuthorizationRequest> authorizationRequestMapper = new DefaultAuthRequestRowMapper();

    private RowMapper<? extends Authentication> userAuthenticationMapper = new RowMapper<Authentication>() {
        public Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorizationRequest authReq = authorizationRequestMapper.mapRow(rs, rowNum);
        Authentication auth = userAuthenticationMapper.mapRow(rs, rowNum);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(authReq, auth);
        oAuth2Authentication.setAuthenticated(rs.getBoolean("authenticated"));
        oAuth2Authentication.setDetails(rs.getBlob("details"));
        return oAuth2Authentication;
    }
}
