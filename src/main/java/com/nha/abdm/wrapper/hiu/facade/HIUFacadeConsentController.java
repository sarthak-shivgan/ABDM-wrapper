/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.facade;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.HIUConsentInterface;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.ConsentStatusRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.FetchConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.InitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.OnNotifyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIUFacadeConsentController {

  @Autowired private HIUConsentInterface hiuConsentInterface;

  @PostMapping({"/consent-init"})
  public FacadeResponse initiateConsentRequest(@RequestBody InitConsentRequest initConsentRequest) {
    return hiuConsentInterface.initiateConsentRequest(initConsentRequest);
  }

  @PostMapping({"/consent-status"})
  public FacadeResponse consentRequestStatus(
      @RequestBody ConsentStatusRequest consentStatusRequest) {
    return hiuConsentInterface.consentRequestStatus(consentStatusRequest);
  }

  @PostMapping({"/hiu-on-notify"})
  public FacadeResponse hiuOnNotify(@RequestBody OnNotifyRequest onNotifyRequest) {
    return hiuConsentInterface.hiuOnNotify(onNotifyRequest);
  }

  @PostMapping({"/fetch-consent"})
  public FacadeResponse fetchConsent(@RequestBody FetchConsentRequest fetchConsentRequest) {
    return hiuConsentInterface.fetchConsent(fetchConsentRequest);
  }
}
