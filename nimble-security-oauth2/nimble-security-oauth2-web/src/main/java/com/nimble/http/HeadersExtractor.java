package com.nimble.http;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Date: 4/25/13
 * Time: 5:02 PM
 */
public interface HeadersExtractor {
    Map<String, List<String>> extractHeaders(HttpServletRequest request);
}
