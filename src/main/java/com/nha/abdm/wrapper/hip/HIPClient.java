/* (C) 2024 */
package com.nha.abdm.wrapper.hip;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationBundle;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationBundleRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HIPClient {

  @Value("${hipBaseUrl}")
  private String hipBaseUrl;

  @Value("${getPatientPath}")
  private String patientPath;

  @Value("${getHealthInformationPath}")
  private String getHealthInformationPath;

  private WebClient webClient;

  public HIPClient(@Value("${hipBaseUrl}") final String baseUrl) {
    webClient =
        WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public HIPPatient getPatient(String patientId) {
    ResponseEntity<HIPPatient> responseEntity =
        webClient.get().uri(patientPath).retrieve().toEntity(HIPPatient.class).block();

    return responseEntity.getBody();
  }

  public ResponseEntity<HealthInformationBundle> healthInformationBundleRequest(
      HealthInformationBundleRequest healthInformationBundleRequest) {
    return webClient
        .post()
        .uri(getHealthInformationPath)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(healthInformationBundleRequest))
        .retrieve()
        .toEntity(HealthInformationBundle.class)
        .block();
  }
}
