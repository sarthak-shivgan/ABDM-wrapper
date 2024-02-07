/* (C) 2024 */
package com.nha.abdm.wrapper.hip;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationPushRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HIUClient {

  public void pushHealthInformation(
      String datPushURl, HealthInformationPushRequest healthInformationPushRequest) {
    WebClient webClient =
        WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    webClient
        .post()
        .uri(datPushURl)
        .body(BodyInserters.fromValue(healthInformationPushRequest))
        .retrieve()
        .toEntity(Void.class)
        .block();
  }
}
