package com.nimble.http.error;

import com.nimble.content.ContentModifier;
import com.nimble.http.filter.AbstractModelAndViewFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A filter that will simply catch all errors that happen upstream and convert to a response that can be formatted and returned to the caller elegantly
 * (rather than the default stacktrace etc)
 * Date: 4/26/13
 * Time: 11:44 AM
 */
public class OutputErrorFormatFilter extends AbstractModelAndViewFilter {
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private List<ContentModifier> errorMessageMods = null;
    private String contentType;
    private String viewName = "unexpected_error";

    public void setErrorMessageMods(List<ContentModifier> errorMessageMods) {
        this.errorMessageMods = errorMessageMods;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Throwable e) {
            handleError((HttpServletResponse) servletResponse, request, e);
        }
    }

    private void handleError(HttpServletResponse servletResponse, HttpServletRequest request, Throwable e) throws ServletException {
        //todo: figure out what else should be logged
        logger.error("Unexpected error occurred: Returning 500 error.  request:" + request.getRequestURL(), e);
        Map<String, String> response = new HashMap<String, String>();
        response.put("error_description", "An unexpected internal server error occurred.  Please try again.");
        response.put("error", "500");

        try {
            render(response, viewName, request, servletResponse);
        } catch (Exception e1) {
            logger.error("Error rendering the view for the caught error.", e1);
            throw new ServletException(e1);
        }
    }
}
