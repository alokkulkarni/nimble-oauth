package com.nimble.security.oauth2.spring.provider.token.dao;

import com.nimble.security.oauth2.spring.common.NimbleRefreshToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Date: 5/8/13
 * Time: 5:04 PM
 */
public class JdbcRefreshTokenDAO implements RefreshTokenDAO {
    private final JdbcTemplate jdbcTemplate;
    protected Log log = LogFactory.getLog(getClass());
    private String insertSql = "insert into oauth2_refresh_token (expiration, authentication_id, times_used, refresh_token) values (?,?,?,?)";
    private String updateSql = "update oauth2_refresh_token set expiration=?, authentication_id=?, times_used=?, refresh_token=? where id=?";
    private String deleteSql = "delete from oauth2_refresh_token where refresh_token=?";
    private String selectSql = "select * from oauth2_refresh_token where refresh_token=?";
    private RefreshTokenRowMapper mapper = new RefreshTokenRowMapper();

    public JdbcRefreshTokenDAO(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, String authId) {
        if (log.isDebugEnabled()) {
            log.debug("storeRefreshToken: start: authId=" + authId + ", token=" + refreshToken.getValue());
        }
        int updated = -1;
        NimbleRefreshToken token = createRefreshToken(refreshToken, authId);
        if (token.getId() <= 0) {
            log.info("storeRefreshToken: insert: authId=" + authId + ", token=" + refreshToken.getValue());
            //need to have an ID to avoid going to the DB to see if the token exists or doing an insert upon
            //failed update.
            //going to create.  The refresh token value should be unique so may want to do a delete here to make sure it
            //is clean.  This *could* cause a fk problem.  Otherwise we *could* have a unique key problem
            updated = jdbcTemplate.update(insertSql, new Object[]{token.getExpiration(), token.getAuthenticationId(), token.getTimesUsed(), token.getValue()},
                    new int[]{Types.TIMESTAMP, Types.VARCHAR, Types.INTEGER, Types.VARCHAR});
        } else {
            log.info("storeRefreshToken: update: authId=" + authId + ", token=" + refreshToken.getValue());
            updated = jdbcTemplate.update(updateSql, new Object[]{token.getExpiration(), token.getAuthenticationId(), token.getTimesUsed(), token.getValue(), token.getId()},
                    new int[]{Types.TIMESTAMP, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.INTEGER});
        }

        log.info("storeRefreshToken: end: authId=" + authId + ", token=" + refreshToken.getValue()+", updated="+updated);

    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        //will return a NimbleRefreshToken (RowMapper)
        OAuth2RefreshToken auth = null;
        if (log.isDebugEnabled()) {
            log.info("readRefreshToken: start: tokenValue=" + tokenValue);
        }

        try {
            auth = jdbcTemplate.queryForObject(selectSql, mapper, tokenValue);
        } catch (EmptyResultDataAccessException e) {
            log.info("Failed to find OAuth2RefreshToken for id " + tokenValue);
        } catch (IllegalArgumentException e) {
            log.error("Could not extract OAuth2RefreshToken for id " + tokenValue);
        }
        if (log.isDebugEnabled()) {
            log.info("readRefreshToken: end: tokenValue=" + tokenValue + ", retVal=" + auth);
        }
        return auth;
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        log.info("removeRefreshToken: start: token=" + token);
        int deleted = jdbcTemplate.update(deleteSql, token.getValue());
        log.info("removeRefreshToken: end: token=" + token + ", deleted=" + deleted);
    }

    protected int getId(OAuth2RefreshToken refreshToken) {
        if (refreshToken instanceof NimbleRefreshToken) {
            return ((NimbleRefreshToken) refreshToken).getId();
        } else {
            return -1;
        }
    }

    private NimbleRefreshToken createRefreshToken(OAuth2RefreshToken base, String authId) {
        if (base instanceof NimbleRefreshToken) {
            return (NimbleRefreshToken) base;
        } else {
            NimbleRefreshToken t = new NimbleRefreshToken(base);
            t.setAuthenticationId(authId);
            return t;
        }
    }

    private static class RefreshTokenRowMapper implements RowMapper<NimbleRefreshToken> {

        public NimbleRefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            NimbleRefreshToken token = new NimbleRefreshToken(rs.getString("refresh_token"), rs.getTimestamp("expiration"), rs.getString("authentication_id"));
            token.setId(rs.getInt("id"));
            token.setTimesUsed(rs.getInt("times_used"));
            return token;
        }
    }
}
