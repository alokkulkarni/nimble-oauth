package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.oauth2.spring.provider.NimbleOAuth2Authentication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;

/**
 * Date: 5/6/13
 * Time: 1:51 PM
 */
public class JdbcNimbleOAuth2AuthenticationDAO implements OAuth2AuthenticationDAO<NimbleOAuth2Authentication> {
    private final JdbcTemplate jdbcTemplate;
    protected Log log = LogFactory.getLog(getClass());
    private SimpleJdbcInsert insert;
    private RowMapper<? extends OAuth2Authentication> authenticationTokenMapper = null;
    private String authenticationFieldSelect = "select a.id as auth_id, a.client_authorization_id, a.user_authorization_id, a.authenticated, a.authorities, a.details" +
            " from oauth2_authorization a";
    private String selectAuthenticationByAccessTokenSql = authenticationFieldSelect + " INNER JOIN oauth2_access_token oat on a.id = oat.authentication_id where oat.access_token = ?";


    public JdbcNimbleOAuth2AuthenticationDAO(DataSource dataSource, RowMapper authenticationTokenMapper) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("oauth2_authorization");
        insert.setColumnNames(Arrays.asList("client_authorization_id", "user_authorization_id", "authenticated", "details"));
        insert.setGeneratedKeyName("id");
        insert.compile();

        this.authenticationTokenMapper = authenticationTokenMapper;
    }

    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication auth = null;

        try {
            auth = jdbcTemplate.queryForObject(selectAuthenticationByAccessTokenSql, authenticationTokenMapper, token);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.debug("Failed to find access token for access token " + token);
            }
        } catch (IllegalArgumentException e) {
            log.error("Could not extract access token for access token " + token);
        }


        return auth;
    }

    public int storeOAuth2Authentication(NimbleOAuth2Authentication authentication) {

        int id = authentication.getId();
        if (id <= 0) {
            log.debug("Creating storeAuthentication: " + authentication);

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("client_authorization_id", authentication.getClientRequestId(), Types.INTEGER);
            params.addValue("user_authorization_id", authentication.getUserAuthId(), Types.INTEGER);
            params.addValue("authenticated", authentication.isAuthenticated(), Types.TINYINT);
            params.addValue("details", SerializationUtils.serialize(authentication.getDetails()), Types.BLOB);

            java.lang.Number n = insert.executeAndReturnKey(params);
            if (n != null) {
                id = n.intValue();
            } else {
                log.error("storeAuthentication: Expected a return ID from insert but none received!");
            }
        } else {
            log.debug("Updating storeAuthentication: " + id);
        }
        return id;
    }


}
