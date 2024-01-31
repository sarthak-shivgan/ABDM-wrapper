/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.NotifyHIURequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnFetchRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnInitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIUGatewayCallbackController {

  @Autowired private GatewayCallbackInterface gatewayCallback;

  @PostMapping({"/v0.5/consent-requests/on-init"})
  public ResponseEntity<GatewayCallbackResponse> onInitConsent(
      @RequestBody OnInitConsentRequest onInitConsentRequest) {
    return gatewayCallback.onInitConsent(onInitConsentRequest);
  }

  @PostMapping({"/v0.5/consent-requests/on-status"})
  public ResponseEntity<GatewayCallbackResponse> consentOnStatus(
      @RequestBody OnStatusRequest onStatusRequest) {
    return gatewayCallback.consentOnStatus(onStatusRequest);
  }

  @PostMapping({"/v0.5/consent/hiu/notify"})
  public ResponseEntity<GatewayCallbackResponse> onInitConsent(
      @RequestBody NotifyHIURequest notifyHIURequest) {
    return gatewayCallback.hiuNotify(notifyHIURequest);
  }

  @PostMapping({"/v0.5/consent/on-fetch"})
  public ResponseEntity<GatewayCallbackResponse> onInitConsent(
      @RequestBody OnFetchRequest onFetchRequest) {
    return gatewayCallback.consentOnFetch(onFetchRequest);
  }
}
