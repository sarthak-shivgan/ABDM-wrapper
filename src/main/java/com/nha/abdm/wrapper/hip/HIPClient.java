/* (C) 2024 */
package com.nha.abdm.wrapper.hip;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HIPClient {

  @Value("${getPatientPath}")
  private String patientUrl;

  public HIPPatient getPatient(String patientId) {
    String url = patientUrl + "/" + patientId;

    WebClient.Builder webClientBuilder = WebClient.builder();
    ResponseEntity<HIPPatient> responseEntity =
        webClientBuilder.build().get().uri(url).retrieve().toEntity(HIPPatient.class).block();

    return responseEntity.getBody();
  }
}
