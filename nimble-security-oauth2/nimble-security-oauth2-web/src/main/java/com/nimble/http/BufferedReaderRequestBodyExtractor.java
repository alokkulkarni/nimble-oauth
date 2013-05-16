package com.nimble.http;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Date: 4/25/13
 * Time: 4:35 PM
 */
public class BufferedReaderRequestBodyExtractor implements RequestBodyExtractor {
    public String extractBody(ServletRequest request) throws IOException {
        BufferedReader bufferedReader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
