/* (C) 2024 */
package com.nha.abdm.wrapper.common;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.DataPushRequest;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.GatewayGenericResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RequestManager {
  private static final Logger log = LogManager.getLogger(RequestManager.class);

  private final WebClient webClient;
  private final WebClient hiuWebClient;

  @Autowired
  public RequestManager(
      @Value("${gatewayBaseUrl}") final String gatewayBaseUrl, SessionManager sessionManager) {
    webClient =
        WebClient.builder()
            .baseUrl(gatewayBaseUrl)
            .defaultHeaders(
                httpHeaders -> httpHeaders.addAll(sessionManager.setGatewayRequestHeaders()))
            .build();
    hiuWebClient = WebClient.builder().build();
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

  public ResponseEntity<GatewayGenericResponse> postToHIU(
      String dataPushUrl, DataPushRequest dataPushRequest) {
    ResponseEntity<GatewayGenericResponse> responseEntity =
        hiuWebClient
            .post()
            .uri(dataPushUrl)
            .body(BodyInserters.fromValue(dataPushRequest))
            .retrieve()
            .toEntity(GatewayGenericResponse.class)
            .block();
    log.info("correlation-id: " + responseEntity.getHeaders().getFirst("correlation-id"));
    return responseEntity;
  }
}
