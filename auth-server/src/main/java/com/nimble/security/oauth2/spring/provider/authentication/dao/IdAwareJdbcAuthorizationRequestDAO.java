package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.oauth2.spring.provider.IdAwareDefaultAuthorizationRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

import javax.sql.DataSource;

/**
 * Date: 5/8/13
 * Time: 2:55 PM
 */
public class IdAwareJdbcAuthorizationRequestDAO extends JdbcAuthorizationRequestDAO {
    public IdAwareJdbcAuthorizationRequestDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected int getId(AuthorizationRequest request) {
        int id = -1;
        try {
            IdAwareDefaultAuthorizationRequest req = (IdAwareDefaultAuthorizationRequest)request;
            id = req.getId();
        } catch(ClassCastException cce) {
            //just return -1 -- does not exist

        }
        return id;
    }

}
