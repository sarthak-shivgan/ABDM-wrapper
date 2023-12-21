package com.nha.abdm.wrapper.hrp.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource({"classpath:application.properties"})
public class GatewayApi {
//    @Value("${baseUrl}")
    public String baseUrl="https://dev.abdm.gov.in";

    public  String createSession=baseUrl+"/gateway/v0.5/sessions";
    public  String link_authInit=baseUrl+"/gateway/v0.5/users/auth/init";
    public  String link_ConfirmAuth=baseUrl+"/gateway/v0.5/users/auth/confirm";
    public  String onDiscover=baseUrl+"/gateway/v0.5/care-contexts/on-discover";
    public  String onInit=baseUrl+"/gateway/v0.5/links/link/on-init";
    public  String onConfirm=baseUrl+"/gateway/v0.5/links/link/on-confirm";
    public  String link_addCareContext=baseUrl+"/gateway/v0.5/links/link/add-contexts";

}
