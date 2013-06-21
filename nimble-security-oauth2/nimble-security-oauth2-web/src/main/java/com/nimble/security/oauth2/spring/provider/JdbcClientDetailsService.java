package com.nimble.security.oauth2.spring.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.util.DefaultJdbcListFactory;
import org.springframework.security.oauth2.common.util.JdbcListFactory;
import org.springframework.security.oauth2.provider.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Basic, JDBC implementation of the client details service.
 */
public class JdbcClientDetailsService implements ClientDetailsService, ClientRegistrationService {
    private static final Log logger = LogFactory.getLog(JdbcClientDetailsService.class);
    private static final String CLIENT_FIELDS_FOR_UPDATE = "name, app_type, description, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information";
    private static final String CLIENT_FIELDS = "client_secret, " + CLIENT_FIELDS_FOR_UPDATE;
    private static final String BASE_FIND_STATEMENT = "select client_id, " + CLIENT_FIELDS
            + " from oauth_client_details";
    private static final String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";
    private static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";
    private static final String DEFAULT_INSERT_STATEMENT = "insert into oauth_client_details (" + CLIENT_FIELDS
            + ", client_id) values (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DEFAULT_UPDATE_STATEMENT = "update oauth_client_details " + "set "
            + CLIENT_FIELDS_FOR_UPDATE.replaceAll(", ", "=?, ") + "=? where client_id = ?";
    private static final String DEFAULT_UPDATE_SECRET_STATEMENT = "update oauth_client_details "
            + "set client_secret = ? where client_id = ?";
    private static final String DEFAULT_DELETE_STATEMENT = "delete from oauth_client_details where client_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private ObjectMapper mapper = new ObjectMapper();
    private RowMapper<NimbleClientDetails> rowMapper = new ClientDetailsRowMapper();
    private String deleteClientDetailsSql = DEFAULT_DELETE_STATEMENT;
    private String findClientDetailsSql = DEFAULT_FIND_STATEMENT;
    private String updateClientDetailsSql = DEFAULT_UPDATE_STATEMENT;
    private String updateClientSecretSql = DEFAULT_UPDATE_SECRET_STATEMENT;
    private String insertClientDetailsSql = DEFAULT_INSERT_STATEMENT;
    private String selectClientDetailsSql = DEFAULT_SELECT_STATEMENT;
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    private JdbcListFactory listFactory;

    public JdbcClientDetailsService(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.listFactory = new DefaultJdbcListFactory(new NamedParameterJdbcTemplate(jdbcTemplate));
    }

