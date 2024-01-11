/* (C) 2024 */
package com.nha.abdm.wrapper.hrp.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class MakeRequest {

  public static ResponseEntity<ObjectNode> post(String uri, HttpEntity<ObjectNode> requestEntity) {
    WebClient.Builder webClientBuilder = WebClient.builder();
    ResponseEntity<ObjectNode> responseEntity =
        webClientBuilder
            .build()
            .post()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
            .body(BodyInserters.fromValue(requestEntity.getBody()))
            .retrieve()
            .toEntity(ObjectNode.class)
            .block();
    return responseEntity;
  }
}
