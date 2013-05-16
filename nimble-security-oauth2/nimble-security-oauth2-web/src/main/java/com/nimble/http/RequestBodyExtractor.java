package com.nimble.http;

import javax.servlet.ServletRequest;
import java.io.IOException;

/**
 * Date: 4/25/13
 * Time: 4:33 PM
 */
public interface RequestBodyExtractor {
    String extractBody(ServletRequest request) throws IOException;
}
