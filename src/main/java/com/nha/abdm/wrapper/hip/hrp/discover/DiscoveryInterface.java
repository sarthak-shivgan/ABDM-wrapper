/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.discover;

import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.DiscoverRequest;
import org.springframework.http.ResponseEntity;

public interface DiscoveryInterface {
  ResponseEntity<GatewayCallbackResponse> discover(DiscoverRequest discoverRequest);
}
