package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.core.userdetails.NimbleUser;
import com.nimble.security.oauth2.spring.provider.authentication.IdAwareOAuth2Authentication;
import com.nimble.security.oauth2.spring.provider.authentication.dao.sql.DefaultAuthenticationRowMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 5/8/13
 * Time: 3:44 PM
 */
public class JdbcAuthenticationDAO implements AuthenticationDAO {
    private final JdbcTemplate jdbcTemplate;
    protected Log log = LogFactory.getLog(getClass());
    private SimpleJdbcInsert insert;
    private RowMapper<? extends Authentication> authenticationRowMapper = new DefaultAuthenticationRowMapper();
    private String selectSql = "select * from oauth2_authentication where id=?";

    public JdbcAuthenticationDAO(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("oauth2_authentication");
        insert.setColumnNames(Arrays.asList("username", "authenticated", "principal", "nimble_token"));
        insert.setGeneratedKeyName("id");
        insert.compile();
    }

    public void setInsert(SimpleJdbcInsert insert) {
        this.insert = insert;
    }

    public void setAuthenticationRowMapper(RowMapper<? extends Authentication> authenticationRowMapper) {
        this.authenticationRowMapper = authenticationRowMapper;
    }

    public Authentication readAuthentication(int id) {
        return jdbcTemplate.queryForObject(selectSql, authenticationRowMapper, id);
    }

    public int storeAuthentication(Authentication authentication) {
        int id = getId(authentication);
        if (id <= 0) {
            log.debug("Creating storeAuthentication: " + authentication);


            Set<String> authorities = new HashSet<String>();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                authorities.add(ga.getAuthority());
            }
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("username", authentication.getName(), Types.VARCHAR);
            params.addValue("authenticated", authentication.isAuthenticated(), Types.TINYINT);
            params.addValue("principal", SerializationUtils.serialize(authentication.getPrincipal()), Types.BLOB);
            params.addValue("nimble_token", getNimbleToken(authentication), Types.VARCHAR);


            java.lang.Number n = insert.executeAndReturnKey(params);
            if (n != null) {
                id = n.intValue();
            } else {
                log.error("storeAuthentication: Expected a return ID from insert but none received!");
            }


        } else {
            log.debug("Updating storeAuthentication: " + id);
            //TODO
        }
        return id;
    }

    protected String getNimbleToken(Authentication authentication) {
        NimbleUser user = (NimbleUser) authentication.getPrincipal();
        return user.getNimbleToken();
    }

    protected int getId(Authentication authentication) {
        int id = -1;
        try {
            IdAwareOAuth2Authentication req = (IdAwareOAuth2Authentication) authentication;
            id = req.getId();
        } catch (ClassCastException cce) {
            //just return -1 -- does not exist

        }
        return id;
    }


}
