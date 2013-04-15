package com.nimble.mvc;

import com.nimble.security.core.userdetails.NimbleUser;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.Map;

/**
 *
 */
@Controller
public class ProxyController {
    private String targetDomain;
	private RestOperations restOperations;

    public ProxyController() {

    }

	@RequestMapping("/api/v1/**")
	public String photos(HttpServletRequest request, Model model) throws Exception {
        String url = targetDomain + request.getServletPath();

        //extract the request body - assuming only string data being to be supported.
        BufferedReader bufferedReader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        HttpEntity<String> reqEntity = new HttpEntity<String>(sb.toString(), extractHeaders(request));

        ResponseEntity<ObjectNode> responseEntity = restOperations.exchange(url, HttpMethod.valueOf(request.getMethod()), reqEntity, ObjectNode.class);
        model.addAttribute("data", responseEntity);
		return "nimble-api";
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
            headers.add(headerName, request.getHeader(headerName));
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
