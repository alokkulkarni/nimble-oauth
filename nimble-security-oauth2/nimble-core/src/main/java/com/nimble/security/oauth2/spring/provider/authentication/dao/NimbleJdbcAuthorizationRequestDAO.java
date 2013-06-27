package com.nimble.security.oauth2.spring.provider.authentication.dao;

import com.nimble.security.oauth2.spring.provider.NimbleAuthorizationRequest;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 5/8/13
 * Time: 2:55 PM
 */
public class NimbleJdbcAuthorizationRequestDAO extends JdbcAuthorizationRequestDAO {
    //private AuthorizationRequestKeyGenerator authorizationRequestKeyGenerator = new NimbleAuthorizationRequestKeyGenerator();
    private String insertSql = "INSERT INTO oauth2_authorization_request (id, client_id, approved, scope, resource_ids, " +
            "authorities, redirect_uri, auth_params, approve_params, username) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private String updateSql = "UPDATE oauth2_authorization_request set username=?, client_id=?, approved=?, scope=?, resource_ids=?, " +
            "authorities=?, redirect_uri=?, auth_params=?, approve_params=? WHERE id=?";

    public NimbleJdbcAuthorizationRequestDAO(DataSource dataSource) {
        super(dataSource);
        setInsertSql(insertSql);
        setUpdateSql(updateSql);
    }

    @Override
    protected String getId(AuthorizationRequest request) {
        String id = null;
        try {
            NimbleAuthorizationRequest req = (NimbleAuthorizationRequest) request;
            id = req.getAuthorizationId();
        } catch (ClassCastException cce) {
            //just return -1 -- does not exist

        }
        return id;
    }

    @Override
    protected Object[] getUpdateValues(String id, AuthorizationRequest authorizationRequest) {
        List values = new ArrayList();
        values.add(((NimbleAuthorizationRequest) authorizationRequest).getUserName());
        values.addAll(Arrays.asList(super.getUpdateValues(id, authorizationRequest)));
        return values.toArray();
    }

    @Override
    protected int[] getUpdateFieldTypes() {
        //add the username type (VARCHAR) to the beginning to match sql
        int[] types = super.getUpdateFieldTypes();
        int[] modified = new int[types.length + 1];
        System.arraycopy(types, 0, modified, 1, types.length);
        modified[0] = Types.VARCHAR;
        return modified;

    }

    @Override
    protected Object[] getInsertValues(String id, AuthorizationRequest authorizationRequest) {
        //add the username to the end to match sql
        List values = new ArrayList(Arrays.asList(super.getInsertValues(id, authorizationRequest)));
        values.add(((NimbleAuthorizationRequest) authorizationRequest).getUserName());
        return values.toArray();
    }

    @Override
    protected int[] getInsertFieldTypes() {
        //add the username type (VARCHAR) to the end to match sql
        int[] types = super.getInsertFieldTypes();
        int[] modified = new int[types.length + 1];
        System.arraycopy(types, 0, modified, 0, types.length);
        modified[types.length] = Types.VARCHAR;
        return modified;
    }
}
