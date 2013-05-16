package com.nimble.security.oauth2.spring.provider.authentication.dao.sql;

import com.nimble.security.oauth2.spring.provider.authentication.NimbleAuthentication;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Date: 5/6/13
 * Time: 2:31 PM
 */
public class DefaultAuthenticationRowMapper implements RowMapper<NimbleAuthentication> {
    public NimbleAuthentication mapRow(ResultSet rs, int rowNum) throws SQLException {
        Set<String> auths = StringUtils.commaDelimitedListToSet(rs.getString("authorities"));
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for(String role : auths) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        NimbleAuthentication clientAuthentication = new NimbleAuthentication(rs.getString("oauth2AuthenticationId"),
                rs.getString("username"), rs.getBoolean("authenticated"), SerializationUtils.deserialize(rs.getBytes("principal")),
                rs.getString("nimble_token"), authorities);



        return clientAuthentication;
    }
}
