package com.nimble.web.client;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * Date: 4/17/13
 * Time: 12:08 PM
 */
public class JsonCheckingResponseErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        super.handleError(response);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
