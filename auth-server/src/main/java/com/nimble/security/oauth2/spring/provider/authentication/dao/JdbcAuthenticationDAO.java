package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.oauth2.spring.provider.authentication.NimbleAuthentication;
import com.nimble.security.oauth2.spring.provider.authentication.dao.sql.DefaultAuthenticationRowMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 5/8/13
 * Time: 3:44 PM
 */
public class JdbcAuthenticationDAO implements AuthenticationDAO {
    private final JdbcTemplate jdbcTemplate;
    protected Log log = LogFactory.getLog(getClass());
    //private SimpleJdbcInsert insert;
    private RowMapper<? extends Authentication> authenticationRowMapper = new DefaultAuthenticationRowMapper();
    private String selectSql = "select * from oauth2_authentication where oauth2AuthenticationId=?";

    private String insertSql = "INSERT INTO oauth2_authentication (oauth2AuthenticationId, username, authenticated, principal, nimble_token, " +
            "authorities) VALUES (?,?,?,?,?,?)";
    private String updateSql = "UPDATE oauth2_authentication set username=?, authenticated=?, principal=?, nimble_token=?, " +
            "authorities=? WHERE oauth2AuthenticationId=?";

    public JdbcAuthenticationDAO(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        /*this.insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("oauth2_authentication");
        insert.setColumnNames(Arrays.asList("username", "authenticated", "principal", "nimble_token"));
        insert.setGeneratedKeyName("id");
        insert.compile();*/
    }

    /*public void setInsert(SimpleJdbcInsert insert) {
        this.insert = insert;
    }*/

    public void setAuthenticationRowMapper(RowMapper<? extends Authentication> authenticationRowMapper) {
        this.authenticationRowMapper = authenticationRowMapper;
    }

    public Authentication readAuthentication(String id) {
        return jdbcTemplate.queryForObject(selectSql, authenticationRowMapper, id);
    }

    public int storeAuthentication(Authentication authentication) {
        String id = null;
        int update = -1;
        if (authentication != null) {
            id = getId(authentication);

            if (id != null) {
                log.debug("Creating authentication: " + authentication);

                try {
                    update = jdbcTemplate.update(insertSql, getInsertValues(id, authentication), getInsertFieldTypes());
                    if (update != 1) {
                        log.warn("storeAuthentication: Unexpected create count: " + update + ", authentication: " + authentication);
                    }
                } catch (DuplicateKeyException dke) {
                    //already exists, do an update
                    log.debug("Updating authorizationRequest: " + id);
                    update = jdbcTemplate.update(updateSql, getUpdateValues(id, authentication), getUpdateFieldTypes());
                    if (update != 1) {
                        log.warn("storeAuthentication: Unexpected updated count: " + update + ", authentication: " + authentication);
                    }
                }
            } else {
                log.error("No id found for Authentication.  ID must always be provided for saving. " + authentication);
            }
        }
        return update;
    }

    //TODO: everything Nimble specific should be in a subclass
    protected String getId(Authentication authentication) {
        return ((NimbleAuthentication)authentication).getOauth2AuthorizationId();
    }


    protected Object[] getUpdateValues(String id, Authentication authentication) {
        Set<String> authorities = getAuthorities(authentication);
        NimbleAuthentication auth = (NimbleAuthentication)authentication;
        return new Object[]{auth.getName(), auth.isAuthenticated(), SerializationUtils.serialize(auth.getPrincipal()),
                auth.getNimbleToken(), authorities, id
        };
    }

    protected int[] getUpdateFieldTypes() {
        return new int[]{Types.VARCHAR, Types.TINYINT, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
    }

    protected Object[] getInsertValues(String id, Authentication authentication) {
        Set<String> authorities = getAuthorities(authentication);
        NimbleAuthentication auth = (NimbleAuthentication)authentication;
        return new Object[]{id, auth.getName(), auth.isAuthenticated(), SerializationUtils.serialize(auth.getPrincipal()),
                auth.getNimbleToken(), authorities
        };
    }

    protected int[] getInsertFieldTypes() {
        return new int[]{Types.VARCHAR, Types.VARCHAR, Types.TINYINT, Types.BLOB, Types.VARCHAR, Types.VARCHAR};
    }

    private Set<String> getAuthorities(Authentication authentication) {
        Set<String> authorities = new HashSet<String>();
        for (GrantedAuthority ga : authentication.getAuthorities()) {
            authorities.add(ga.getAuthority());
        }
        return authorities;
    }


}