    /**
     * @param passwordEncoder the password encoder to set
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        if (logger.isDebugEnabled()) {
            logger.debug("loadClientByClientId: start: clientId=" + clientId);
        }
        NimbleClientDetails details;
        try {
            details = jdbcTemplate.queryForObject(selectClientDetailsSql, new ClientDetailsRowMapper(), clientId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("loadClientByClientId: end: clientId=" + clientId + ", details=" + details);
        }
        return details;
    }

    public void addClientDetails(ClientDetails cd) throws ClientAlreadyExistsException {
        if (logger.isDebugEnabled()) {
            logger.debug("addClientDetails: start: ClientDetails=" + cd);
        }
        int updated = -1;
        NimbleClientDetails clientDetails = (NimbleClientDetails) cd;

        try {
            updated = jdbcTemplate.update(insertClientDetailsSql, getFields(clientDetails));
        } catch (DuplicateKeyException e) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId(), e);
        }
        logger.info("addClientDetails: end: ClientDetails=" + cd + ", updated=" + updated);
    }

    public void updateClientDetails(ClientDetails cd) throws NoSuchClientException {
        logger.info("updateClientDetails: start: ClientDetails=" + cd);

        NimbleClientDetails clientDetails = (NimbleClientDetails) cd;
        int count = jdbcTemplate.update(updateClientDetailsSql, getFieldsForUpdate(clientDetails));
        if (count == 0) {
            throw new NoSuchClientException("No client found with id = " + clientDetails.getClientId());
        }
        logger.info("updateClientDetails: end: ClientDetails=" + cd + ", updated=" + count);
    }

    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        logger.info("updateClientSecret: start: clientId=" + clientId);
        int count = jdbcTemplate.update(updateClientSecretSql, passwordEncoder.encode(secret), clientId);
        if (count != 1) {
            logger.info("updateClientSecret: unexpected # rows updated: clientId=" + clientId + ", updated=" + count);
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
        logger.info("updateClientSecret: end: clientId=" + clientId + ", updated=" + count);
    }

    public void removeClientDetails(String clientId) throws NoSuchClientException {
        logger.info("removeClientDetails: start: clientId=" + clientId);
        int count = jdbcTemplate.update(deleteClientDetailsSql, clientId);
        if (count != 1) {
            logger.info("removeClientDetails: unexpected # rows updated: clientId=" + clientId + ", updated=" + count);
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
        logger.info("removeClientDetails: end: clientId=" + clientId + ", updated=" + count);
    }

    public List<ClientDetails> listClientDetails() {
        List<ClientDetails> cd = new ArrayList<ClientDetails>();
        cd.addAll(listFactory.getList(findClientDetailsSql, Collections.<String, Object>emptyMap(), rowMapper));
        return cd;
    }

    private Object[] getFields(NimbleClientDetails clientDetails) {
        Object[] fieldsForUpdate = getFieldsForUpdate(clientDetails);
        Object[] fields = new Object[fieldsForUpdate.length + 1];
        System.arraycopy(fieldsForUpdate, 0, fields, 1, fieldsForUpdate.length);
        fields[0] = clientDetails.getClientSecret() != null ? passwordEncoder.encode(clientDetails.getClientSecret())
                : null;
        return fields;
    }

    private Object[] getFieldsForUpdate(NimbleClientDetails clientDetails) {
        String json = null;
        try {
            json = mapper.writeValueAsString(clientDetails.getAdditionalInformation());
        } catch (Exception e) {
            logger.warn("Could not serialize additional information: " + clientDetails, e);
        }
        return new Object[]{
                clientDetails.getName(), clientDetails.getAppType(), clientDetails.getDescription(),
                clientDetails.getResourceIds() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails
                        .getResourceIds()) : null,
                clientDetails.getScope() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails
                        .getScope()) : null,
                clientDetails.getAuthorizedGrantTypes() != null ? StringUtils
                        .collectionToCommaDelimitedString(clientDetails.getAuthorizedGrantTypes()) : null,
                clientDetails.getRegisteredRedirectUri() != null ? StringUtils
                        .collectionToCommaDelimitedString(clientDetails.getRegisteredRedirectUri()) : null,
                clientDetails.getAuthorities() != null ? StringUtils.collectionToCommaDelimitedString(clientDetails
                        .getAuthorities()) : null, clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(), json, clientDetails.getClientId()};
    }

    public void setSelectClientDetailsSql(String selectClientDetailsSql) {
        this.selectClientDetailsSql = selectClientDetailsSql;
    }

    public void setDeleteClientDetailsSql(String deleteClientDetailsSql) {
        this.deleteClientDetailsSql = deleteClientDetailsSql;
    }

    public void setUpdateClientDetailsSql(String updateClientDetailsSql) {
        this.updateClientDetailsSql = updateClientDetailsSql;
    }

    public void setUpdateClientSecretSql(String updateClientSecretSql) {
        this.updateClientSecretSql = updateClientSecretSql;
    }

    public void setInsertClientDetailsSql(String insertClientDetailsSql) {
        this.insertClientDetailsSql = insertClientDetailsSql;
    }

    public void setFindClientDetailsSql(String findClientDetailsSql) {
        this.findClientDetailsSql = findClientDetailsSql;
    }

    /**
     * @param listFactory the list factory to set
     */
    public void setListFactory(JdbcListFactory listFactory) {
        this.listFactory = listFactory;
    }

    /**
     * @param rowMapper the rowMapper to set
     */
    public void setRowMapper(RowMapper<NimbleClientDetails> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * Row mapper for ClientDetails.
     *
     * @author Dave Syer
     */
    private static class ClientDetailsRowMapper implements RowMapper<NimbleClientDetails> {
        private ObjectMapper mapper = new ObjectMapper();

        public NimbleClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            NimbleClientDetails details = new NimbleClientDetails(rs.getString("client_id"), rs.getString("resource_ids"), rs.getString("scope"),
                    rs.getString("authorized_grant_types"), rs.getString("authorities"), rs.getString("web_server_redirect_uri"),
                    rs.getString("name"), rs.getString("app_type"), rs.getString("description"));
            details.setClientSecret(rs.getString("client_secret"));
            if (rs.getObject(8) != null) {
                details.setAccessTokenValiditySeconds(rs.getInt("access_token_validity"));
            }
            if (rs.getObject(9) != null) {
                details.setRefreshTokenValiditySeconds(rs.getInt("refresh_token_validity"));
            }
            String json = rs.getString("additional_information");
            if (json != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> additionalInformation = mapper.readValue(json, Map.class);
                    details.setAdditionalInformation(additionalInformation);
                } catch (Exception e) {
                    logger.warn("Could not decode JSON for additional information: " + details, e);
                }
            }
            return details;
        }
    }

}

