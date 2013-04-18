package com.nimble.mvc;

import com.nimble.security.core.userdetails.NimbleUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.net.URI;
import java.util.*;

/**
 *
 */
@Controller
public class ProxyController {
    protected Log logger = LogFactory.getLog(getClass());
    private Set<String> headersToStrip;
    private Set<String> forwardHeadersToStrip;
    private String targetDomain;
	private RestOperations restOperations;

    public ProxyController() {
        headersToStrip = new HashSet<String>();
        forwardHeadersToStrip = new HashSet<String>();
        headersToStrip.add("Content-Encoding");
        //want to strip authorization since changing the method
        forwardHeadersToStrip.add("authorization");
        setHeadersToStrip(headersToStrip);
    }

    public void setHeadersToStrip(Set<String> headersToStrip) {
        this.headersToStrip = headersToStrip;
    }

    @RequestMapping("/api/v1/**")
	public ResponseEntity<ObjectNode> proxy(HttpServletRequest request, Model model) throws Exception {
        String orig = request.getQueryString();

        String url = targetDomain + request.getServletPath() + "?" + orig;
        logger.debug("URL: "+url);

        //extract the request body - assuming only string data being to be supported.
        BufferedReader bufferedReader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        HttpEntity<String> reqEntity = new HttpEntity<String>(sb.toString(), extractHeaders(request));
        ResponseEntity<ObjectNode> responseEntity = null;
        URI uri = new URI(url);
        responseEntity = restOperations.exchange(uri, HttpMethod.valueOf(request.getMethod()), reqEntity, ObjectNode.class);
        //now need to omit certain headers that no longer apply
        MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<String, String>();
        for(Map.Entry<String, List<String>> entry : responseEntity.getHeaders().entrySet()) {
            if(!headersToStrip.contains(entry.getKey())) {
                newHeaders.put(entry.getKey(), entry.getValue());
            }
        }

        ResponseEntity<ObjectNode> proxyResp = new ResponseEntity<ObjectNode>(responseEntity.getBody(), newHeaders, responseEntity.getStatusCode());

		return proxyResp;
	}

	public void setRestOperations(RestTemplate restOperations) {
		this.restOperations = restOperations;

	}

    public void setTargetDomain(String targetDomain) {
        this.targetDomain = targetDomain;
    }

    protected HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            if(!forwardHeadersToStrip.contains(headerName.toLowerCase())) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }
        //need to add the Nimble token header
        SecurityContext ctx = SecurityContextHolder.getContext();
        if(ctx != null) {
            //should not be here if this is null;
            Authentication auth = ctx.getAuthentication();
            NimbleUser user = (NimbleUser) auth.getPrincipal();
            if(user != null) {
                //it should never be the case that we do not get here.  Checks are done via security configuration
                headers.add("Authorization", "Nimble token=\""+user.getNimbleToken()+"\"");
            }
        }
        return headers;
    }
}
