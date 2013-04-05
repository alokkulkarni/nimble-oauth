package com.nimble.security.oauth.mvc;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Controller
public class ProxyController {
    private String targetDomain;
	private RestOperations restOperations;

	@RequestMapping("/api/v1/**")
	public String photos(HttpServletRequest request, Model model) throws Exception {
        String url = targetDomain + request.getServletPath();
		ObjectNode result = restOperations.getForObject(url, ObjectNode.class);

		model.addAttribute("data", result);
		return "nimble-api";
	}

	public void setRestOperations(OAuth2RestTemplate restOperations) {
		this.restOperations = restOperations;
	}

    public void setTargetDomain(String targetDomain) {
        this.targetDomain = targetDomain;
    }
}
