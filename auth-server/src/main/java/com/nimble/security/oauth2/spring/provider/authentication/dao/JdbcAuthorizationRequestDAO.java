package com.nimble.security.oauth2.spring.provider.authentication.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 5/8/13
 * Time: 9:59 AM
 */
public abstract class JdbcAuthorizationRequestDAO implements AuthorizationRequestDAO {
    private final JdbcTemplate jdbcTemplate;
    protected Log log = LogFactory.getLog(getClass());

    private SimpleJdbcInsert insert;
    //private RowMapper<? extends OAuth2Authentication> authenticationTokenMapper = null;
    //private String authenticationFieldSelect = "select a.id as auth_id, a.client_authorization_id, a.user_authorization_id, a.authenticated, a.authorities, a.details" +
    //        " from oauth2_authorization a";
    /*private String selectAuthenticationByAccessTokenSql = authenticationFieldSelect + " INNER JOIN oauth2_access_token oat on a.id = oat.authentication_id where oat.access_token = ?";
    private String createAuthorizationRequestSql = "insert into oauth2_authorization_request (client_id, approved, scope, resource_ids, authorities, redirect_uri, auth_params, approve_params) VALUES (?,?,?,?,?,?,?,?)";
    private String updateAuthorizationRequestSql = "update oauth2_authorization_request SET client_id=?, approved=?, scope=?, resource_ids=?, authorities=?, redirect_uri=?, auth_params=?, approve_params=? where id=?";
    */


    public JdbcAuthorizationRequestDAO(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("oauth2_authorization_request");
        insert.setColumnNames(Arrays.asList("client_id", "approved", "scope", "resource_ids", "authorities", "redirect_uri", "auth_params", "approve_params"));
        insert.setGeneratedKeyName("id");
        insert.compile();
    }

    public AuthorizationRequest getAuthorizationRequest(AuthorizationRequest request) {
        return null;
    }

    public int storeAuthorizationRequest(AuthorizationRequest authorizationRequest) {
        int id = getId(authorizationRequest);
        if (id <= 0) {
            log.debug("Creating authorizationRequest: " + authorizationRequest);

            Set<String> authorities = new HashSet<String>();
            for (GrantedAuthority ga : authorizationRequest.getAuthorities()) {
                authorities.add(ga.getAuthority());
            }
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("client_id", authorizationRequest.getClientId(), Types.VARCHAR);
            params.addValue("approved", authorizationRequest.isApproved(), Types.TINYINT);
            params.addValue("scope", StringUtils.collectionToCommaDelimitedString(authorizationRequest.getScope()), Types.VARCHAR);
            params.addValue("resource_ids", StringUtils.collectionToCommaDelimitedString(authorizationRequest.getResourceIds()), Types.VARCHAR);
            params.addValue("authorities", StringUtils.collectionToCommaDelimitedString(authorities), Types.VARCHAR);
            params.addValue("redirect_uri", authorizationRequest.getRedirectUri(), Types.VARCHAR);
            params.addValue("auth_params", SerializationUtils.serialize(authorizationRequest.getAuthorizationParameters()), Types.BLOB);
            params.addValue("approve_params", SerializationUtils.serialize(authorizationRequest.getApprovalParameters()), Types.BLOB);

            java.lang.Number n = insert.executeAndReturnKey(params);
            if (n != null) {
                id = n.intValue();
            } else {
                log.error("storeAuthorizationRequest: Expected a return ID from insert but none received!");
            }

            /*KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(createAuthorizationRequestSql, new Object[]{authorizationRequest.getClientId(),
                    authorizationRequest.isApproved(), StringUtils.collectionToCommaDelimitedString(authorizationRequest.getScope()),
                    StringUtils.collectionToCommaDelimitedString(authorizationRequest.getResourceIds()), StringUtils.collectionToCommaDelimitedString(authorities),
                    authorizationRequest.getRedirectUri(), SerializationUtils.serialize(authorizationRequest.getAuthorizationParameters()),
                    SerializationUtils.serialize(authorizationRequest.getApprovalParameters())
            }, new int[]{Types.VARCHAR, Types.TINYINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.BLOB});
*/
        } else {
            log.debug("Updating authorizationRequest: " + id);
        }
        return id;
    }

    protected abstract int getId(AuthorizationRequest request);
}
