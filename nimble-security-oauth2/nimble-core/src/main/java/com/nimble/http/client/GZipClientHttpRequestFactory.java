package com.nimble.http.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Date: 4/15/13
 * Time: 3:15 PM
 */
public class GZipClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    protected Log log = LogFactory.getLog(getClass());

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("createRequest: start: uri=" + uri + ", method=" + httpMethod);
        }
        HttpURLConnection connection = openConnection(uri.toURL(), null);
        prepareConnection(connection, httpMethod.name());
        return new GzipClientHttpRequest(connection);

    }


}
