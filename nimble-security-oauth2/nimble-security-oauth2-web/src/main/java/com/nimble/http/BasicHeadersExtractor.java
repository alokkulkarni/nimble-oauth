package com.nimble.http;

import com.nimble.content.ContentModifier;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Date: 4/25/13
 * Time: 5:03 PM
 */
public class BasicHeadersExtractor implements HeadersExtractor {
    private List<ContentModifier> modifiers = null;


    public void setModifiers(List<ContentModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public Map<String, List<String>> extractHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            List<String> headerValues = headers.get(headerName);
            if (headerValues == null) {
                headerValues = new LinkedList<String>();
                headers.put(headerName, headerValues);
            }
            headerValues.add(headerValue);
        }

        if (modifiers != null) {
            for (ContentModifier modifier : modifiers) {
                headers = modifier.modify(headers);
            }
        }

        return headers;
    }
}
