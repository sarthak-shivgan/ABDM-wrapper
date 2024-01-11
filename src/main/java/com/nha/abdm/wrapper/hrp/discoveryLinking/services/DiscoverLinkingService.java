/* (C) 2024 */
package com.nha.abdm.wrapper.hrp.discoveryLinking.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import java.net.URISyntaxException;

public interface DiscoverLinkingService {
  void onDiscoverCall(DiscoverResponse data) throws URISyntaxException, JsonProcessingException;

  void onInitCall(InitResponse data) throws URISyntaxException, JsonProcessingException;

  void onConfirmCall(ConfirmResponse data) throws URISyntaxException, JsonProcessingException;
}
