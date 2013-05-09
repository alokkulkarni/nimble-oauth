package com.nimble.security.oauth2.spring.provider.authentication.dao.sql;

import com.nimble.security.oauth2.spring.provider.IdAwareDefaultAuthorizationRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Date: 5/6/13
 * Time: 2:30 PM
 */
public class DefaultAuthRequestRowMapper implements RowMapper<IdAwareDefaultAuthorizationRequest> {
    public IdAwareDefaultAuthorizationRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        java.util.Map<String, String> authorizationParameters = SerializationUtils.deserialize(rs.getBytes("auth_params"));
        Map<String, String> approvalParameters = SerializationUtils.deserialize(rs.getBytes("approve_params"));
        String clientId = rs.getString("client_id");
        Collection<String> scope = StringUtils.commaDelimitedListToSet(rs.getString("scope"));
        IdAwareDefaultAuthorizationRequest authorizationRequest = new IdAwareDefaultAuthorizationRequest(authorizationParameters, approvalParameters, clientId, scope);
        authorizationRequest.setId(rs.getInt("id"));
        authorizationRequest.setApproved(rs.getBoolean("approved"));

        Set<String> authorities = StringUtils.commaDelimitedListToSet(rs.getString("authorities"));
        Collection<SimpleGrantedAuthority> authList = new ArrayList<SimpleGrantedAuthority>(authorities.size());
        for (String auth : authorities) {
            authList.add(new SimpleGrantedAuthority(auth));
        }
        authorizationRequest.setAuthorities(authList);
        authorizationRequest.setRedirectUri(rs.getString("redirect_uri"));
        authorizationRequest.setResourceIds(StringUtils.commaDelimitedListToSet(rs.getString("resource_ids")));
        return authorizationRequest;
    }
}
