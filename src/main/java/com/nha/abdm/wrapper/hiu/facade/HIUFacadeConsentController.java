/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.facade;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.HIUConsentInterface;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.InitConsentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIUFacadeConsentController {

  @Autowired private HIUConsentInterface hiuConsentInterface;

  @PostMapping({"/consents-init"})
  public FacadeResponse initiateConsentRequest(@RequestBody InitConsentRequest initConsentRequest) {
    return hiuConsentInterface.initiateConsentRequest(initConsentRequest);
  }
}
