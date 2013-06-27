package com.nimble.test;

import com.nimble.security.oauth2.spring.provider.token.dao.JdbcRefreshTokenDAO;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Date: 4/10/13
 * Time: 2:12 PM
 */
public class UnitTest {
    public static void main(String[] args) {

        FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext("file:/Users/brandonnimble/projects/Nimble/nimble-oauth/unittesting/src/testCtx.xml");


        /*NimbleJdbcAuthorizationRequestDAO authReqDao = (NimbleJdbcAuthorizationRequestDAO) ctx.getBean("authReq");
        Map<String, String> authParam = new HashMap<String, String>();
        authParam.put("login", "bvallade");
        Map<String, String> approvalParam = new HashMap<String, String>();
        approvalParam.put("auth", "yup");
        NimbleAuthorizationRequest ar = new NimbleAuthorizationRequest(authParam, approvalParam, "nimbletest", Arrays.asList("ROLE_USER"));
        ar.setRedirectUri("http://redirect.to.me.com");
        ar.setApproved(true);
        ar.setResourceIds(new HashSet<String>(Arrays.asList("resource1", "resource2")));
        ar.setScope(new HashSet<String>(Arrays.asList("read", "write")));
        authReqDao.storeAuthorizationRequest(ar);*/
        ApigeeConverter.importApigeeKeys(ctx);
        JdbcRefreshTokenDAO dao = (JdbcRefreshTokenDAO) ctx.getBean("refreshTokenDAO");
        DefaultExpiringOAuth2RefreshToken t = new DefaultExpiringOAuth2RefreshToken("afadqwdaqdq", new Date());
        //dao.storeRefreshToken(t, 4);

        //manualHttp();
        RestTemplate rt = new RestTemplate();
        //want to use the default message converters and add the form one.
        List<HttpMessageConverter<?>> converters = rt.getMessageConverters();
        converters.add(0, new org.springframework.http.converter.FormHttpMessageConverter());
        rt.setMessageConverters(converters);
        ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod)
                    throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(false);
            }
        };
        rt.setRequestFactory(requestFactory);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.add("email", "brandon.vallade@gmail.com");
        params.add("password", "bone19991");
        params.add("is_persistent", "1");
        //Object o = rt.postForEntity("https://app.nimble.com/api/sessions?email={email}&password={password}&is_persistent={is_persistent}", params, Map.class, params);
        try {
            Object o = rt.postForEntity("https://app.nimble.com/api/sessions", params, Map.class, params);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
        return;
    }

    private static void manualHttp() {
        try {

            String urlParameters = "email=" + URLEncoder.encode("brandon.vallade@gmail.com", "UTF-8") + "&password=" + URLEncoder.encode("bone1999", "UTF-8");
            //String request = "https://app.nimble.com/api/sessions";
            String request = "https://app.nimble.com/api/sessions?" + urlParameters;


            URL myurl = new URL(request);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-length", String.valueOf(urlParameters.length()));
            //con.setRequestProperty("Content-length", "0");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);
            con.setDoInput(true);

            DataOutputStream output = new DataOutputStream(con.getOutputStream());
            output.writeBytes(urlParameters);
            output.close();

            DataInputStream input = new DataInputStream(con.getInputStream());
            for (int c = input.read(); c != -1; c = input.read())
                System.out.print((char) c);
            input.close();

            System.out.println("Resp Code:" + con.getResponseCode());
            System.out.println("Resp Message:" + con.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
