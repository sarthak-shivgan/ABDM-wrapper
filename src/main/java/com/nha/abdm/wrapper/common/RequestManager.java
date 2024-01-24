/* (C) 2024 */
package com.nha.abdm.wrapper.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RequestManager {

  private WebClient webClient;

  public RequestManager(
      @Value("${gatewayBaseUrl}") final String gatewayBaseUrl, SessionManager sessionManager) {
    webClient =
        WebClient.builder()
            .baseUrl(gatewayBaseUrl)
            .defaultHeaders(
                httpHeaders -> httpHeaders.addAll(sessionManager.setGatewayRequestHeaders()))
            .build();
  }

  public <T> ResponseEntity<ObjectNode> fetchResponseFromPostRequest(String uri, T request) {
    WebClient.Builder webClientBuilder = WebClient.builder();
    ResponseEntity<ObjectNode> responseEntity =
        webClient
            .post()
            .uri(uri)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .toEntity(ObjectNode.class)
            .block();
    return responseEntity;
  }
}
