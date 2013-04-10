package com.nimble.security.oauth.provider.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 *
 */
public class TokenRequiredFilter  implements Filter {
    protected Log logger = LogFactory.getLog(getClass());
    private FilterConfig filterConfig;

    public TokenRequiredFilter() {
    }

    protected String parseToken(HttpServletRequest request) {
        // first check the header...
        String token = parseHeaderToken(request);

        // bearer type allows a request parameter as well
        if (token == null) {
            logger.debug("Token not found in headers. Trying request parameters.");
            token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
            if (token == null) {
                logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
            }
        }

        return token;
    }

    /**
     * Parse the OAuth header parameters. The parameters will be oauth-decoded.
     *
     * @param request The request.
     * @return The parsed parameters, or null if no OAuth authorization header was supplied.
     */
    protected String parseHeaderToken(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Enumeration<String> headers = request.getHeaders("Authorization");
        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
                String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
            else {
                // todo: support additional authorization schemes for different token types, e.g. "MAC" specified by
                // http://tools.ietf.org/html/draft-hammer-oauth-v2-mac-token
            }
        }

        return null;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = parseToken((HttpServletRequest)servletRequest);
        if(token == null) {
            throw new InvalidTokenException("No access token found");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
