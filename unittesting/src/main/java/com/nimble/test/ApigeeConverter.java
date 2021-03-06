package com.nimble.test;

import com.nimble.security.core.userdetails.NimbleUser;
import com.nimble.security.oauth2.spring.common.NimbleRefreshToken;
import com.nimble.security.oauth2.spring.provider.NimbleOAuth2Authentication;
import com.nimble.security.oauth2.spring.provider.authentication.NimbleOauth2AuthenticationManager;
import com.nimble.security.oauth2.spring.provider.token.NimbleTokenStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class ApigeeConverter {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: com.nimble.test.ApigeeConverter [application context file path]");
            return;
        }
        String path = args[1];
        System.out.println("Attempting to load ctx from " + path);
        FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext("file:" + path);
        importApigeeKeys(ctx);
    }

    static void importApigeeKeys(ApplicationContext ctx) {
        DataSource ds = (DataSource) ctx.getBean("oAuthDataSource");
        NimbleOauth2AuthenticationManager authenticationManager = (NimbleOauth2AuthenticationManager) ctx.getBean("oauth2AuthenticationManager");
        NimbleTokenStore tokenStore = (NimbleTokenStore) ctx.getBean("nimbleTokenStore");
        JdbcTemplate template = new JdbcTemplate(ds);
        OauthImportDataDAO dao = new OauthImportDataDAO(template);
        List<ImportValues> values = dao.getImportData();


        DefaultAuthorizationRequest authorizationRequest;
        UsernamePasswordAuthenticationToken userAuthentication;
        OAuth2Authentication authentication;

        for (ImportValues row : values) {
            //first, the authorization request
            Map<String, String> authorizationParameters = new HashMap<String, String>();
            authorizationParameters.put("client_secret", row.getSecret());
            authorizationParameters.put("redirect_uri", row.getRedirectUri());
            authorizationParameters.put("response_type", "code");
            authorizationParameters.put("grant_type", "authorization_code");
            authorizationParameters.put("code", row.getAuthCode());
            String clientId = row.getClientId();
            Collection<String> scope = Arrays.asList("read", "write");
            List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT"));

            authorizationRequest = new DefaultAuthorizationRequest(clientId, scope);
            authorizationRequest.setAuthorities(authorities);
            authorizationRequest.setApproved(true);
            authorizationRequest.setAuthorizationParameters(authorizationParameters);
            authorizationRequest.setRedirectUri(row.getRedirectUri());

            //now the user authentication
            Collection<? extends GrantedAuthority> auth = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

            NimbleUser principal = new NimbleUser(row.getUserId(), "", true, true, true, true, auth, row.getNimbleToken());
            principal.eraseCredentials();
            Object credentials = null;

            userAuthentication = new UsernamePasswordAuthenticationToken(principal, credentials, auth);
            authentication = new OAuth2Authentication(authorizationRequest, userAuthentication);
            NimbleOAuth2Authentication nimbleOAuth2Authentication = authenticationManager.storeOAuth2Authentication(authentication);
            //now the refresh token
            NimbleRefreshToken refreshToken = new NimbleRefreshToken(row.getRefreshToken(), row.getExpiration(), nimbleOAuth2Authentication.getAuthenticationId());
            refreshToken.setTimesUsed(row.getUsedCount());
            tokenStore.storeRefreshToken(refreshToken, nimbleOAuth2Authentication);

        }


    }

    private static class OauthImportDataDAO {
        private String dbName = "apigeerailsdb";
        private JdbcTemplate template;
        private String sql = "SELECT" +
                "    o_two_refresh_tokens.user_attribute1," +  //nimble_token
                "    o_two_refresh_tokens.refresh_token," +
                "    o_two_refresh_tokens.userid," +
                "    keys.secret," +
                "    keys.key," +  //clientid
                "    client_application_label_values.value," +  //redirect_uri
                "    o_two_refresh_tokens.auth_code," +
                "    o_two_refresh_tokens.validitytimestamp," +
                "    o_two_refresh_tokens.used_count" +
                " FROM" +
                "    " + dbName + ".o_two_refresh_tokens," +
                "    " + dbName + ".client_applications," +
                "    " + dbName + ".keys," +
                "    " + dbName + ".client_application_label_values" +
                " WHERE" +
                "    " + dbName + ".o_two_refresh_tokens.clientid = " + dbName + ".keys.key" +
                " AND " + dbName + ".keys.client_application_id = " + dbName + ".client_applications.id" +
                " AND " + dbName + ".client_applications.id =" +
                "    " + dbName + ".client_application_label_values.client_application_id ;";

        private OauthImportDataDAO(JdbcTemplate template) {
            this.template = template;
        }

        public List<ImportValues> getImportData() {
            return template.query(sql, new Object[]{}, new RowMapper<ImportValues>() {
                @Override
                public ImportValues mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new ImportValues(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getTimestamp(8), rs.getInt(9));
                }
            });
        }
    }

    private static class ImportValues {
        private String nimbleToken;
        private String refreshToken;
        private String userId;
        private String secret;
        private String clientId;
        private String redirectUri;
        private String authCode;
        private Timestamp expiration;
        private int usedCount;

        private ImportValues(String nimbleToken, String refreshToken, String userId, String secret, String clientId, String redirectUri, String authCode, Timestamp expiration, int usedCount) {
            this.nimbleToken = nimbleToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
            this.secret = secret;
            this.clientId = clientId;
            this.redirectUri = redirectUri;
            this.authCode = authCode;
            this.expiration = expiration;
            this.usedCount = usedCount;
        }

        private String getNimbleToken() {
            return nimbleToken;
        }

        private String getRefreshToken() {
            return refreshToken;
        }

        private String getUserId() {
            return userId;
        }

        private String getSecret() {
            return secret;
        }

        private String getRedirectUri() {
            return redirectUri;
        }

        private String getClientId() {
            return clientId;
        }

        private String getAuthCode() {
            return authCode;
        }

        private Timestamp getExpiration() {
            return expiration;
        }

        private int getUsedCount() {
            return usedCount;
        }
    }


}