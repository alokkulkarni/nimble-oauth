package com.nimble.security.oauth2.spring.provider.token.dao;

import com.nimble.security.oauth2.spring.common.NimbleRefreshToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private String insertSql = "insert into oauth2_refresh_token expiration, authentication_id, times_used, refresh_token values (?,?,?,?)";
    private String updateSql = "update oauth2_refresh_token set expiration=?, authentication_id=?, times_used=?, refresh_token=? where id=?";
    private String deleteSql = "delete from oauth2_refresh_token where refresh_token=?";
    private String selectSql = "select * from oauth2_refresh_token where refresh_token=?";
    private RefreshTokenRowMapper mapper = new RefreshTokenRowMapper();

    public JdbcRefreshTokenDAO(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, int authId) {

        NimbleRefreshToken token = new NimbleRefreshToken(refreshToken);
        if (token.getId() <= 0) {
            //need to have an ID to avoid going to the DB to see if the token exists or doing an insert upon
            //failed update.
            //going to create.  The refresh token value should be unique so may want to do a delete here to make sure it
            //is clean.  This *could* cause a fk problem.  Otherwise we *could* have a unique key problem
            jdbcTemplate.update(insertSql, new Object[]{token.getExpiration(), token.getAuthenticationId(), token.getTimesUsed(), token.getValue()},
                    new Object[]{Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR});
        } else {
            jdbcTemplate.update(updateSql, new Object[]{token.getExpiration(), token.getAuthenticationId(), token.getTimesUsed(), token.getValue(), token.getId()},
                    new Object[]{Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER});
        }

    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        //will return a NimbleRefreshToken (RowMapper)
        return jdbcTemplate.queryForObject(selectSql, mapper, tokenValue);
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        jdbcTemplate.update(deleteSql, token.getValue());
    }

    protected int getId(OAuth2RefreshToken refreshToken) {
        if (refreshToken instanceof NimbleRefreshToken) {
            return ((NimbleRefreshToken) refreshToken).getId();
        } else {
            return -1;
        }
    }

    private static class RefreshTokenRowMapper implements RowMapper<NimbleRefreshToken> {

        public NimbleRefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            NimbleRefreshToken token = new NimbleRefreshToken(rs.getString("refresh_token"), rs.getDate("expiration"), rs.getInt("authentication_id"));
            token.setId(rs.getInt("id"));
            token.setTimesUsed(rs.getInt("times_used"));
            return token;
        }
    }
}
