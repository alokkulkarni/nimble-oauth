package com.nimble.security.oauth2.spring.provider.authentication.dao.sql;

import com.nimble.security.oauth2.spring.provider.authentication.NimbleOauth2VO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.SerializationUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Date: 5/6/13
 * Time: 2:20 PM
 */
public class OAuth2AuthenticationRowMapper implements RowMapper<NimbleOauth2VO> {
    public NimbleOauth2VO mapRow(ResultSet rs, int rowNum) throws SQLException {

        NimbleOauth2VO oAuth2Authentication = new NimbleOauth2VO(rs.getString("id"), /*rs.getString("client_authorization_id"),
                rs.getInt("user_authorization_id"),*/ rs.getBoolean("authenticated"), SerializationUtils.deserialize(rs.getBytes("details")));

        return oAuth2Authentication;
    }
}
