/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.FetchPatientConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.InitConsentRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.OnNotifyRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.responses.ConsentResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.responses.ConsentStatusResponse;

public interface HIUConsentInterface {
  FacadeResponse initiateConsentRequest(InitConsentRequest initConsentRequest);

  ConsentStatusResponse consentRequestStatus(String clientRequestId)
      throws IllegalDataStateException;

  void hiuOnNotify(OnNotifyRequest onNotifyRequest);

  ConsentResponse fetchConsent(FetchPatientConsentRequest fetchPatientConsentRequest)
      throws IllegalDataStateException;
}
