package com.nimble.web.client;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Date: 4/17/13
 * Time: 12:35 PM
 */
public class IgnoreResponseErrorHandler implements ResponseErrorHandler {
    public boolean hasError(ClientHttpResponse response) throws IOException {
        //ignoring all errors
        return false;
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        //should never get here as we never have errors
        throw new UnsupportedOperationException("This class does not implement handleError as hasError is always false");
    }
}
