/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.NotifyHIURequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnFetchRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnInitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayCallbackService implements GatewayCallbackInterface {
  @Override
  public ResponseEntity<GatewayCallbackResponse> onInitConsent(
      OnInitConsentRequest onInitConsentRequest) {
    // TODO: Implement on init workflow logic.
    return null;
  }

  @Override
  public ResponseEntity<GatewayCallbackResponse> consentOnStatus(OnStatusRequest onStatusRequest) {
    // TODO: Implement consent on status workflow logic.
    return null;
  }

  @Override
  public ResponseEntity<GatewayCallbackResponse> hiuNotify(NotifyHIURequest notifyHIURequest) {
    // TODO: Implement hiu notify workflow logic.
    return null;
  }

  @Override
  public ResponseEntity<GatewayCallbackResponse> consentOnFetch(OnFetchRequest onFetchRequest) {
    // TODO: Implement consent on fetch workflow logic.
    return null;
  }
}
