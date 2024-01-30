/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.NotifyHIURequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnFetchRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnInitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnStatusRequest;
import org.springframework.http.ResponseEntity;

public interface GatewayCallbackInterface {
  ResponseEntity<GatewayCallbackResponse> onInitConsent(OnInitConsentRequest onInitConsentRequest);

  ResponseEntity<GatewayCallbackResponse> consentOnStatus(OnStatusRequest onStatusRequest);

  ResponseEntity<GatewayCallbackResponse> hiuNotify(NotifyHIURequest notifyHIURequest);

  ResponseEntity<GatewayCallbackResponse> consentOnFetch(OnFetchRequest onFetchRequest);
}
