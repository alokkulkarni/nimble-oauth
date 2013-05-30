package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.oauth2.spring.provider.authentication.dao.sql.DefaultAuthRequestRowMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 5/8/13
 * Time: 9:59 AM
 */
public abstract class JdbcAuthorizationRequestDAO implements AuthorizationRequestDAO {
    private final JdbcTemplate jdbcTemplate;
    protected Log log = LogFactory.getLog(getClass());
    //private SimpleJdbcInsert insert;
    private String selectSql = "SELECT * from oauth2_authorization_request where id=?";
    private String insertSql = "INSERT INTO oauth2_authorization_request (id, client_id, approved, scope, resource_ids, " +
            "authorities, redirect_uri, auth_params, approve_params) VALUES (?,?,?,?,?,?,?,?,?)";
    private String updateSql = "UPDATE oauth2_authorization_request set client_id=?, approved=?, scope=?, resource_ids=?, " +
            "authorities=?, redirect_uri=?, auth_params=?, approve_params=? WHERE id=?";
    private RowMapper<? extends AuthorizationRequest> authRequestRowMapper = new DefaultAuthRequestRowMapper();


    public JdbcAuthorizationRequestDAO(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        /*this.insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("oauth2_authorization_request");
        insert.setColumnNames(Arrays.asList("client_id", "approved", "scope", "resource_ids", "authorities", "redirect_uri", "auth_params", "approve_params"));
        insert.setGeneratedKeyName("id");
        insert.compile();*/
    }

    public AuthorizationRequest getAuthorizationRequest(String requestId) {
        AuthorizationRequest auth = null;

        try {
            auth = jdbcTemplate.queryForObject(selectSql, authRequestRowMapper, requestId);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.debug("Failed to find AuthorizationRequest for id " + requestId);
            }
        } catch (IllegalArgumentException e) {
            log.error("Could not extract AuthorizationRequest for id " + requestId);
        }

        return auth;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }

    public void setUpdateSql(String updateSql) {
        this.updateSql = updateSql;
    }

    public void setAuthRequestRowMapper(RowMapper<? extends AuthorizationRequest> authRequestRowMapper) {
        this.authRequestRowMapper = authRequestRowMapper;
    }

    public String storeAuthorizationRequest(AuthorizationRequest authorizationRequest) {
        String id = null;
        if (authorizationRequest != null) {
            id = getId(authorizationRequest);

            if (id != null) {
                log.debug("Creating authorizationRequest: " + authorizationRequest);


            /*MapSqlParameterSource params = new MapSqlParameterSource();
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
            }*/

                try {
                    int update = jdbcTemplate.update(insertSql, getInsertValues(id, authorizationRequest), getInsertFieldTypes());
                    if (update != 1) {
                        log.warn("storeAuthorizationRequest: Unexpected create count: " + update + ", authorizationRequest: " + authorizationRequest);
                    }
                } catch (DuplicateKeyException dke) {
                    //already exists, do an update
                    log.debug("Updating authorizationRequest: " + id);
                    int update = jdbcTemplate.update(updateSql, getUpdateValues(id, authorizationRequest), getUpdateFieldTypes());
                    if (update != 1) {
                        log.warn("storeAuthorizationRequest: Unexpected updated count: " + update + ", authorizationRequest: " + authorizationRequest);
                    }
                }
            } else {
                log.error("No id found for AuthorizationRequest.  ID must always be provided for saving.");
            }
        }
        return id;
    }

    private Set<String> getAuthorities(AuthorizationRequest authorizationRequest) {
        Set<String> authorities = new HashSet<String>();
        for (GrantedAuthority ga : authorizationRequest.getAuthorities()) {
            authorities.add(ga.getAuthority());
        }
        return authorities;
    }

    protected abstract String getId(AuthorizationRequest request);

    protected Object[] getUpdateValues(String id, AuthorizationRequest authorizationRequest) {
        Set<String> authorities = getAuthorities(authorizationRequest);
        return new Object[]{authorizationRequest.getClientId(),
                authorizationRequest.isApproved(), StringUtils.collectionToCommaDelimitedString(authorizationRequest.getScope()),
                StringUtils.collectionToCommaDelimitedString(authorizationRequest.getResourceIds()), StringUtils.collectionToCommaDelimitedString(authorities),
                authorizationRequest.getRedirectUri(), SerializationUtils.serialize(authorizationRequest.getAuthorizationParameters()),
                SerializationUtils.serialize(authorizationRequest.getApprovalParameters()), id
        };
    }

    protected int[] getUpdateFieldTypes() {
        return new int[]{Types.VARCHAR, Types.TINYINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.BLOB, Types.VARCHAR};
    }

    protected Object[] getInsertValues(String id, AuthorizationRequest authorizationRequest) {
        Set<String> authorities = getAuthorities(authorizationRequest);
        return new Object[]{id, authorizationRequest.getClientId(),
                authorizationRequest.isApproved(), StringUtils.collectionToCommaDelimitedString(authorizationRequest.getScope()),
                StringUtils.collectionToCommaDelimitedString(authorizationRequest.getResourceIds()), StringUtils.collectionToCommaDelimitedString(authorities),
                authorizationRequest.getRedirectUri(), SerializationUtils.serialize(authorizationRequest.getAuthorizationParameters()),
                SerializationUtils.serialize(authorizationRequest.getApprovalParameters())
        };
    }

    protected int[] getInsertFieldTypes() {
        return new int[]{Types.VARCHAR, Types.VARCHAR, Types.TINYINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.BLOB};
    }
}
