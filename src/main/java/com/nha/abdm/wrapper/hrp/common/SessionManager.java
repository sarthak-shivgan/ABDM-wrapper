package com.nha.abdm.wrapper.hrp.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import com.nha.abdm.wrapper.hrp.services.CommonServices;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import java.net.URISyntaxException;

@Data
@Component
public class SessionManager {
     @Autowired
     @Lazy
     CommonServices commonServices;
     @Autowired
     ApplicationConfig applicationConfig;

    public String accessToken;

    public String fetchAccessToken() throws URISyntaxException, JsonProcessingException {
        if(accessToken==null){
            return commonServices.startSession();
        }
        return accessToken;
    }
    public HttpHeaders initialiseHeadersForGateway() throws URISyntaxException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-CM-ID",applicationConfig.environment);
        headers.add("Authorization",this.fetchAccessToken());
        return headers;
    }

}
