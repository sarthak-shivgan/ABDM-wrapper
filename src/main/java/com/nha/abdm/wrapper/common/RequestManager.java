/* (C) 2024 */
package com.nha.abdm.wrapper.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RequestManager {

  @Autowired SessionManager sessionManager;

  public <T> ResponseEntity<ObjectNode> fetchResponseFromPostRequest(String uri, T request) {
    WebClient.Builder webClientBuilder = WebClient.builder();
    ResponseEntity<ObjectNode> responseEntity =
        webClientBuilder
            .build()
            .post()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.addAll(sessionManager.setGatewayRequestHeaders()))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .toEntity(ObjectNode.class)
            .block();
    return responseEntity;
  }
}
