/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.ConsentStatusRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.FetchConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.InitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.OnNotifyRequest;
import org.springframework.http.ResponseEntity;

public interface HIUConsentInterface {
  ResponseEntity<FacadeResponse> initiateConsentRequest(InitConsentRequest initConsentRequest);

  ResponseEntity<FacadeResponse> consentRequestStatus(ConsentStatusRequest consentStatusRequest);

  ResponseEntity<FacadeResponse> hiuOnNotify(OnNotifyRequest onNotifyRequest);

  ResponseEntity<FacadeResponse> fetchConsent(FetchConsentRequest fetchConsentRequest);
}
