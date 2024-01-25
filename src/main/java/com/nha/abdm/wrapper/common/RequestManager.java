/* (C) 2024 */
package com.nha.abdm.wrapper.common;

import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.GatewayGenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RequestManager {

  private final WebClient webClient;

  @Autowired
  public RequestManager(
      @Value("${gatewayBaseUrl}") final String gatewayBaseUrl, SessionManager sessionManager) {
    webClient =
        WebClient.builder()
            .baseUrl(gatewayBaseUrl)
            .defaultHeaders(
                httpHeaders -> httpHeaders.addAll(sessionManager.setGatewayRequestHeaders()))
            .build();
  }

  public <T> ResponseEntity<GatewayGenericResponse> fetchResponseFromGateway(
      String uri, T request) {
    return webClient
        .post()
        .uri(uri)
        .body(BodyInserters.fromValue(request))
        .retrieve()
        .toEntity(GatewayGenericResponse.class)
        .block();
  }
}
