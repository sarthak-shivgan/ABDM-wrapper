/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.ConsentStatusRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.FetchConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.InitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.OnNotifyRequest;

public interface HIUConsentInterface {
  FacadeResponse initiateConsentRequest(InitConsentRequest initConsentRequest);

  FacadeResponse consentRequestStatus(ConsentStatusRequest consentStatusRequest);

  FacadeResponse hiuOnNotify(OnNotifyRequest onNotifyRequest);

  FacadeResponse fetchConsent(FetchConsentRequest fetchConsentRequest);
}
