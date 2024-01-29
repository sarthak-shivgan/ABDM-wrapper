/* (C) 2024 */
package com.nha.abdm.wrapper.hip;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HIPClient {

  @Value("${getPatientPath}")
  private String patientUrl;

  private WebClient webClient;

  public HIPClient(@Value("${getPatientPath}") final String baseUrl) {
    webClient =
        WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public HIPPatient getPatient(String patientId) {
    String url = "/" + patientId;
    ResponseEntity<HIPPatient> responseEntity =
        webClient.get().uri(url).retrieve().toEntity(HIPPatient.class).block();

    return responseEntity.getBody();
  }
}
