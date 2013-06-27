package com.nimble.security.oauth2.spring.provider;

import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;

/**
 * Date: 6/19/13
 * Time: 4:09 PM
 */
public class NimbleClientDetails extends BaseClientDetails {
    private String name;

    private String appType;

    private String description;

    public NimbleClientDetails(String name, String appType, String description) {
        this.name = name;
        this.appType = appType;
        this.description = description;
    }

    public NimbleClientDetails(ClientDetails prototype, String name, String appType, String description) {
        super(prototype);
        this.name = name;
        this.appType = appType;
        this.description = description;
    }

    public NimbleClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, String authorities, String name, String appType, String description) {
        super(clientId, resourceIds, scopes, grantTypes, authorities);
        this.name = name;
        this.appType = appType;
        this.description = description;
    }

    public NimbleClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, String authorities, String redirectUris, String name, String appType, String description) {
        super(clientId, resourceIds, scopes, grantTypes, authorities, redirectUris);
        this.name = name;
        this.appType = appType;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
