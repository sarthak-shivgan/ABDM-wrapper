package com.nha.abdm.wrapper.hrp.common;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:application.properties"})
public class GatewayApiPaths {
    @Value("${baseUrl}")
    public String baseUrl;
    public static String CREATE_SESSION;
    public static String LINK_AUTH_INIT;
    public static String LINK_CONFIRM_AUTH;
    public static String ON_DISCOVER;
    public static String ON_INIT;
    public static String ON_CONFIRM;
    public static String LINK_ADD_CARE_CONTEXT;
    @PostConstruct
    public void init() {
        CREATE_SESSION = baseUrl + "/gateway/v0.5/sessions";
        LINK_AUTH_INIT =baseUrl+"/gateway/v0.5/users/auth/init";
        LINK_CONFIRM_AUTH =baseUrl+"/gateway/v0.5/users/auth/confirm";
        ON_DISCOVER =baseUrl+"/gateway/v0.5/care-contexts/on-discover";
        ON_INIT =baseUrl+"/gateway/v0.5/links/link/on-init";
        ON_CONFIRM =baseUrl+"/gateway/v0.5/links/link/on-confirm";
        LINK_ADD_CARE_CONTEXT =baseUrl+"/gateway/v0.5/links/link/add-contexts";
    }


}
